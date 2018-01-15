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
package joda
package time

import export.Exported
import laws.discipline._, arbitrary._
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}

class JodaTimeEncoderCompanionTests extends DisciplineSuite {

  type TestEncoder[D] = Exported[Encoder[String, D, codec.type]]

  object EncoderCompanion extends JodaTimeEncoderCompanion[String, codec.type] {
    override def encoderFrom[D](d: StringEncoder[D]) = d.tag[codec.type]

    implicit val dateTimeTestEncoder: TestEncoder[DateTime]           = Exported(defaultDateTimeEncoder)
    implicit val localDateTimeTestEncoder: TestEncoder[LocalDateTime] = Exported(defaultLocalDateTimeEncoder)
    implicit val localDateTestEncoder: TestEncoder[LocalDate]         = Exported(defaultLocalDateEncoder)
    implicit val localTimeTestEncoder: TestEncoder[LocalTime]         = Exported(defaultLocalTimeEncoder)
  }

  checkAll("JodaTimeEncoderCompanion[DateTime]", StringEncoderTests[DateTime].encoder[Int, Int])
  checkAll("JodaTimeEncoderCompanion[LocalDateTime]", StringEncoderTests[LocalDateTime].encoder[Int, Int])
  checkAll("JodaTimeEncoderCompanion[LocalDate]", StringEncoderTests[LocalDate].encoder[Int, Int])
  checkAll("JodaTimeEncoderCompanion[LocalTime]", StringEncoderTests[LocalTime].encoder[Int, Int])

}
