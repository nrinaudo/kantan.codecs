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

class JodaTimeCodecCompanionTests extends DisciplineSuite {

  type TestDecoder[A] = Exported[Decoder[String, A, DecodeError, codec.type]]
  type TestEncoder[A] = Exported[Encoder[String, A, codec.type]]

  object CodecCompanion extends JodaTimeCodecCompanion[String, DecodeError, codec.type] {

    override def decoderFrom[D](d: StringDecoder[D]) = d.tag[codec.type]
    override def encoderFrom[D](d: StringEncoder[D]) = d.tag[codec.type]

    implicit val defaultDateTimeTestDecoder: TestDecoder[DateTime]           = Exported(defaultDateTimeDecoder)
    implicit val defaultLocalDateTimeTestDecoder: TestDecoder[LocalDateTime] = Exported(defaultLocalDateTimeDecoder)
    implicit val defaultLocalTimeTestDecoder: TestDecoder[LocalTime]         = Exported(defaultLocalTimeDecoder)
    implicit val defaultLocalDateTestDecoder: TestDecoder[LocalDate]         = Exported(defaultLocalDateDecoder)

    implicit val defaultDateTimeTestEncoder: TestEncoder[DateTime]           = Exported(defaultDateTimeEncoder)
    implicit val defaultLocalDateTimeTestEncoder: TestEncoder[LocalDateTime] = Exported(defaultLocalDateTimeEncoder)
    implicit val defaultLocalTimeTestEncoder: TestEncoder[LocalTime]         = Exported(defaultLocalTimeEncoder)
    implicit val defaultLocalDateTestEncoder: TestEncoder[LocalDate]         = Exported(defaultLocalDateEncoder)

  }

  import CodecCompanion._

  checkAll("JodaTimeCodecCompanion[DateTime]", CodecTests[String, DateTime, DecodeError, codec.type].codec[Int, Int])
  checkAll(
    "JodaTimeCodecCompanion[LocalDateTime]",
    CodecTests[String, LocalDateTime, DecodeError, codec.type].codec[Int, Int]
  )
  checkAll("JodaTimeCodecCompanion[LocalDate]", CodecTests[String, LocalDate, DecodeError, codec.type].codec[Int, Int])
  checkAll("JodaTimeCodecCompanion[LocalTime]", CodecTests[String, LocalTime, DecodeError, codec.type].codec[Int, Int])

}
