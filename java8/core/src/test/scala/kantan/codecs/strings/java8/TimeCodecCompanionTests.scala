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
import kantan.codecs.Encoder
import kantan.codecs.`export`.Exported
import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline.CodecTests
import kantan.codecs.laws.discipline.DisciplineSuite
import kantan.codecs.strings.DecodeError
import kantan.codecs.strings.StringDecoder
import kantan.codecs.strings.StringEncoder
import kantan.codecs.strings.java8.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime

class TimeCodecCompanionTests extends DisciplineSuite {

  type TestDecoder[D] = Exported[Decoder[String, D, DecodeError, codec.type]]
  type TestEncoder[D] = Exported[Encoder[String, D, codec.type]]

  object CodecCompanion extends TimeCodecCompanion[String, DecodeError, codec.type] {

    override def decoderFrom[D](d: StringDecoder[D]): Decoder[String, D, DecodeError, codec.type] =
      d.tag[codec.type]
    override def encoderFrom[D](d: StringEncoder[D]): Encoder[String, D, codec.type] =
      d.tag[codec.type]

    implicit val instantTestEncoder: TestEncoder[Instant]               = Exported(defaultInstantEncoder)
    implicit val zonedDateTimeTestEncoder: TestEncoder[ZonedDateTime]   = Exported(defaultZonedDateTimeEncoder)
    implicit val offsetDateTimeTestEncoder: TestEncoder[OffsetDateTime] = Exported(defaultOffsetDateTimeEncoder)
    implicit val localDateTimeTestEncoder: TestEncoder[LocalDateTime]   = Exported(defaultLocalDateTimeEncoder)
    implicit val localDateTestEncoder: TestEncoder[LocalDate]           = Exported(defaultLocalDateEncoder)
    implicit val localTimeTestEncoder: TestEncoder[LocalTime]           = Exported(defaultLocalTimeEncoder)

    implicit val instantTestDecoder: TestDecoder[Instant]               = Exported(defaultInstantDecoder)
    implicit val zonedDateTimeTestDecoder: TestDecoder[ZonedDateTime]   = Exported(defaultZonedDateTimeDecoder)
    implicit val offsetDateTimeTestDecoder: TestDecoder[OffsetDateTime] = Exported(defaultOffsetDateTimeDecoder)
    implicit val localDateTimeTestDecoder: TestDecoder[LocalDateTime]   = Exported(defaultLocalDateTimeDecoder)
    implicit val localDateTestDecoder: TestDecoder[LocalDate]           = Exported(defaultLocalDateDecoder)
    implicit val localTimeTestDecoder: TestDecoder[LocalTime]           = Exported(defaultLocalTimeDecoder)

  }

  import CodecCompanion._

  implicitly[Arbitrary[LegalValue[String, Instant, codec.type]]]

  checkAll("TimeCodecCompanion[Instant]", CodecTests[String, Instant, DecodeError, codec.type].codec[Int, Int])
  checkAll(
    "TimeCodecCompanion[ZonedDateTime]",
    CodecTests[String, ZonedDateTime, DecodeError, codec.type].codec[Int, Int]
  )
  checkAll(
    "TimeCodecCompanion[OffsetDateTime]",
    CodecTests[String, OffsetDateTime, DecodeError, codec.type].codec[Int, Int]
  )
  checkAll(
    "TimeCodecCompanion[LocalDateTime]",
    CodecTests[String, LocalDateTime, DecodeError, codec.type].codec[Int, Int]
  )
  checkAll("TimeCodecCompanion[LocalDate]", CodecTests[String, LocalDate, DecodeError, codec.type].codec[Int, Int])
  checkAll("TimeCodecCompanion[LocalTime]", CodecTests[String, LocalTime, DecodeError, codec.type].codec[Int, Int])

}
