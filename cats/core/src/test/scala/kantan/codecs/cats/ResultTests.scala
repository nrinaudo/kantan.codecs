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
package cats

import _root_.cats._, instances.all._
import _root_.cats.kernel.laws.discipline.{MonoidTests, OrderTests}
import _root_.cats.laws.discipline._
import laws.discipline.{SerializableTests ⇒ _, _}
import laws.discipline.arbitrary._

class ResultTests extends DisciplineSuite {

  implicit val iso = SemigroupalTests.Isomorphisms.invariant[Result[String, ?]]

  checkAll("Result[String, Int]", MonoidTests[Result[String, Int]].monoid)
  checkAll("Monoid[Result[String, Int]]", SerializableTests.serializable(Monoid[Result[String, Int]]))

  checkAll("Result[String, ?]", SemigroupalTests[Result[String, ?]].semigroupal[Int, Int, Int])
  checkAll("Semigroupal[Result[String, ?]]", SerializableTests.serializable(Semigroupal[Result[String, ?]]))

  checkAll("Result[String, ?]", MonadTests[Result[String, ?]].monad[Int, Int, Int])
  checkAll("Monad[Result[String, ?]]", SerializableTests.serializable(Monad[Result[Int, ?]]))

  checkAll("Result[?, ?]", BifunctorTests[Result].bifunctor[Int, Int, Int, Int, Int, Int])
  checkAll("Bifunctor[Result[?, ?]]", SerializableTests.serializable(Bifunctor[Result[?, ?]]))

  checkAll("Result[String, ?]", TraverseTests[Result[String, ?]].traverse[Int, Int, Int, Int, Option, Option])
  checkAll("Traverse[Result[String, ?]]", SerializableTests.serializable(Traverse[Result[String, ?]]))

  checkAll("Result[Int, String]", OrderTests[Result[Int, String]].order)
  checkAll("Order[Result[Int, String]]", SerializableTests.serializable(Order[Result[Int, String]]))

  checkAll("Result[?, ?]", BitraverseTests[Result].bitraverse[Option, Int, Int, Int, String, String, String])
  checkAll("Bitraverse[Result]", SerializableTests.serializable(Bitraverse[Result]))

  test("Show should yield the expected result for successes") {
    forAll { i: Int ⇒
      Show[Result[String, Int]].show(Result.success(i)) should be(s"Success($i)")
    }
  }

  test("Show should yield the expected result for failures") {
    forAll { i: Int ⇒
      Show[Result[Int, String]].show(Result.failure(i)) should be(s"Failure($i)")
    }
  }
}
