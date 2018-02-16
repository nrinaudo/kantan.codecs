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

/** Provides useful methods for a java8 time codec companions.
  *
  * Usage note: when declaring default implicit instances, be sure to wrap them in an [[export.Exported]]. Otherwise,
  * custom instances and default ones are very likely to conflict.
  */
trait TimeCodecCompanion[E, F, T] extends TimeDecoderCompanion[E, F, T] with TimeEncoderCompanion[E, T] {

  // - LocalTime -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def localTimeCodec(format: String): Codec[E, LocalTime, F, T] = localTimeCodec(Format(format))

  def localTimeCodec(format: ⇒ DateTimeFormatter): Codec[E, LocalTime, F, T] = localTimeCodec(Format(format))

  def localTimeCodec(format: Format): Codec[E, LocalTime, F, T] =
    Codec.from(localTimeDecoder(format), localTimeEncoder(format))

  // - LocalDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def localDateTimeCodec(format: String): Codec[E, LocalDateTime, F, T] =
    localDateTimeCodec(Format(format))

  def localDateTimeCodec(format: ⇒ DateTimeFormatter): Codec[E, LocalDateTime, F, T] =
    localDateTimeCodec(Format(format))

  def localDateTimeCodec(format: Format): Codec[E, LocalDateTime, F, T] =
    Codec.from(localDateTimeDecoder(format), localDateTimeEncoder(format))

  // - LocalDate -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def localDateCodec(format: String): Codec[E, LocalDate, F, T] =
    localDateCodec(Format(format))

  def localDateCodec(format: ⇒ DateTimeFormatter): Codec[E, LocalDate, F, T] =
    localDateCodec(Format(format))

  def localDateCodec(format: Format): Codec[E, LocalDate, F, T] =
    Codec.from(localDateDecoder(format), localDateEncoder(format))

  // - Instant ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def instantCodec(format: String): Codec[E, Instant, F, T] =
    instantCodec(Format(format))

  def instantCodec(format: ⇒ DateTimeFormatter): Codec[E, Instant, F, T] =
    instantCodec(Format(format))

  def instantCodec(format: Format): Codec[E, Instant, F, T] =
    Codec.from(instantDecoder(format), instantEncoder(format))

  // - ZonedDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def zonedDateTimeCodec(format: String): Codec[E, ZonedDateTime, F, T] =
    zonedDateTimeCodec(Format(format))

  def zonedDateTimeCodec(format: ⇒ DateTimeFormatter): Codec[E, ZonedDateTime, F, T] =
    zonedDateTimeCodec(Format(format))

  def zonedDateTimeCodec(format: Format): Codec[E, ZonedDateTime, F, T] =
    Codec.from(zonedDateTimeDecoder(format), zonedDateTimeEncoder(format))

  // - OffsetDateTime --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def offsetDateTimeCodec(format: String): Codec[E, OffsetDateTime, F, T] =
    offsetDateTimeCodec(Format(format))

  def offsetDateTimeCodec(format: ⇒ DateTimeFormatter): Codec[E, OffsetDateTime, F, T] =
    offsetDateTimeCodec(Format(format))

  def offsetDateTimeCodec(format: Format): Codec[E, OffsetDateTime, F, T] =
    Codec.from(offsetDateTimeDecoder(format), offsetDateTimeEncoder(format))

}
