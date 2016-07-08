/*
 * Copyright 2016 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.codecs

import java.io.Closeable
import scala.util.Try

trait ResourceIterator[+A] extends TraversableOnce[A] with Closeable { self ⇒
  // - Abstract methods ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  protected def readNext(): A
  protected def checkNext: Boolean
  protected def release(): Unit



  // - Internal state handling -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  private var isClosed = false

  @inline
  private def doClose(): Unit = {
    isClosed = true

    // In the current version, closing can't fail, since it can occur in hasNext.
    // This is not ideal, and I intend to fix that if I ever come up with a workable solution.
    Try(release())
  }



  // - Iterator methods ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  final def close(): Unit = if(!isClosed) doClose()

  final def hasNext: Boolean =
  // We need to check for emptiness here in order to deal with a very specific case: the underlying data was empty
  // from the beginning. Under these circumstances, `next` will never be called, and we need to call `close()` on
  // `hasNext`. This is not ideal, but the only other alternative would be to check for emptiness at creation time and
  // close then, which would end up ignoring calls to `withClose`.
    if(isClosed)       false
    else if(checkNext) true
    else {
      doClose()
      false
    }

  final def next(): A =
    try {
      val n = readNext()
      hasNext
      n
    }
    catch {
      case e: Throwable ⇒
        close()
        throw e
    }



  // - Useful methods --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def drop(n: Int): ResourceIterator[A] =
    if(n > 0 && hasNext) {
      next()
      drop(n - 1)
    }
    else this

  def dropWhile(p: A ⇒ Boolean): ResourceIterator[A] =
  // Empty rows: nothing to drop
    if(isEmpty) this
    else {
      // Looks for the first element that does not match p.
      var n = self.next()
      while(self.hasNext && p(n)) n = self.next()

      // No such element, return the empty stream.
      if(isEmpty && p(n)) this

      // We've found one such element, return a new iterator that starts with it.
      else new ResourceIterator[A] {
        var done = false

        override def checkNext = !done || self.hasNext
        override def release() = self.close()

        override def readNext(): A =
          if(done) self.next()
          else {
            done = true
            n
          }
      }
    }

  def take(n: Int): ResourceIterator[A] = new ResourceIterator[A] {
    var count = n
    override def checkNext = count > 0 && self.hasNext
    override def readNext() = {
      if(count > 0) {
        val a = self.next()
        count -= 1
        a
      }
      else ResourceIterator.empty.next()
    }
    override def release() = self.close()
  }

  // TODO: takeWhile

  def collect[B](f: PartialFunction[A, B]): ResourceIterator[B] = {
    var n = self.find(f.isDefinedAt)

    new ResourceIterator[B] {
      override def checkNext = n.isDefined

      override def readNext() = {
        val r = n.getOrElse(ResourceIterator.empty.next())
        n = self.find(f.isDefinedAt)
        f(r)
      }

      override def release() = self.close()
    }
  }



  // - Monadic operations ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def map[B](f: A ⇒ B): ResourceIterator[B] = new ResourceIterator[B] {
    override def checkNext  = self.hasNext
    override def readNext() = f(self.next())
    override def release()  = self.close()
  }

  def flatMap[B](f: A ⇒ ResourceIterator[B]): ResourceIterator[B] = {
    var cur: ResourceIterator[B] = ResourceIterator.empty
    new ResourceIterator[B] {
      override def checkNext = cur.hasNext || self.hasNext && {cur = f(self.next()); checkNext}
      override def readNext() = cur.next()
      override def release() = self.close()
    }
  }

  def filter(p: A ⇒ Boolean): ResourceIterator[A] = collect {
    case a if p(a) ⇒ a
  }

  def withFilter(p: A ⇒ Boolean): ResourceIterator[A] = filter(p)


  // - TraversableOnce -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  override def foreach[U](f: A ⇒ U): Unit = while(hasNext) f(next())
  override def seq: TraversableOnce[A] = this
  override def hasDefiniteSize: Boolean = isEmpty

  override def copyToArray[B >: A](xs: Array[B], start: Int, len: Int): Unit = {
    var i = start
    val end = start + math.min(len, xs.length - start)
    while(i < end && hasNext) {
      xs(i) = next()
      i += 1
    }
  }

  override def forall(p: A ⇒ Boolean): Boolean = {
    var res = true
    while(res && hasNext) res = p(next())
    res
  }
  override def toTraversable: Traversable[A] = toStream
  override def isEmpty: Boolean = !hasNext
  override def find(p: A ⇒ Boolean): Option[A] = {
    var res: Option[A] = None
    while(res.isEmpty && hasNext) {
      val n = next()
      if(p(n)) res = Some(n)
    }
    res
  }
  override def exists(p: A ⇒ Boolean): Boolean = {
    var res = false
    while(!res && hasNext) res = p(next())
    res
  }
  override def toStream: Stream[A] = if(hasNext) Stream.cons(next(), toStream) else Stream.empty
  override def toIterator: Iterator[A] = new Iterator[A] {
    override def hasNext: Boolean = self.hasNext
    override def next(): A = self.next()
  }
  override def isTraversableAgain: Boolean = false

  def withClose(f: () ⇒ Unit): ResourceIterator[A] = {
    new ResourceIterator[A] {
      override def checkNext = self.hasNext
      override def readNext() = self.next()
      override def release() = f()
    }
  }

  def safe[F](empty: ⇒ F)(f: Throwable ⇒ F): ResourceIterator[Result[F, A]] = new ResourceIterator[Result[F, A]] {
    override def readNext() =
      if(self.hasNext)
        try { Result.success(self.next()) }
        catch { case scala.util.control.NonFatal(t) ⇒ Result.failure(f(t)) }
      else Result.failure(empty)
    override def checkNext = self.hasNext
    override def release() = self.close()
  }
}

object ResourceIterator {
  val empty: ResourceIterator[Nothing] = new ResourceIterator[Nothing] {
    override def checkNext  = false
    override def readNext() = throw new NoSuchElementException("next on empty resource iterator")
    override def release()  = ()
  }

  def apply[A](as: A*): ResourceIterator[A] = ResourceIterator.fromIterator(as.iterator)

  def fromIterator[A](as: Iterator[A]): ResourceIterator[A] = new ResourceIterator[A] {
    override def checkNext = as.hasNext
    override def readNext() = as.next()
    override def release() = ()
  }
}
