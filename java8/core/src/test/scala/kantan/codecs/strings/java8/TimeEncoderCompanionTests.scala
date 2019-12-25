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

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, OffsetDateTime, ZonedDateTime}
import kantan.codecs.export.{DerivedEncoder, Exported}
import kantan.codecs.strings.StringEncoder
import kantan.codecs.strings.java8.laws.discipline.DisciplineSuite
import kantan.codecs.strings.java8.laws.discipline.arbitrary._

/** Companion tagged by a type that is *not* the strings tag type. This allows us to declare exported implicits that
  * are unique and not "hidden" by existing ones.
  */
object EncoderCompanion extends TimeEncoderCompanion[String, codec.type] {

  type TestEncoder[D] = DerivedEncoder[String, D, codec.type]

  override def encoderFrom[D](d: StringEncoder[D]) = d.tag[codec.type]

  implicit val instantTestEncoder: TestEncoder[Instant]               = Exported(defaultInstantEncoder)
  implicit val zonedDateTimeTestEncoder: TestEncoder[ZonedDateTime]   = Exported(defaultZonedDateTimeEncoder)
  implicit val offsetDateTimeTestEncoder: TestEncoder[OffsetDateTime] = Exported(defaultOffsetDateTimeEncoder)
  implicit val localDateTimeTestEncoder: TestEncoder[LocalDateTime]   = Exported(defaultLocalDateTimeEncoder)
  implicit val localDateTestEncoder: TestEncoder[LocalDate]           = Exported(defaultLocalDateEncoder)
  implicit val localTimeTestEncoder: TestEncoder[LocalTime]           = Exported(defaultLocalTimeEncoder)

}

class TimeEncoderCompanionTests extends DisciplineSuite {

  import EncoderCompanion._

  checkAll("TimeEncoderCompanion[Instant]", TimeEncoderTests[Instant].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[ZonedDateTime]", TimeEncoderTests[ZonedDateTime].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[OffsetDateTime]", TimeEncoderTests[OffsetDateTime].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[LocalDateTime]", TimeEncoderTests[LocalDateTime].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[LocalDate]", TimeEncoderTests[LocalDate].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[LocalTime]", TimeEncoderTests[LocalTime].encoder[Int, Int])

}
