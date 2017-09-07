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

import java.time._
import kantan.codecs.Decoder
import kantan.codecs.export.Exported
import kantan.codecs.laws.discipline.DecoderTests
import kantan.codecs.strings.{DecodeError, StringDecoder}
import kantan.codecs.strings.java8.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class TimeDecoderCompanionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  type TestDecoder[D] = Exported[Decoder[String, D, DecodeError, codec.type]]

  object DecoderCompanion extends TimeDecoderCompanion[String, DecodeError, codec.type] {
    override def decoderFrom[D](d: StringDecoder[D]) = d.tag[codec.type]

    implicit val instantTestDecoder: TestDecoder[Instant]               = Exported(defaultInstantDecoder)
    implicit val zonedDateTimeTestDecoder: TestDecoder[ZonedDateTime]   = Exported(defaultZonedDateTimeDecoder)
    implicit val offsetDateTimeTestDecoder: TestDecoder[OffsetDateTime] = Exported(defaultOffsetDateTimeDecoder)
    implicit val localDateTimeTestDecoder: TestDecoder[LocalDateTime]   = Exported(defaultLocalDateTimeDecoder)
    implicit val localDateTestDecoder: TestDecoder[LocalDate]           = Exported(defaultLocalDateDecoder)
    implicit val localTimeTestDecoder: TestDecoder[LocalTime]           = Exported(defaultLocalTimeDecoder)
  }

  import DecoderCompanion._

  checkAll("TimeDecoderCompanion[Instant]", DecoderTests[String, Instant, DecodeError, codec.type].decoder[Int, Int])
  checkAll(
    "TimeDecoderCompanion[ZonedDateTime]",
    DecoderTests[String, ZonedDateTime, DecodeError, codec.type].decoder[Int, Int]
  )
  checkAll(
    "TimeDecoderCompanion[OffsetDateTime]",
    DecoderTests[String, OffsetDateTime, DecodeError, codec.type].decoder[Int, Int]
  )
  checkAll(
    "TimeDecoderCompanion[LocalDateTime]",
    DecoderTests[String, LocalDateTime, DecodeError, codec.type].decoder[Int, Int]
  )
  checkAll(
    "TimeDecoderCompanion[LocalDate]",
    DecoderTests[String, LocalDate, DecodeError, codec.type].decoder[Int, Int]
  )
  checkAll(
    "TimeDecoderCompanion[LocalTime]",
    DecoderTests[String, LocalTime, DecodeError, codec.type].decoder[Int, Int]
  )
}
