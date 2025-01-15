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

package kantan.codecs.resource

import kantan.codecs.collection.Factory

import scala.annotation.unchecked.{uncheckedVariance => uV}
import scala.collection.mutable.Buffer
import scala.reflect.ClassTag

@SuppressWarnings(
  Array(
    "org.wartremover.warts.Var",
    "org.wartremover.warts.Throw",
    "org.wartremover.warts.While",
    "org.wartremover.warts.Null"
  )
)
trait VersionSpecificResourceIterator[+A] { self: ResourceIterator[A] =>

  def to[F[_]](implicit factory: Factory[A @uV, F[A @uV]]): F[A @uV] =
    foldLeft(factory.newBuilder)(_ += _).result

  def toList: List[A] =
    to[List]
  def toArray[AA >: A](implicit ct: ClassTag[AA]): Array[AA] =
    toIterator.toArray
  def toBuffer[AA >: A]: Buffer[AA] =
    toIterator.toBuffer
  def toIndexedSeq: IndexedSeq[A] =
    to[IndexedSeq]
  def toIterable: Iterable[A] =
    to[Iterable]
  def toSeq: Seq[A] =
    to[Seq]
  def seq: Seq[A] =
    to[Seq]
  def toSet[AA >: A]: Set[AA] =
    toIterator.toSet
  def toVector: Vector[A] =
    to[Vector]
  def toTraversable: Traversable[A] =
    to[Traversable]
  def toStream: Stream[A] =
    if(hasNext) Stream.cons(next(), toStream) else Stream.empty
  def toIterator: Iterator[A] =
    new Iterator[A] {
      override def hasNext: Boolean =
        self.hasNext
      override def next(): A =
        self.next()
    }
}
