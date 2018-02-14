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

import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.joda.time.format.DateTimeFormatter

/** Provides useful methods for a joda-time codec companions.
  *
  * Usage note: when declaring default implicit instances, be sure to wrap them in an [[export.Exported]]. Otherwise,
  * custom instances and default ones are very likely to conflict.
  */
trait JodaTimeCodecCompanion[E, F, T] extends JodaTimeDecoderCompanion[E, F, T] with JodaTimeEncoderCompanion[E, T] {

  // - DateTime --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def dateTimeCodec(format: String): Codec[E, DateTime, F, T] = dateTimeCodec(Format(format))

  def dateTimeCodec(format: ⇒ DateTimeFormatter): Codec[E, DateTime, F, T] = dateTimeCodec(Format(format))

  def dateTimeCodec(format: Format): Codec[E, DateTime, F, T] =
    Codec.from(dateTimeDecoder(format), dateTimeEncoder(format))

  // - LocalDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def localDateTimeCodec[D](format: String): Codec[E, LocalDateTime, F, T] =
    localDateTimeCodec(Format(format))

  def localDateTimeCodec[D](format: ⇒ DateTimeFormatter): Codec[E, LocalDateTime, F, T] =
    localDateTimeCodec(Format(format))

  def localDateTimeCodec[D](format: Format): Codec[E, LocalDateTime, F, T] =
    Codec.from(localDateTimeDecoder(format), localDateTimeEncoder(format))

  // - LocalDate -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def localDateCodec[D](format: String): Codec[E, LocalDate, F, T] = localDateCodec(Format(format))

  def localDateCodec[D](format: ⇒ DateTimeFormatter): Codec[E, LocalDate, F, T] = localDateCodec(Format(format))

  def localDateCodec[D](format: Format): Codec[E, LocalDate, F, T] =
    Codec.from(localDateDecoder(format), localDateEncoder(format))

  // - LocalTime -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def localTimeCodec[D](format: String): Codec[E, LocalTime, F, T] = localTimeCodec(Format(format))

  def localTimeCodec[D](format: ⇒ DateTimeFormatter): Codec[E, LocalTime, F, T] = localTimeCodec(Format(format))

  def localTimeCodec[D](format: Format): Codec[E, LocalTime, F, T] =
    Codec.from(localTimeDecoder(format), localTimeEncoder(format))

}
