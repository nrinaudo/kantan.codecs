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
package collection

import org.scalacheck.Arbitrary
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.collection.immutable.{Queue, TreeSet}

/** Tests for basic [[Hasbuilder]] features. */
abstract class HasBuilderTests[F[_], A](label: String, iterate: F[A] => Iterator[A])(
  implicit arb: Arbitrary[F[A]],
  hb: HasBuilder[F, A]
) extends FunSuite with GeneratorDrivenPropertyChecks with Matchers {

  test(s"HasBuilder[$label] should create the expected collection") {
    forAll { fa: F[A] =>
      iterate(fa).foldLeft(HasBuilder[F, A].newBuilder)(_ += _).result should be(fa)
    }
  }

}

class TreeSetHasBuilderTests     extends HasBuilderTests[TreeSet, Int]("TreeSet", _.iterator)
class QueueHasBuilderTests       extends HasBuilderTests[Queue, Int]("Queue", _.iterator)
class ListHasBuilderTests        extends HasBuilderTests[List, Int]("List", _.iterator)
class StreamHasBuilderTests      extends HasBuilderTests[Stream, Int]("Stream", _.iterator)
class VectorHasBuilderTests      extends HasBuilderTests[Vector, Int]("Vector", _.iterator)
class IndexedSeqHasBuilderTests  extends HasBuilderTests[IndexedSeq, Int]("IndexedSeq", _.iterator)
class TraversableHasBuilderTests extends HasBuilderTests[Traversable, Int]("Traversable", _.toList.iterator)
class SeqHasBuilderTests         extends HasBuilderTests[Seq, Int]("Seq", _.iterator)
class SetHasBuilderTests         extends HasBuilderTests[Set, Int]("Set", _.iterator)
class ArrayHasBuilderTests       extends HasBuilderTests[Array, Int]("Array", _.iterator)

// SortedSet doesn't have a direct instance of HasBuilder, and derives it from CanBuildFrom.
class SortedSetHasBuilderTests extends HasBuilderTests[scala.collection.mutable.SortedSet, Int]("SortedSet", _.iterator)
