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

import imp.imp
import kantan.codecs.{Codec, Decoder, Encoder}
import kantan.codecs.laws.{DecoderLaws, EncoderLaws}
import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline.{DecoderTests => RootDecoderTests, EncoderTests => RootEncoderTests}
import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.{Arbitrary, Cogen}

/** Acts as both a tag type and a namespace for tagged type tests.
  *
  * The purpose of this is to test `Decoder.tag` and `Encoder.tag`.
  */
object tagged {

  // - Type aliases for readability ------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  type DecoderTests[D]      = RootDecoderTests[String, D, DecodeError, tagged.type]
  type EncoderTests[D]      = RootEncoderTests[String, D, tagged.type]
  type TaggedDecoderLaws[D] = DecoderLaws[String, D, DecodeError, tagged.type]
  type TaggedEncoderLaws[D] = EncoderLaws[String, D, tagged.type]
  type TaggedLegalValue[D]  = LegalValue[String, D, tagged.type]

  // - Specialised tests -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // These are provided strictly for readability purposes. They allow developers to write:
  // {{{
  // checkAll("TaggedDecoder[Int]", tagged.DecoderTests[Int].decoder[Int, Int])
  // }}}
  // rather than:
  // {{{
  // checkAll("TaggedDecoder[Int]", DecoderTests[String, Int, DecodeError, tagged.type].decoder[Int, Int])
  // }}}

  object DecoderTests {
    def apply[D: Arbitrary: Cogen: TaggedDecoderLaws](
      implicit al: Arbitrary[TaggedLegalValue[D]]
    ): DecoderTests[D] =
      RootDecoderTests.apply
  }

  object EncoderTests {
    def apply[D: Arbitrary: TaggedEncoderLaws](
      implicit al: Arbitrary[TaggedLegalValue[D]]
    ): EncoderTests[D] =
      RootEncoderTests.apply
  }

  // - Default instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def decoder[D: StringDecoder]: Decoder[String, D, DecodeError, tagged.type] =
    StringDecoder[D].tag[tagged.type]

  implicit def encoder[D: StringEncoder]: Encoder[String, D, tagged.type] =
    StringEncoder[D].tag[tagged.type]

  def codec[D: StringCodec]: Codec[String, D, DecodeError, tagged.type] =
    imp[StringCodec[D]].tag[tagged.type]

}
