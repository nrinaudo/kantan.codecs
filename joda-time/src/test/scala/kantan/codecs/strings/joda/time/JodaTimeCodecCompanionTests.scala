/*
 * Copyright 2017 Nicolas Rinaudo
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

package kantan.codecs.strings.joda.time

import kantan.codecs.Codec
import kantan.codecs.laws.discipline.CodecTests
import kantan.codecs.strings.{DecodeError, StringDecoder, StringEncoder}
import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class JodaTimeCodecCompanionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  object CodecCompanion extends JodaTimeCodecCompanion[String, DecodeError, codec.type] {
    override def decoderFrom[D](d: StringDecoder[D]) = d.tag[codec.type]
    override def encoderFrom[D](d: StringEncoder[D]) = d.tag[codec.type]
  }

  implicit val dateTimeCodec: Codec[String, DateTime, DecodeError, codec.type] =
    CodecCompanion.dateTimeCodec(defaultDateTimeFormat)

  implicit val localDateTimeCodec: Codec[String, LocalDateTime, DecodeError, codec.type] =
    CodecCompanion.localDateTimeCodec(defaultLocalDateTimeFormat)

  implicit val localDateCodec: Codec[String, LocalDate, DecodeError, codec.type] =
    CodecCompanion.localDateCodec(defaultLocalDateFormat)

  implicit val localTimeCodec: Codec[String, LocalTime, DecodeError, codec.type] =
    CodecCompanion.localTimeCodec(defaultLocalTimeFormat)

  checkAll("JodaTimeCodecCompanion[DateTime]", CodecTests[String, DateTime, DecodeError, codec.type].codec[Int, Int])
  checkAll("JodaTimeCodecCompanion[LocalDateTime]",
    CodecTests[String, LocalDateTime, DecodeError, codec.type].codec[Int, Int])
  checkAll("JodaTimeCodecCompanion[LocalDate]", CodecTests[String, LocalDate, DecodeError, codec.type].codec[Int, Int])
  checkAll("JodaTimeCodecCompanion[LocalTime]", CodecTests[String, LocalTime, DecodeError, codec.type].codec[Int, Int])
}
