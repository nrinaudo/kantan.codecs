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

package kantan.codecs.strings.joda.time

import kantan.codecs.Decoder
import kantan.codecs.export.Exported
import kantan.codecs.laws.discipline.DecoderTests
import kantan.codecs.strings.{DecodeError, StringDecoder}
import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class JodaTimeDecoderCompanionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  type TestDecoder[D] = Exported[Decoder[String, D, DecodeError, codec.type]]

  object DecoderCompanion extends JodaTimeDecoderCompanion[String, DecodeError, codec.type] {
    override def decoderFrom[D](d: StringDecoder[D]) = d.tag[codec.type]

    implicit val dateTimeTestDecoder: TestDecoder[DateTime]           = Exported(defaultDateTimeDecoder)
    implicit val localDateTimeTestDecoder: TestDecoder[LocalDateTime] = Exported(defaultLocalDateTimeDecoder)
    implicit val localDateTestDecoder: TestDecoder[LocalDate]         = Exported(defaultLocalDateDecoder)
    implicit val localTimeTestDecoder: TestDecoder[LocalTime]         = Exported(defaultLocalTimeDecoder)
  }

  import DecoderCompanion._

  checkAll(
    "JodaTimeDecoderCompanion[DateTime]",
    DecoderTests[String, DateTime, DecodeError, codec.type].decoder[Int, Int]
  )
  checkAll(
    "JodaTimeDecoderCompanion[LocalDateTime]",
    DecoderTests[String, LocalDateTime, DecodeError, codec.type].decoder[Int, Int]
  )
  checkAll(
    "JodaTimeDecoderCompanion[LocalDate]",
    DecoderTests[String, LocalDate, DecodeError, codec.type].decoder[Int, Int]
  )
  checkAll(
    "JodaTimeDecoderCompanion[LocalTime]",
    DecoderTests[String, LocalTime, DecodeError, codec.type].decoder[Int, Int]
  )
}
