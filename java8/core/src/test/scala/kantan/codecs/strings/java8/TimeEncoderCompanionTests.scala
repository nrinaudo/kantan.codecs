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

import kantan.codecs.Encoder
import kantan.codecs.`export`.Exported
import kantan.codecs.laws.discipline.DisciplineSuite
import kantan.codecs.laws.discipline.EncoderTests
import kantan.codecs.strings.StringEncoder
import kantan.codecs.strings.java8.laws.discipline.arbitrary._

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime

class TimeEncoderCompanionTests extends DisciplineSuite {

  type TestEncoder[D] = Exported[Encoder[String, D, codec.type]]

  object EncoderCompanion extends TimeEncoderCompanion[String, codec.type] {

    override def encoderFrom[D](d: StringEncoder[D]): Encoder[String, D, codec.type] =
      d.tag[codec.type]

    implicit val instantTestEncoder: TestEncoder[Instant]               = Exported(defaultInstantEncoder)
    implicit val zonedDateTimeTestEncoder: TestEncoder[ZonedDateTime]   = Exported(defaultZonedDateTimeEncoder)
    implicit val offsetDateTimeTestEncoder: TestEncoder[OffsetDateTime] = Exported(defaultOffsetDateTimeEncoder)
    implicit val localDateTimeTestEncoder: TestEncoder[LocalDateTime]   = Exported(defaultLocalDateTimeEncoder)
    implicit val localDateTestEncoder: TestEncoder[LocalDate]           = Exported(defaultLocalDateEncoder)
    implicit val localTimeTestEncoder: TestEncoder[LocalTime]           = Exported(defaultLocalTimeEncoder)

  }

  import EncoderCompanion._

  checkAll("TimeEncoderCompanion[Instant]", EncoderTests[String, Instant, codec.type].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[ZonedDateTime]", EncoderTests[String, ZonedDateTime, codec.type].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[OffsetDateTime]", EncoderTests[String, OffsetDateTime, codec.type].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[LocalDateTime]", EncoderTests[String, LocalDateTime, codec.type].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[LocalDate]", EncoderTests[String, LocalDate, codec.type].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[LocalTime]", EncoderTests[String, LocalTime, codec.type].encoder[Int, Int])

}
