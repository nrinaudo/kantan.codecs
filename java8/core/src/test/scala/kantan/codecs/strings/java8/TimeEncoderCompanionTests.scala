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
package strings
package java8

import export.Exported
import java.time._
import laws.discipline._, arbitrary._

class TimeEncoderCompanionTests extends DisciplineSuite {

  type TestEncoder[D] = Exported[Encoder[String, D, codec.type]]

  object EncoderCompanion extends TimeEncoderCompanion[String, codec.type] {
    override def encoderFrom[D](d: StringEncoder[D]) = d.tag[codec.type]

    implicit val instantTestEncoder: TestEncoder[Instant]               = Exported(defaultInstantEncoder)
    implicit val zonedDateTimeTestEncoder: TestEncoder[ZonedDateTime]   = Exported(defaultZonedDateTimeEncoder)
    implicit val offsetDateTimeTestEncoder: TestEncoder[OffsetDateTime] = Exported(defaultOffsetDateTimeEncoder)
    implicit val localDateTimeTestEncoder: TestEncoder[LocalDateTime]   = Exported(defaultLocalDateTimeEncoder)
    implicit val localDateTestEncoder: TestEncoder[LocalDate]           = Exported(defaultLocalDateEncoder)
    implicit val localTimeTestEncoder: TestEncoder[LocalTime]           = Exported(defaultLocalTimeEncoder)
  }

  checkAll("TimeEncoderCompanion[Instant]", StringEncoderTests[Instant].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[ZonedDateTime]", StringEncoderTests[ZonedDateTime].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[OffsetDateTime]", StringEncoderTests[OffsetDateTime].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[LocalDateTime]", StringEncoderTests[LocalDateTime].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[LocalDate]", StringEncoderTests[LocalDate].encoder[Int, Int])
  checkAll("TimeEncoderCompanion[LocalTime]", StringEncoderTests[LocalTime].encoder[Int, Int])

}
