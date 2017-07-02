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

import kantan.codecs.error._
import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class DecoderCompanionTests extends FunSuite with GeneratorDrivenPropertyChecks {
  object codec
  object Companion extends DecoderCompanion[String, String, codec.type]

  implicit val stringIsError: IsError[String] = IsError[Exception].map(_.getMessage)

  type Dec = String ⇒ Result[String, Int]

  test("DecoderCompanion.from should be equivalent to Decoder.from") {
    forAll { (f: Dec, str: String) ⇒
      assert(Decoder.from(f).decode(str) == Companion.from(f).decode(str))
    }
  }

  test("DecoderCompanion.oneOf should be equivalent to Decoder.oneOf for non-empty lists") {
    forAll { (h: Dec, t: List[Dec], str: String) ⇒
      val cDec = Companion.oneOf((h :: t).map(Companion.from[Int]):_*)
      val dDec = Decoder.oneOf((h :: t).map(Decoder.from[String, Int, String, codec.type]):_*)

      assert(cDec.decode(str) == dDec.decode(str))
    }
  }

  test("DecoderCompanion.oneOf should be equivalent to Decoder.oneOf for empty lists") {
    val cDec = Companion.oneOf()
    val dDec = Decoder.oneOf[String, Int, String, codec.type]()

    forAll { (str: String) ⇒ assert(cDec.decode(str) == dDec.decode(str)) }
  }
}
