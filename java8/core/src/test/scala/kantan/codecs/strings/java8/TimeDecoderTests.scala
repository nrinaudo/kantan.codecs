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

package kantan.codecs.strings.java8

import kantan.codecs.Decoder
import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline.DecoderTests
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.strings.DecodeError
import org.scalacheck.{Arbitrary, Cogen}

object TimeDecoderTests {

  type TimeDecoder[D]      = Decoder[String, D, DecodeError, codec.type]
  type TimeDecoderTests[D] = DecoderTests[String, D, DecodeError, codec.type]
  type LegalTime[D]        = LegalValue[String, D, codec.type]

  def apply[D: Arbitrary: Cogen: TimeDecoder](
    implicit al: Arbitrary[LegalTime[D]]
  ): TimeDecoderTests[D] =
    DecoderTests.apply

}
