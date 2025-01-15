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

package kantan.codecs.strings

import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class ErrorsTests extends AnyFunSuite with ScalaCheckPropertyChecks with Matchers {
  test("DecodeErrors should be equal if the underlying exceptions are the same") {
    forAll { (e1: DecodeError, e2: Exception) =>
      (e1, e2) match {
        case (DecodeError(t1), DecodeError(t2)) => (e1 == e2) should be(t1 == t2)
        case _                                  => e1 should not be e2
      }
    }
  }

  test("DecodeErrors should have identical hashCodes if the underlying exceptions are the same") {
    forAll { (e1: DecodeError, e2: DecodeError) =>
      (e1.hashCode() == e2.hashCode()) should be(e1.message == e2.getMessage)
    }
  }
}
