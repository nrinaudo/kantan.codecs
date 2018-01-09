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
import java.time.format.DateTimeFormatter
import kantan.codecs.Decoder
import kantan.codecs.strings.StringDecoder

trait TimeDecoderCompanion[E, F, T] {
  def decoderFrom[D](d: StringDecoder[D]): Decoder[E, D, F, T]

  def localTimeDecoder(format: String): Decoder[E, LocalTime, F, T]              = localTimeDecoder(Format(format))
  def localTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalTime, F, T] = localTimeDecoder(Format(format))
  def localTimeDecoder(format: Format): Decoder[E, LocalTime, F, T]              = decoderFrom(localTimeStringDecoder(format))
  def defaultLocalTimeDecoder: Decoder[E, LocalTime, F, T]                       = localTimeDecoder(defaultLocalTimeFormat)

  def localDateDecoder(format: String): Decoder[E, LocalDate, F, T]              = localDateDecoder(Format(format))
  def localDateDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalDate, F, T] = localDateDecoder(Format(format))
  def localDateDecoder(format: Format): Decoder[E, LocalDate, F, T]              = decoderFrom(localDateStringCodec(format))
  def defaultLocalDateDecoder: Decoder[E, LocalDate, F, T]                       = localDateDecoder(defaultLocalDateFormat)

  def localDateTimeDecoder(format: String): Decoder[E, LocalDateTime, F, T] = localDateTimeDecoder(Format(format))
  def localDateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalDateTime, F, T] =
    localDateTimeDecoder(Format(format))
  def localDateTimeDecoder(format: Format): Decoder[E, LocalDateTime, F, T] =
    decoderFrom(localDateTimeStringCodec(format))
  def defaultLocalDateTimeDecoder: Decoder[E, LocalDateTime, F, T] = localDateTimeDecoder(defaultLocalDateTimeFormat)

  def offsetDateTimeDecoder(format: String): Decoder[E, OffsetDateTime, F, T] = offsetDateTimeDecoder(Format(format))
  def offsetDateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, OffsetDateTime, F, T] =
    offsetDateTimeDecoder(Format(format))
  def offsetDateTimeDecoder(format: Format): Decoder[E, OffsetDateTime, F, T] =
    decoderFrom(offsetDateTimeStringCodec(format))
  def defaultOffsetDateTimeDecoder: Decoder[E, OffsetDateTime, F, T] =
    offsetDateTimeDecoder(defaultOffsetDateTimeFormat)

  def zonedDateTimeDecoder(format: String): Decoder[E, ZonedDateTime, F, T] = zonedDateTimeDecoder(Format(format))
  def zonedDateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, ZonedDateTime, F, T] =
    zonedDateTimeDecoder(Format(format))
  def zonedDateTimeDecoder(format: Format): Decoder[E, ZonedDateTime, F, T] =
    decoderFrom(zonedDateTimeStringCodec(format))
  def defaultZonedDateTimeDecoder: Decoder[E, ZonedDateTime, F, T] = zonedDateTimeDecoder(defaultZonedDateTimeFormat)

  def instantDecoder(format: String): Decoder[E, Instant, F, T]              = instantDecoder(Format(format))
  def instantDecoder(format: ⇒ DateTimeFormatter): Decoder[E, Instant, F, T] = instantDecoder(Format(format))
  def instantDecoder(format: Format): Decoder[E, Instant, F, T]              = decoderFrom(instantStringCodec(format))
  def defaultInstantDecoder: Decoder[E, Instant, F, T]                       = instantDecoder(defaultInstantFormat)
}
