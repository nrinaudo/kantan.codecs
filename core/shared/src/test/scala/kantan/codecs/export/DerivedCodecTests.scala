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

package kantan.codecs.export

import kantan.codecs.{Codec, Decoder, Encoder}
import kantan.codecs.export.DerivedCodecTests.{
  DerivedStringCodec,
  DerivedStringDecoder,
  DerivedStringEncoder,
  WrappedInt
}
import kantan.codecs.strings.{codecs, DecodeError, StringCodec, StringDecoder, StringEncoder, StringResult}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class DerivedCodecTests extends AnyFunSuite with ScalaCheckPropertyChecks with Matchers {

  // - Helper functions ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // Standard decode function for WrappedInt, used in all tests
  val decode: String => StringResult[WrappedInt] = s => StringResult(WrappedInt(s.toInt))

  // Standard encode function for WrappedInt[Int], used in all tests
  val encode: WrappedInt => String = _.i.toString

  // - Priority tests --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("Derived decoders should have a lower priority than bespoke ones") {
    implicit val bespoke: StringDecoder[WrappedInt]        = StringDecoder.from(decode)
    implicit val derived: DerivedStringDecoder[WrappedInt] = DerivedDecoder.from(decode)

    implicitly[Decoder[String, WrappedInt, DecodeError, codecs.type]] should be(bespoke)
  }

  test("Derived encoders should have a lower priority than bespoke ones") {
    implicit val bespoke: StringEncoder[WrappedInt]        = StringEncoder.from(encode)
    implicit val derived: DerivedStringEncoder[WrappedInt] = DerivedEncoder.from(encode)

    implicitly[Encoder[String, WrappedInt, codecs.type]] should be(bespoke)
  }

  // - Resolving tests -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("Derived decoders should be picked up when no other is available") {
    implicit val derived: DerivedStringDecoder[WrappedInt] = DerivedDecoder.from(decode)

    implicitly[StringDecoder[WrappedInt]] should be(derived.value)
  }

  test("Derived encoders should be picked up when no other is available") {
    implicit val derived: DerivedStringEncoder[WrappedInt] = DerivedEncoder.from(encode)

    implicitly[Encoder[String, WrappedInt, codecs.type]] should be(derived.value)
  }

  test("Derived codecs should be picked up when no encoder or decoder is available") {
    val codec: StringCodec[WrappedInt]                   = Codec.from(decode)(encode)
    implicit val derived: DerivedStringCodec[WrappedInt] = Exported(codec)

    implicitly[Encoder[String, WrappedInt, codecs.type]] should be(codec)
    implicitly[Decoder[String, WrappedInt, DecodeError, codecs.type]] should be(codec)
  }
}

object DerivedCodecTests {
  // - Type aliases ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // These are strictly for readability purposes
  type DerivedStringDecoder[D] = DerivedDecoder[String, D, DecodeError, codecs.type]
  type DerivedStringEncoder[D] = DerivedEncoder[String, D, codecs.type]
  type DerivedStringCodec[D]   = Exported[Codec[String, D, DecodeError, codecs.type]]

  // - Test types ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Simple type, needed only to make sure we don't get accidental codecs for existing types. */
  final case class WrappedInt(i: Int)
}
