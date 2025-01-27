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

import scala.collection.Factory
import scala.collection.mutable.Buffer

@SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
trait VersionSpecificResourceIterator[+A] { self: ResourceIterator[A] =>
  def to[F](factory: Factory[A, F]): F =
    foldLeft(factory.newBuilder)(_ += _).result()

  def toList: List[A] =
    to(List)
  def toBuffer[AA >: A]: Buffer[AA] =
    to(Buffer)
  def toIndexedSeq: IndexedSeq[A] =
    to(IndexedSeq)
  def toIterable: Iterable[A] =
    to(Iterable)
  def toSeq: Seq[A] =
    to(Seq)
  def toSet[AA >: A]: Set[AA] =
    to(Set)
  def toVector: Vector[A] =
    to(Vector)

  def iterator: Iterator[A] =
    new Iterator[A] {
      override def next(): A =
        self.next()
      override def hasNext: Boolean =
        self.hasNext
    }
}
