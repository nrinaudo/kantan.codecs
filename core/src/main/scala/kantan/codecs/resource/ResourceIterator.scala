/*
 * Copyright 2017 Nicolas Rinaudo
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

package kantan.codecs.resource

import kantan.codecs.Result
import scala.annotation.tailrec
import scala.util.Try

/** Offers iterator-like access to IO resources.
  *
  * For the most part, values of type [[ResourceIterator]] can be considered as iterators, with a few improvements.
  *
  * First, they have a [[ResourceIterator.close()*]] method, which allows you to release the underlying resource when
  * needed. This is fairly important and part of the reason why working with `Source.getLines` can be so aggravating.
  *
  * Second, [[ResourceIterator.close()*]] is mostly not needed: whenever an IO error occurs or the underlying resource
  * is empty, it will be closed automatically. Provided you intend to read the whole resource, you never need to
  * explicitly close it. This covers non-obvious cases such as [[ResourceIterator.filter filtering]] or
  * [[ResourceIterator.drop dropping]] elements.
  *
  * You should be able to express most common causes for not reading the entire stream through standard combinators. For
  * example, "take the first `n` elements" is `take(n)`, or "take all odd elements" is `filter(_ % 2 == 0)`. This
  * allows you to ignore the fact that the underlying resource needs to be closed. Should you ever find youself in a
  * situation when you just want to stop, however, [[ResourceIterator.close()*]] is available.
  */
trait ResourceIterator[+A] extends TraversableOnce[A] with java.io.Closeable { self ⇒
  // - Abstract methods ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Reads the next element in the underlying resource
    *
    * This method is by definition side-effecting and allowed to throw exceptions.
    */
  protected def readNext(): A

  /** Checks whether there are still elements to read in the underlying resource.
    *
    * This method must be pure: it's expected not to have side effects and never to throw exceptions.
    */
  protected def checkNext: Boolean

  /** Releases the underlying resource.
    *
    * While this method is side-effecting and allowed to throw exceptions, the current implementation will simply
    * swallow them.
    */
  protected def release(): Unit



  // - Internal state handling -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  private var isClosed = false
  private var lastError: Option[Throwable] = None

  @inline
  private def doClose(): Unit = {
    isClosed = true

    // In the current version, closing can't be allowed to fail, since it can occur in hasNext.
    // This is not ideal, and I intend to fix that if I ever come up with a workable solution.
    Try(release())
    ()
  }



  // - Iterator methods ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  final def close(): Unit = if(!isClosed) doClose()

  final def hasNext: Boolean =
  // We need to check for emptiness here in order to deal with a very specific case: the underlying data was empty
  // from the beginning. Under these circumstances, `next` will never be called, and we need to call `close()` on
  // `hasNext`. This is not ideal, but the only other alternative would be to check for emptiness at creation time and
  // close then, which would end up ignoring calls to `withClose`.
    if(isClosed) false
    else {
      if(try {checkNext}
      catch {
        case scala.util.control.NonFatal(e) ⇒
          lastError = Some(e)
          close()
          true
      }) true
      else {
        doClose()
        false
      }
    }

  final def next(): A = lastError match {
    case Some(e) ⇒
      lastError = None
      throw e
    case _ ⇒
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
  }



  // - Useful methods --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Drops the next `n` elements from the resource.
    *
    * If the resource contains `m` elements such that `m < n`, then only `m` elements will be dropped.
    *
    * No element will be consumed until the next [[next]] call.
    */
  def drop(n: Int): ResourceIterator[A] =
    if(n <= 0 || isEmpty) this
    else new ResourceIterator[A] {
      var rem = n // remaining number of items to drop.

      @tailrec
      def hasMore(): Boolean =
        if(rem <= 0) self.hasNext
        else if(self.hasNext) {
          rem = rem - 1
          self.next()
          hasMore()
        }
        else false

      override def checkNext: Boolean = hasMore()
      override def readNext() =
        if(hasMore()) self.next()
        else ResourceIterator.empty.next()
      override def release() = self.close()
    }

