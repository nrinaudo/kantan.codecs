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

trait ResourceIterable[+A] extends Traversable[A] {
  /** Type of the concrete implementation.
    *
    * This is needed to be able to perform transforming operations, such as [[map]], without losing information about
    * the type we're working with.
    */
  type Repr[X] <: ResourceIterable[X]



  // - Abstract methods ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Opens the underlying resource and returns an iterator on it. */
  def iterator: ResourceIterator[A]

  /** Used to perform operations on the underlying operator, but later.
    *
    * The point is to allow methods such as [[map]] to be lazy: the mapping operation will be only be applied when
    * [[iterator]] is called, not immediately as is `Traversable`'s default behaviour.
    */
  protected def onIterator[B](f: ResourceIterator[A] ⇒ ResourceIterator[B]): Repr[B]



  // - "Lazy" methods --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def map[B](f: A ⇒ B): Repr[B] = onIterator(_.map(f))
  def flatMap[B](f: A ⇒ Repr[B]): Repr[B] = onIterator(_.flatMap(f.andThen(_.iterator)))
  def collect[B](f: PartialFunction[A, B]): Repr[B] = onIterator(_.collect(f))
  override def drop(n: Int): ResourceIterable[A] = onIterator(_.drop(n))
  override def dropWhile(p: A ⇒ Boolean): ResourceIterable[A] = onIterator(_.dropWhile(p))
  override def take(n: Int): ResourceIterable[A] = onIterator(_.take(n))
  override def takeWhile(p: A ⇒ Boolean): ResourceIterable[A] = onIterator(_.takeWhile(p))
  override def filter(p: A ⇒ Boolean): ResourceIterable[A] = onIterator(_.filter(p))
  override def withFilter(p: A ⇒ Boolean): ResourceIterable[A] = onIterator(_.withFilter(p))



  // - Traversable methods ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  override def foreach[U](f: A ⇒ U) = iterator.foreach(f)
  override def forall(p: A ⇒ Boolean) = iterator.forall(p)
  override def exists(p: A ⇒ Boolean) = iterator.exists(p)
  override def find(p: A ⇒ Boolean) = iterator.find(p)
  override def isEmpty = !iterator.hasNext
  override def foldRight[B](z: B)(op: (A, B) ⇒ B) = iterator.foldRight(z)(op)
  override def reduceRight[B >: A](op: (A, B) ⇒ B) = iterator.reduceRight(op)
}


