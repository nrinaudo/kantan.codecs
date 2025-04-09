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

package kantan.codecs.strings

import kantan.codecs.`export`.Exported

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime

package object java8 extends TimeCodecCompanion[String, DecodeError, codecs.type] with ToFormatLiteral {

  override def encoderFrom[D](d: StringEncoder[D]) =
    d
  override def decoderFrom[D](d: StringDecoder[D]) =
    d

  implicit val defaultStringInstantDecoder: Exported[StringDecoder[Instant]] = Exported(defaultInstantDecoder)
  implicit val defaultStringLocalDateTimeDecoder: Exported[StringDecoder[LocalDateTime]] = Exported(
    defaultLocalDateTimeDecoder
  )
  implicit val defaultStringZonedDateTimeDecoder: Exported[StringDecoder[ZonedDateTime]] = Exported(
    defaultZonedDateTimeDecoder
  )
  implicit val defaultStringOffsetDateTimeDecoder: Exported[StringDecoder[OffsetDateTime]] = Exported(
    defaultOffsetDateTimeDecoder
  )
  implicit val defaultStringLocalDateDecoder: Exported[StringDecoder[LocalDate]] = Exported(defaultLocalDateDecoder)
  implicit val defaultStringLocalTimeDecoder: Exported[StringDecoder[LocalTime]] = Exported(defaultLocalTimeDecoder)

  implicit val defaultStringInstantEncoder: Exported[StringEncoder[Instant]] = Exported(defaultInstantEncoder)
  implicit val defaultStringLocalDateTimeEncoder: Exported[StringEncoder[LocalDateTime]] = Exported(
    defaultLocalDateTimeEncoder
  )
  implicit val defaultStringZonedDateTimeEncoder: Exported[StringEncoder[ZonedDateTime]] = Exported(
    defaultZonedDateTimeEncoder
  )
  implicit val defaultStringOffsetDateTimeEncoder: Exported[StringEncoder[OffsetDateTime]] = Exported(
    defaultOffsetDateTimeEncoder
  )
  implicit val defaultStringLocalDateEncoder: Exported[StringEncoder[LocalDate]] = Exported(defaultLocalDateEncoder)
  implicit val defaultStringLocalTimeEncoder: Exported[StringEncoder[LocalTime]] = Exported(defaultLocalTimeEncoder)

}
