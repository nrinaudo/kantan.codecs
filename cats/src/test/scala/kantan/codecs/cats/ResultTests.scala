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

package kantan.codecs.cats

import cats._
import cats.data.NonEmptyList
import cats.instances.all._
import cats.kernel.laws.{GroupLaws, OrderLaws}
import cats.laws.discipline._, CartesianTests.Isomorphisms
import cats.laws.discipline.arbitrary._
import kantan.codecs.Result
import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.{Arbitrary, Cogen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ResultTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case class NoOrder(value: Int)

  implicit val noOrderCogen: Cogen[NoOrder] = Cogen(_.value.toLong)

  implicit val noOrderEq: Eq[NoOrder] = new Eq[NoOrder] {
    override def eqv(a1: NoOrder, a2: NoOrder): Boolean = a1.value == a2.value
  }
  implicit val arbNoOrder: Arbitrary[NoOrder] = Arbitrary(Arbitrary.arbitrary[Int].map(NoOrder.apply))

  // TODO: not sure why this isn't resolved automatically. Investigate.
  implicit val test: Isomorphisms[Result[String, ?]] = CartesianTests.Isomorphisms.invariant[Result[String, ?]]

  checkAll("Result[String, ?]", MonadTests[Result[String, ?]].monad[Int, Int, Int])
  checkAll("Result[?, ?]", BifunctorTests[Result].bifunctor[Int, Int, Int, Int, Int, Int])
  checkAll("Result[String, ?]", TraverseTests[Result[String, ?]].traverse[Int, Int, Int, Int, Option, Option])
  checkAll("Result[Int, String]", OrderLaws[Result[Int, String]].order)
  checkAll("Result[Int, String]", OrderLaws[Result[NoOrder, String]].eqv)
  checkAll("Result[String, Int]", GroupLaws[Result[String, Int]].monoid)
  checkAll("Result[String, NonEmptyList[Int]]", GroupLaws[Result[String, NonEmptyList[Int]]].semigroup)

  test("Show should yield the expected result for successes") {
    forAll { i: Int ⇒
      assert(Show[Result[String, Int]].show(Result.success(i)) == s"Success($i)")
    }
  }

  test("Show should yield the expected result for failures") {
    forAll { i: Int ⇒
      assert(Show[Result[Int, String]].show(Result.failure(i)) == s"Failure($i)")
    }
  }
}
