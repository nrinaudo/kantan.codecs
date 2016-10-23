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

import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ResultCompanionTests extends FunSuite with GeneratorDrivenPropertyChecks {
  object simple extends ResultCompanion.Simple[String]

  test("Simple.success should behave like Result.success") {
    forAll { i: Int ⇒ assert(simple.success(i) == Result.success(i)) }
  }

  test("Simple.failure should behave like Result.failure") {
    forAll { s: String ⇒ assert(simple.failure(s) == Result.failure(s)) }
  }

  test("Simple.fromEither should behave like Result.fromEither") {
    forAll { (e: Either[String, Int]) ⇒ assert(simple.fromEither(e) == Result.fromEither(e)) }
  }

  test("Simple.fromOption should behave like Result.fromOption") {
    forAll { (o: Option[Int], s: String) ⇒ assert(simple.fromOption(o, s) == Result.fromOption(o, s)) }
  }

  test("Simple.sequence should behave like Result.sequence") {
    forAll { (l: List[Result[String, Int]]) ⇒
      assert(simple.sequence(l) == Result.sequence(l))
    }
  }
}