  /** Drops elements from the resource until one is found that doesn't verify `p` or the resource is empty.
    *
    * No element will be consumed until the next [[next]] call.
    */
  def dropWhile(p: A ⇒ Boolean): ResourceIterator[A] =
    if(isEmpty) this
    else new ResourceIterator[A] {
      var state = 0 // Current state. 0: not initialised. 1: `n` contains an interesting value. 2: back to normal.
      var n: A = _  // Where to store the first A that verifies p.

      def init(): Unit = {
        // Skips all elements until one is found that doesn't match `p` or the end of the resource is reached.
        n = self.next()
        while(self.hasNext && p(n)) n = self.next()

        // If we've not found anything interesting, move to the final state.
        if(p(n)) state = 2

        // Otherwise, move to the 'return `n`' state.
        else state = 1
      }

      // We have no choice here but to perform all IO-bound operations in `checkNext` - we can't know if there are more
      // elements to be had unless we read all of them until one that doesn't match `p` is found.
      override def checkNext = {
        if(state == 0) init()

        if(state == 1) true
        else           self.hasNext
      }

      override def readNext(): A = {
        if(state == 0) init()

        if(state == 1) {
          state = 2
          n
        }
        else self.next()
      }

      override def release() = self.close()
    }

  /** Restrict this resource to the next `n` elements, dropping whatever is left. */
  def take(n: Int): ResourceIterator[A] = new ResourceIterator[A] {
    var count = n
    override def checkNext = count > 0 && self.hasNext
    override def readNext() = {
      if(count > 0) {
        count -= 1
        self.next()
      }
      else ResourceIterator.empty.next()
    }
    override def release() = self.close()
  }

  /** Considers this resource to be empty as soon as an element is found that doesn't verify `p`. */
  def takeWhile(p: A ⇒ Boolean): ResourceIterator[A] =
    if(isEmpty) this
    else new ResourceIterator[A] {
      var first = true      // Whether we've started reading from the resource.
      var n: A = _          // Latest value read from the resource.
      var hasN: Boolean = _ // Whether or not n verifies p.

      def takeNext(): Unit = {
        n = self.next()
        hasN = p(n)
      }

      def init(): Unit = {
        takeNext()
        first = false
      }

      override def checkNext = {
        if(first) init()
        hasN
      }

      override def readNext() = {
        if(first) init()
        if(hasN) {
          val n2 = n
          if(self.hasNext) takeNext()
          else hasN = false
          n2
        }
        else ResourceIterator.empty.next()
      }

      override def release() = self.close()
    }

  def collect[B](f: PartialFunction[A, B]): ResourceIterator[B] =
    if(isEmpty) ResourceIterator.empty
    else new ResourceIterator[B] {
      var n: Option[A] = _
      var first = true

      def init(): Unit = {
        n = self.find(f.isDefinedAt)
        first = false
      }

      override def checkNext = {
        if(first) init()
        n.isDefined
      }

      override def readNext() = {
        if(first) init()

        val r = n.getOrElse(ResourceIterator.empty.next())
        n = self.find(f.isDefinedAt)
        f(r)
      }

      override def release() = self.close()
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

  /** Calls the specified function when the underlying resource is empty. */
  def withClose(f: () ⇒ Unit): ResourceIterator[A] = {
    new ResourceIterator[A] {
      override def checkNext = self.hasNext
      override def readNext() = self.next()
      override def release() = {
        self.close()
        f()
      }
    }
  }

  /** Makes the current [[kantan.codecs.resource.ResourceIterator]] safe.
    *
    * This is achieved by catching all non-fatal exceptions and passing them to the specified `f` to turn into a failure
    * type.
    *
    * This is meant to be used by the various kantan.* libraries that offer stream-like APIs: it allows them to wrap
    * IO in a safe iterator and focus on dealing with decoding.
    *
    * @param empty error value for when `next` is called on an empty iterator.
    * @param f     used to turn non-fatal exceptions into error types.
    */
  def safe[F](empty: ⇒ F)(f: Throwable ⇒ F): ResourceIterator[Result[F, A]] = new ResourceIterator[Result[F, A]] {
    override def readNext() =
      if(self.hasNext) Result.nonFatal(self.next()).leftMap(f)
      else             Result.failure(empty)
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
