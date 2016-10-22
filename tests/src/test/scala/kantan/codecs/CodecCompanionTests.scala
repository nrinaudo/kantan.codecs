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

class CodecCompanionTests extends FunSuite with GeneratorDrivenPropertyChecks {
  object codec
  object Companion extends CodecCompanion[String, Exception, codec.type]

  test("CodecCompanion.from should be equivalent to Codec.from (for encoding)") {
    forAll { (f: String ⇒ Result[Exception, Int], g: Int ⇒ String, i: Int) ⇒
      assert(Codec.from(f)(g).encode(i) == Codec.from(f)(g).encode(i))
    }
  }

  test("CodecCompanion.from should be equivalent to Codec.from (for decoding)") {
    forAll { (f: String ⇒ Result[Exception, Int], g: Int ⇒ String, str: String) ⇒
      assert(Codec.from(f)(g).decode(str) == Codec.from(f)(g).decode(str))
    }
  }
}
