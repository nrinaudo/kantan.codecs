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
package joda.time

import export.Exported
import joda.time.laws.discipline._
import joda.time.laws.discipline.arbitrary._
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}

class JodaTimeEncoderCompanionTests extends DisciplineSuite {

  type TestEncoder[A] = Exported[Encoder[String, A, codec.type]]

  object EncoderCompanion extends JodaTimeEncoderCompanion[String, codec.type] {

    override def encoderFrom[D](d: StringEncoder[D]) = d.tag[codec.type]

    implicit val defaultDateTimeTestEncoder: TestEncoder[DateTime]           = Exported(defaultDateTimeEncoder)
    implicit val defaultLocalDateTimeTestEncoder: TestEncoder[LocalDateTime] = Exported(defaultLocalDateTimeEncoder)
    implicit val defaultLocalTimeTestEncoder: TestEncoder[LocalTime]         = Exported(defaultLocalTimeEncoder)
    implicit val defaultLocalDateTestEncoder: TestEncoder[LocalDate]         = Exported(defaultLocalDateEncoder)

  }

  import EncoderCompanion._

  checkAll("JodaTimeEncoderCompanion[DateTime]", EncoderTests[String, DateTime, codec.type].encoder[Int, Int])
  checkAll("JodaTimeEncoderCompanion[LocalDateTime]", EncoderTests[String, LocalDateTime, codec.type].encoder[Int, Int])
  checkAll("JodaTimeEncoderCompanion[LocalDate]", EncoderTests[String, LocalDate, codec.type].encoder[Int, Int])
  checkAll("JodaTimeEncoderCompanion[LocalTime]", EncoderTests[String, LocalTime, codec.type].encoder[Int, Int])

}
