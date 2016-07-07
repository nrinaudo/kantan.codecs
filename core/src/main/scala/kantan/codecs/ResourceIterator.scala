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

trait ResourceIterator[+A] extends TraversableOnce[A] with Closeable { self ⇒
  // Makes sure the underlying resource is closed if empty.
  // This is necessary for edges cases where hasNext returns true from the get-go: next is never called and we never
  // check whether the underlying resource should be freed.
  closeIfNeeded(false)



  // - Internal state handling -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  protected def readNext(): A
  protected def checkNext: Boolean
  protected def shouldClose: Boolean = !hasNext

  private var isClosed = false

  private def closeIfNeeded(force: Boolean): Unit = if(force || (!isClosed && shouldClose)) {
    isClosed = true
    close()
  }

  def close(): Unit
  final def hasNext: Boolean = {
    !isClosed && checkNext
  }

  final def next(): A = {
    var error = false

    try { readNext() }
    catch { case e: Throwable ⇒ error = true; throw e}
    finally {
      closeIfNeeded(error)
    }
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
        override def close() = self.close()

        override def readNext(): A =
          if(done) self.readNext()
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
        val a = self.readNext()
        count -= 1
        a
      }
      else ResourceIterator.empty.next()
    }
    override def close() = self.close()
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

      override def close() = self.close()
    }
  }



  // - Monadic operations ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def map[B](f: A ⇒ B): ResourceIterator[B] = new ResourceIterator[B] {
    override def checkNext  = self.hasNext
    override def readNext() = f(self.readNext())
    override def close()  = self.close()
  }

  def flatMap[B](f: A ⇒ ResourceIterator[B]): ResourceIterator[B] = {
    var cur: ResourceIterator[B] = ResourceIterator.empty
    new ResourceIterator[B] {
      override def checkNext = cur.hasNext || self.hasNext && {cur = f(self.next()); checkNext}
      override def readNext() = cur.readNext()
      override def close() = self.close()
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

  def withClose(f: () ⇒ Unit): ResourceIterator[A] = new ResourceIterator[A] {
    override def checkNext = self.hasNext
    override def readNext() = self.readNext()
    override def close() = f()
  }

  def safe[F](empty: ⇒ F)(f: Throwable ⇒ F): ResourceIterator[Result[F, A]] = new ResourceIterator[Result[F, A]] {
    override def readNext() =
      if(self.hasNext)
        try { Result.success(self.next()) }
        catch { case scala.util.control.NonFatal(t) ⇒ Result.failure(f(t)) }
      else Result.failure(empty)
    override def checkNext = self.hasNext
    override def close() = self.close()
  }
}

object ResourceIterator {
  val empty: ResourceIterator[Nothing] = new ResourceIterator[Nothing] {
    override def checkNext = false
    override def readNext() = throw new NoSuchElementException("next on empty resource iterator")
    override def close() = ()
  }

  def apply[A](as: A*): ResourceIterator[A] = ResourceIterator.fromIterator(as.iterator)

  def fromIterator[A](as: Iterator[A]): ResourceIterator[A] = new ResourceIterator[A] {
    override def checkNext = as.hasNext
    override def readNext() = as.next()
    override def close() = ()
  }
}
