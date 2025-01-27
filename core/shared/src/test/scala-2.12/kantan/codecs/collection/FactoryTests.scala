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

import org.scalacheck.Arbitrary
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.collection.immutable.Queue
import scala.collection.immutable.TreeSet

/** Tests for basic [[Factory]] features. */
abstract class FactoryTests[A, F[_]](label: String, iterate: F[A] => Iterator[A])(implicit
  arb: Arbitrary[F[A]],
  hb: Factory[A, F[A]]
) extends AnyFunSuite with ScalaCheckPropertyChecks with Matchers {

  test(s"Factory[$label] should create the expected collection") {
    forAll { fa: F[A] =>
      iterate(fa).foldLeft(Factory[A, F[A]].newBuilder)(_ += _).result should be(fa)
    }
  }
}

class TreeSetFactoryTests extends FactoryTests[Int, TreeSet]("TreeSet", _.iterator)
class QueueFactoryTests extends FactoryTests[Int, Queue]("Queue", _.iterator)
class ListFactoryTests extends FactoryTests[Int, List]("List", _.iterator)
class StreamFactoryTests extends FactoryTests[Int, Stream]("Stream", _.iterator)
class VectorFactoryTests extends FactoryTests[Int, Vector]("Vector", _.iterator)
class IndexedSeqFactoryTests extends FactoryTests[Int, IndexedSeq]("IndexedSeq", _.iterator)
class TraversableFactoryTests extends FactoryTests[Int, Traversable]("Traversable", _.toList.iterator)
class SeqFactoryTests extends FactoryTests[Int, Seq]("Seq", _.iterator)
class SetFactoryTests extends FactoryTests[Int, Set]("Set", _.iterator)
class ArrayFactoryTests extends FactoryTests[Int, Array]("Array", _.iterator)

// SortedSet doesn't have a direct instance of Factory, and derives it from CanBuildFrom.
class SortedSetFactoryTests extends FactoryTests[Int, scala.collection.mutable.SortedSet]("SortedSet", _.iterator)
