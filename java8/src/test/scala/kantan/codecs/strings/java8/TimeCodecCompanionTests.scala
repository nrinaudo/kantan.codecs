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

import java.time._
import kantan.codecs.Codec
import kantan.codecs.laws.discipline.CodecTests
import kantan.codecs.strings.{DecodeError, StringDecoder, StringEncoder}
import kantan.codecs.strings.java8.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class TimeCodecCompanionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  type TestCodec[D] = Codec[String, D, DecodeError, codec.type]

  object CodecCompanion extends TimeCodecCompanion[String, DecodeError, codec.type] {
    override def decoderFrom[D](d: StringDecoder[D]) = d.tag[codec.type]
    override def encoderFrom[D](d: StringEncoder[D]) = d.tag[codec.type]

    implicit val instantTestCodec: TestCodec[Instant]               = defaultInstantCodec
    implicit val zonedDateTimeTestCodec: TestCodec[ZonedDateTime]   = defaultZonedDateTimeCodec
    implicit val offsetDateTimeTestCodec: TestCodec[OffsetDateTime] = defaultOffsetDateTimeCodec
    implicit val localDateTimeTestCodec: TestCodec[LocalDateTime]   = defaultLocalDateTimeCodec
    implicit val localDateTestCodec: TestCodec[LocalDate]           = defaultLocalDateCodec
    implicit val localTimeTestCodec: TestCodec[LocalTime]           = defaultLocalTimeCodec
  }

  import CodecCompanion._

  checkAll("TimeCodecCompanion[Instant]", CodecTests[String, Instant, DecodeError, codec.type].codec[Int, Int])
  checkAll(
    "TimeCodecCompanion[ZonedDateTime]",
    CodecTests[String, ZonedDateTime, DecodeError, codec.type].codec[Int, Int]
  )
  checkAll(
    "TimeCodecCompanion[OffsetDateTime]",
    CodecTests[String, OffsetDateTime, DecodeError, codec.type].codec[Int, Int]
  )
  checkAll(
    "TimeCodecCompanion[LocalDateTime]",
    CodecTests[String, LocalDateTime, DecodeError, codec.type].codec[Int, Int]
  )
  checkAll("TimeCodecCompanion[LocalDate]", CodecTests[String, LocalDate, DecodeError, codec.type].codec[Int, Int])
  checkAll("TimeCodecCompanion[LocalTime]", CodecTests[String, LocalTime, DecodeError, codec.type].codec[Int, Int])
}
