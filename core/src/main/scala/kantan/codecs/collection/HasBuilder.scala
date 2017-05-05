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

package kantan.codecs.collection

import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.{Queue, TreeSet}
import scala.collection.mutable
import scala.reflect.ClassTag

/** Provides ways to create instances of `Builder` for arbitrary collection types.
  *
  * This is a bit like `CanBuildFrom` (and instances of [[HasBuilder]] can be derived automatically for any type that
  * has a `CanBuildFrom`), but simpler (no `From` type parameter, no variance, ...) and with one critical difference:
  * all the default instances are `Serializable`, meaning that type classes that depend on `HasBuilder` can be
  * used with Apache Spark and other framework that, unfortunately, rely on serialisation.
  */
trait HasBuilder[F[_], A] {
  def newBuilder: mutable.Builder[A, F[A]]
}

trait LowPriorityHasBuilderInstances {
  implicit def canBuildFromHasBuilder[F[_], A](implicit cbf: CanBuildFrom[Nothing, A, F[A]]): HasBuilder[F, A] =
    new HasBuilder[F, A] {
      override def newBuilder = cbf()
    }
}

object HasBuilder extends LowPriorityHasBuilderInstances {
  def apply[F[_], A](implicit ev: HasBuilder[F, A]): HasBuilder[F, A] = macro imp.summon[HasBuilder[F, A]]

  def apply[F[_], A](f: ⇒ mutable.Builder[A, F[A]]): HasBuilder[F, A] = new HasBuilder[F, A] with Serializable {
    override def newBuilder = f
  }

  implicit def treeSetHasBuilder[A: Ordering]: HasBuilder[TreeSet, A] = HasBuilder(TreeSet.newBuilder[A])
  implicit def queueHasBuilder[A]: HasBuilder[Queue, A] = HasBuilder(Queue.newBuilder[A])
  implicit def listHasBuilder[A]: HasBuilder[List, A] = HasBuilder(List.newBuilder[A])
  implicit def streamHasBuilder[A]: HasBuilder[Stream, A] = HasBuilder(Stream.newBuilder[A])
  implicit def vectorHasBuilder[A]: HasBuilder[Vector, A] = HasBuilder(Vector.newBuilder[A])
  implicit def indexedSeqHasBuilder[A]: HasBuilder[IndexedSeq, A] = HasBuilder(IndexedSeq.newBuilder[A])
  implicit def traversableHasBuilder[A]: HasBuilder[Traversable, A] = HasBuilder(Traversable.newBuilder[A])
  implicit def seqHasBuilder[A]: HasBuilder[Seq, A] = HasBuilder(Seq.newBuilder[A])
  implicit def setHasBuilder[A]: HasBuilder[Set, A] = HasBuilder(Set.newBuilder[A])
  implicit def arrayHasBuilder[A: ClassTag]: HasBuilder[Array, A] = HasBuilder(Array.newBuilder[A])
}
