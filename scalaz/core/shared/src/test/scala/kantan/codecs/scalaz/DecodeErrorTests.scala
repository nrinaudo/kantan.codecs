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

package kantan.codecs.scalaz

import kantan.codecs.scalaz.laws.discipline.ScalazDisciplineSuite
import kantan.codecs.scalaz.laws.discipline.arbitrary._
import kantan.codecs.strings.DecodeError
import scalaz.Show
import scalaz.scalacheck.ScalazProperties.{equal => equ}

class DecodeErrorTests extends ScalazDisciplineSuite {

  checkAll("DecodeError", equ.laws[DecodeError])

  test("Show[DecodeError] should yield a string containing the error message") {
    forAll { (error: DecodeError) =>
      Show[DecodeError].shows(error) should include(error.message)
    }
  }

}
