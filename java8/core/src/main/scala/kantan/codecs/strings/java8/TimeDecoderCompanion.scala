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

import java.time._
import java.time.format.DateTimeFormatter
import strings.StringDecoder

/** Provides useful methods for a java8 time decoder companions.
  *
  * Usage note: when declaring default implicit instances, be sure to wrap them in an [[export.Exported]]. Otherwise,
  * custom instances and default ones are very likely to conflict.
  */
trait TimeDecoderCompanion[E, F, T] {

  def decoderFrom[D](d: StringDecoder[D]): Decoder[E, D, F, T]

  // - LocalTime -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def localTimeDecoder(format: String): Decoder[E, LocalTime, F, T] = localTimeDecoder(Format(format))

  def localTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalTime, F, T] = localTimeDecoder(Format(format))

  def localTimeDecoder(format: Format): Decoder[E, LocalTime, F, T] =
    decoderFrom(StringDecoder.from(StringDecoder.makeSafe("LocalTime")(s ⇒ format.parseLocalTime(s))))

  def defaultLocalTimeDecoder: Decoder[E, LocalTime, F, T] = localTimeDecoder(Format.defaultLocalTimeFormat)

  // - LocalDate -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def localDateDecoder(format: String): Decoder[E, LocalDate, F, T] = localDateDecoder(Format(format))

  def localDateDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalDate, F, T] = localDateDecoder(Format(format))

  def localDateDecoder(format: Format): Decoder[E, LocalDate, F, T] =
    decoderFrom(StringDecoder.from(StringDecoder.makeSafe("LocalDate")(s ⇒ format.parseLocalDate(s))))

  def defaultLocalDateDecoder: Decoder[E, LocalDate, F, T] = localDateDecoder(Format.defaultLocalDateFormat)

  // - LocalDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def localDateTimeDecoder(format: String): Decoder[E, LocalDateTime, F, T] = localDateTimeDecoder(Format(format))

  def localDateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalDateTime, F, T] =
    localDateTimeDecoder(Format(format))

  def localDateTimeDecoder(format: Format): Decoder[E, LocalDateTime, F, T] =
    decoderFrom(StringDecoder.from(StringDecoder.makeSafe("LocalDateTime")(s ⇒ format.parseLocalDateTime(s))))

  def defaultLocalDateTimeDecoder: Decoder[E, LocalDateTime, F, T] =
    localDateTimeDecoder(Format.defaultLocalDateTimeFormat)

  // - OffsetDateTime --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def offsetDateTimeDecoder(format: String): Decoder[E, OffsetDateTime, F, T] = offsetDateTimeDecoder(Format(format))

  def offsetDateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, OffsetDateTime, F, T] =
    offsetDateTimeDecoder(Format(format))

  def offsetDateTimeDecoder(format: Format): Decoder[E, OffsetDateTime, F, T] =
    decoderFrom(StringDecoder.from(StringDecoder.makeSafe("OffsetDateTime")(s ⇒ format.parseOffsetDateTime(s))))

  def defaultOffsetDateTimeDecoder: Decoder[E, OffsetDateTime, F, T] =
    offsetDateTimeDecoder(Format.defaultOffsetDateTimeFormat)

  // - ZonedDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def zonedDateTimeDecoder(format: String): Decoder[E, ZonedDateTime, F, T] = zonedDateTimeDecoder(Format(format))

  def zonedDateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, ZonedDateTime, F, T] =
    zonedDateTimeDecoder(Format(format))

  def zonedDateTimeDecoder(format: Format): Decoder[E, ZonedDateTime, F, T] =
    decoderFrom(StringDecoder.from(StringDecoder.makeSafe("ZonedDateTime")(s ⇒ format.parseZonedDateTime(s))))

  def defaultZonedDateTimeDecoder: Decoder[E, ZonedDateTime, F, T] =
    zonedDateTimeDecoder(Format.defaultZonedDateTimeFormat)

  // - Instant ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def instantDecoder(format: String): Decoder[E, Instant, F, T] = instantDecoder(Format(format))

  def instantDecoder(format: ⇒ DateTimeFormatter): Decoder[E, Instant, F, T] = instantDecoder(Format(format))

  def instantDecoder(format: Format): Decoder[E, Instant, F, T] =
    decoderFrom(StringDecoder.from(StringDecoder.makeSafe("Instant")(s ⇒ format.parseInstant(s))))

  def defaultInstantDecoder: Decoder[E, Instant, F, T] = instantDecoder(Format.defaultInstantFormat)

}
