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
import kantan.codecs.export.{DerivedDecoder, Exported}
import kantan.codecs.strings.{DecodeError, StringDecoder}
import kantan.codecs.strings.java8.laws.discipline.DisciplineSuite
import kantan.codecs.strings.java8.laws.discipline.arbitrary._

/** Companion tagged by a type that is *not* the strings tag type. This allows us to declare exported implicits that
  * are unique and not "hidden" by existing ones.
  */
object DecoderCompanion extends TimeDecoderCompanion[String, DecodeError, codec.type] {

  type TestDecoder[D] = DerivedDecoder[String, D, DecodeError, codec.type]

  override def decoderFrom[D](d: StringDecoder[D]) = d.tag[codec.type]

  implicit val instantTestDecoder: TestDecoder[Instant]               = Exported(defaultInstantDecoder)
  implicit val zonedDateTimeTestDecoder: TestDecoder[ZonedDateTime]   = Exported(defaultZonedDateTimeDecoder)
  implicit val offsetDateTimeTestDecoder: TestDecoder[OffsetDateTime] = Exported(defaultOffsetDateTimeDecoder)
  implicit val localDateTimeTestDecoder: TestDecoder[LocalDateTime]   = Exported(defaultLocalDateTimeDecoder)
  implicit val localDateTestDecoder: TestDecoder[LocalDate]           = Exported(defaultLocalDateDecoder)
  implicit val localTimeTestDecoder: TestDecoder[LocalTime]           = Exported(defaultLocalTimeDecoder)

}

class TimeDecoderCompanionTests extends DisciplineSuite {

  import DecoderCompanion._

  checkAll("TimeDecoderCompanion[Instant]", TimeDecoderTests[Instant].decoder[Int, Int])
  checkAll("TimeDecoderCompanion[ZonedDateTime]", TimeDecoderTests[ZonedDateTime].decoder[Int, Int])
  checkAll("TimeDecoderCompanion[OffsetDateTime]", TimeDecoderTests[OffsetDateTime].decoder[Int, Int])
  checkAll("TimeDecoderCompanion[LocalDateTime]", TimeDecoderTests[LocalDateTime].decoder[Int, Int])
  checkAll("TimeDecoderCompanion[LocalDate]", TimeDecoderTests[LocalDate].decoder[Int, Int])
  checkAll("TimeDecoderCompanion[LocalTime]", TimeDecoderTests[LocalTime].decoder[Int, Int])

}
