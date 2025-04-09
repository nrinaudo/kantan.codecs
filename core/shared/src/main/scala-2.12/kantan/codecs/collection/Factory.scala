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

package kantan.codecs.collection

import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.Queue
import scala.collection.immutable.TreeSet
import scala.collection.mutable
import scala.reflect.ClassTag

/** Provides ways to create instances of `Builder` for arbitrary collection types.
  *
  * This is a bit like `CanBuildFrom` (and instances of [[Factory]] can be derived automatically for any type that has a
  * `CanBuildFrom`), but simpler (no `From` type parameter, no variance, ...) and with one critical difference: all the
  * default instances are `Serializable`, meaning that type classes that depend on `Factory` can be used with Apache
  * Spark and other framework that, unfortunately, rely on serialization.
  */
trait Factory[A, C] {
  def newBuilder: mutable.Builder[A, C]
}

trait LowPriorityFactoryInstances {
  implicit def canBuildFromFactory[C, A](implicit cbf: CanBuildFrom[Nothing, A, C]): Factory[A, C] =
    new Factory[A, C] {
      override def newBuilder =
        cbf()
    }
}

object Factory extends LowPriorityFactoryInstances {
  def apply[A, C](implicit ev: Factory[A, C]): Factory[A, C] =
    ev

  def apply[A, C](f: => mutable.Builder[A, C]): Factory[A, C] =
    new Factory[A, C] with Serializable {
      override def newBuilder =
        f
    }

  implicit def seqFactory[A]: Factory[A, Seq[A]] =
    Factory(Seq.newBuilder[A])
  implicit def queueFactory[A]: Factory[A, Queue[A]] =
    Factory(Queue.newBuilder[A])
  implicit def listFactory[A]: Factory[A, List[A]] =
    Factory(List.newBuilder[A])
  implicit def streamFactory[A]: Factory[A, Stream[A]] =
    Factory(Stream.newBuilder[A])
  implicit def vectorFactory[A]: Factory[A, Vector[A]] =
    Factory(Vector.newBuilder[A])
  implicit def indexedSeqFactory[A]: Factory[A, IndexedSeq[A]] =
    Factory(IndexedSeq.newBuilder[A])
  implicit def treeSetFactory[A: Ordering]: Factory[A, TreeSet[A]] =
    Factory(TreeSet.newBuilder[A])
  implicit def traversableFactory[A]: Factory[A, Traversable[A]] =
    Factory(Traversable.newBuilder[A])
  implicit def iterableFactory[A]: Factory[A, Iterable[A]] =
    Factory(Iterable.newBuilder[A])
  implicit def setFactory[A]: Factory[A, Set[A]] =
    Factory(Set.newBuilder[A])
  implicit def arrayFactory[A: ClassTag]: Factory[A, Array[A]] =
    Factory(Array.newBuilder[A])
}
