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

class JodaTimeDecoderCompanionTests extends DisciplineSuite {

  type TestDecoder[D] = Exported[Decoder[String, D, DecodeError, codec.type]]

  object DecoderCompanion extends JodaTimeDecoderCompanion[String, DecodeError, codec.type] {
    override def decoderFrom[D](d: StringDecoder[D]) = d.tag[codec.type]

    implicit val dateTimeTestDecoder: TestDecoder[DateTime]           = Exported(defaultDateTimeDecoder)
    implicit val localDateTimeTestDecoder: TestDecoder[LocalDateTime] = Exported(defaultLocalDateTimeDecoder)
    implicit val localDateTestDecoder: TestDecoder[LocalDate]         = Exported(defaultLocalDateDecoder)
    implicit val localTimeTestDecoder: TestDecoder[LocalTime]         = Exported(defaultLocalTimeDecoder)
  }

  checkAll("JodaTimeDecoderCompanion[DateTime]", StringDecoderTests[DateTime].decoder[Int, Int])
  checkAll("JodaTimeDecoderCompanion[LocalDateTime]", StringDecoderTests[LocalDateTime].decoder[Int, Int])
  checkAll("JodaTimeDecoderCompanion[LocalDate]", StringDecoderTests[LocalDate].decoder[Int, Int])
  checkAll("JodaTimeDecoderCompanion[LocalTime]", StringDecoderTests[LocalTime].decoder[Int, Int])

}
