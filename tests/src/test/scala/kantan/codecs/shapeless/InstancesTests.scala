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

package kantan.codecs.shapeless

import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import kantan.codecs.shapeless.laws._
import kantan.codecs.shapeless.laws.discipline.arbitrary._
import kantan.codecs.strings._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import shapeless._

class InstancesTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // - HList / Coproduct instances -------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val cnilDec: StringDecoder[CNil] = cnilDecoder(_ ⇒ DecodeError("Attempting to decode CNil"))

  implicit def hlistEncoder[A](implicit ea: StringEncoder[A]): StringEncoder[A :: HNil] =
    StringEncoder((a: A :: HNil) ⇒ a match {
      case h :: _ ⇒ ea.encode(h)
    })

  implicit def hlistDecoder[A](implicit da: StringDecoder[A]): StringDecoder[A :: HNil] =
    da.map(h ⇒ h :: HNil)



  // - Tests -----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val decoder = StringDecoder[Int Or Boolean]


  checkAll("StringDecoder[Int Or Boolean]", DecoderTests[String, Int Or Boolean, DecodeError, codecs.type]
    .decoder[Int, Int])
  checkAll("StringEncoder[Int Or Boolean]", EncoderTests[String, Int Or Boolean, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Int Or Boolean]", CodecTests[String, Int Or Boolean, DecodeError, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Int Or Boolean]", DecoderTests[String, Int Or Boolean, DecodeError, tagged.type]
    .decoder[Int, Int])
  checkAll("TaggedEncoder[Int Or Boolean]", EncoderTests[String, Int Or Boolean, tagged.type].encoder[Int, Int])

  test("Encoder[?, CNil, ?] should fail") {
    intercept[IllegalStateException] {StringEncoder[CNil].encode(null)}
    ()
  }
}
