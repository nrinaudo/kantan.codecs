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

class TimeCodecCompanionTests extends DisciplineSuite {

  type TestDecoder[A] = Exported[Decoder[String, A, DecodeError, codec.type]]
  type TestEncoder[A] = Exported[Encoder[String, A, codec.type]]

  object CodecCompanion extends TimeCodecCompanion[String, DecodeError, codec.type] {
    override def decoderFrom[D](d: StringDecoder[D]) = d.tag[codec.type]
    override def encoderFrom[D](d: StringEncoder[D]) = d.tag[codec.type]

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

  checkAll("TimeCodecCompanion[Instant]", StringCodecTests[Instant].codec[Int, Int])
  checkAll("TimeCodecCompanion[ZonedDateTime]", StringCodecTests[ZonedDateTime].codec[Int, Int])
  checkAll("TimeCodecCompanion[OffsetDateTime]", StringCodecTests[OffsetDateTime].codec[Int, Int])
  checkAll("TimeCodecCompanion[LocalDateTime]", StringCodecTests[LocalDateTime].codec[Int, Int])
  checkAll("TimeCodecCompanion[LocalDate]", StringCodecTests[LocalDate].codec[Int, Int])
  checkAll("TimeCodecCompanion[LocalTime]", StringCodecTests[LocalTime].codec[Int, Int])

}
