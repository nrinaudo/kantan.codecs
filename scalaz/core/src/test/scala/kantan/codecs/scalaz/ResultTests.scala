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
package scalaz

import _root_.scalaz._
import _root_.scalaz.scalacheck.ScalazArbitrary._
import _root_.scalaz.scalacheck.ScalazProperties._
import _root_.scalaz.scalacheck.ScalazProperties.{equal ⇒ equ}
import _root_.scalaz.std.anyVal._
import _root_.scalaz.std.string._
import laws.discipline._, arbitrary._
import org.scalacheck.Arbitrary

class ResultTests extends ScalazDisciplineSuite {
  case class NoOrder(value: Int)
  implicit val noOrderEqual: Equal[NoOrder] = new Equal[NoOrder] {
    override def equal(a1: NoOrder, a2: NoOrder): Boolean = a1.value == a2.value
  }
  implicit val arbNoOrder: Arbitrary[NoOrder] = Arbitrary(Arbitrary.arbitrary[Int].map(NoOrder.apply))

  checkAll("Result[String, Int]", order.laws[Result[String, Int]])
  checkAll("Result[String, Int]", equ.laws[Result[String, NoOrder]])
  checkAll("Result[String, NonEmptyList[Int]]", semigroup.laws[Result[String, NonEmptyList[Int]]])
  checkAll("Result[String, Int]", monoid.laws[Result[String, Int]])
  checkAll("Result[String, ?]", monad.laws[Result[String, ?]])
  checkAll("Result[String, ?]", traverse.laws[Result[String, ?]])
  checkAll("Result[?, ?]", bitraverse.laws[Result[?, ?]])

  test("Show should yield the expected result for successes") {
    forAll { i: Int ⇒
      Show[Result[String, Int]].shows(Result.success(i)) should be(s"Success($i)")
    }
  }

  test("Show should yield the expected result for failures") {
    forAll { i: Int ⇒
      Show[Result[Int, String]].shows(Result.failure(i)) should be(s"Failure($i)")
    }
  }
}
