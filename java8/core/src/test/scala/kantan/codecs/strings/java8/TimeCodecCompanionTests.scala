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
import kantan.codecs.Codec
import kantan.codecs.export.Exported
import kantan.codecs.strings.{DecodeError, StringDecoder, StringEncoder}
import kantan.codecs.strings.java8.laws.discipline.DisciplineSuite
import kantan.codecs.strings.java8.laws.discipline.arbitrary._

/** Companion tagged by a type that is *not* the strings tag type. This allows us to declare exported implicits that
  * are unique and not "hidden" by existing ones.
  */
object CodecCompanion extends TimeCodecCompanion[String, DecodeError, codec.type] {

  type TestCodec[D] = Exported[Codec[String, D, DecodeError, codec.type]]

  override def decoderFrom[D](d: StringDecoder[D]) = d.tag[codec.type]
  override def encoderFrom[D](d: StringEncoder[D]) = d.tag[codec.type]

  implicit val instantTestCodec: TestCodec[Instant] = Exported(instantCodec(Format.defaultInstantFormat))
  implicit val zonedDateTimeTestCodec: TestCodec[ZonedDateTime] = Exported(
    zonedDateTimeCodec(Format.defaultZonedDateTimeFormat)
  )
  implicit val offsetDateTimeTestCodec: TestCodec[OffsetDateTime] = Exported(
    offsetDateTimeCodec(Format.defaultOffsetDateTimeFormat)
  )
  implicit val localDateTimeTestCodec: TestCodec[LocalDateTime] = Exported(
    localDateTimeCodec(Format.defaultLocalDateTimeFormat)
  )
  implicit val localDateTestCodec: TestCodec[LocalDate] = Exported(localDateCodec(Format.defaultLocalDateFormat))
  implicit val localTimeTestCodec: TestCodec[LocalTime] = Exported(localTimeCodec(Format.defaultLocalTimeFormat))

}

class TimeCodecCompanionTests extends DisciplineSuite {

  import CodecCompanion._

  checkAll("TimeCodecCompanion[Instant]", TimeDecoderTests[Instant].decoder[Int, Int])
  checkAll("TimeCodecCompanion[Instant]", TimeEncoderTests[Instant].encoder[Int, Int])

  checkAll("TimeCodecCompanion[ZonedDateTime]", TimeDecoderTests[ZonedDateTime].decoder[Int, Int])
  checkAll("TimeCodecCompanion[ZonedDateTime]", TimeEncoderTests[ZonedDateTime].encoder[Int, Int])

  checkAll("TimeCodecCompanion[OffsetDateTime]", TimeDecoderTests[OffsetDateTime].decoder[Int, Int])
  checkAll("TimeCodecCompanion[OffsetDateTime]", TimeEncoderTests[OffsetDateTime].encoder[Int, Int])

  checkAll("TimeCodecCompanion[LocalDateTime]", TimeDecoderTests[LocalDateTime].decoder[Int, Int])
  checkAll("TimeCodecCompanion[LocalDateTime]", TimeEncoderTests[LocalDateTime].encoder[Int, Int])

  checkAll("TimeCodecCompanion[LocalDate]", TimeDecoderTests[LocalDate].decoder[Int, Int])
  checkAll("TimeCodecCompanion[LocalDate]", TimeEncoderTests[LocalDate].encoder[Int, Int])

  checkAll("TimeCodecCompanion[LocalTime]", TimeDecoderTests[LocalTime].decoder[Int, Int])
  checkAll("TimeCodecCompanion[LocalTime]", TimeEncoderTests[LocalTime].encoder[Int, Int])

}
