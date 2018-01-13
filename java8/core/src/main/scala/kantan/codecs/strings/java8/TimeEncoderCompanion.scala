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
import strings.StringEncoder

trait TimeEncoderCompanion[E, T] {
  def encoderFrom[D](d: StringEncoder[D]): Encoder[E, D, T]

  def localTimeEncoder(format: String): Encoder[E, LocalTime, T]              = localTimeEncoder(Format(format))
  def localTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, LocalTime, T] = localTimeEncoder(Format(format))
  def localTimeEncoder(format: Format): Encoder[E, LocalTime, T]              = encoderFrom(localTimeStringEncoder(format))
  def defaultLocalTimeEncoder: Encoder[E, LocalTime, T]                       = localTimeEncoder(defaultLocalTimeFormat)

  def localDateEncoder(format: String): Encoder[E, LocalDate, T]              = localDateEncoder(Format(format))
  def localDateEncoder(format: ⇒ DateTimeFormatter): Encoder[E, LocalDate, T] = localDateEncoder(Format(format))
  def localDateEncoder(format: Format): Encoder[E, LocalDate, T]              = encoderFrom(localDateStringEncoder(format))
  def defaultLocalDateEncoder: Encoder[E, LocalDate, T]                       = localDateEncoder(defaultLocalDateFormat)

  def localDateTimeEncoder(format: String): Encoder[E, LocalDateTime, T] = localDateTimeEncoder(Format(format))
  def localDateTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, LocalDateTime, T] =
    localDateTimeEncoder(Format(format))
  def localDateTimeEncoder(format: Format): Encoder[E, LocalDateTime, T] = encoderFrom(localDateTimeStringCodec(format))
  def defaultLocalDateTimeEncoder: Encoder[E, LocalDateTime, T] =
    localDateTimeEncoder(defaultLocalDateTimeFormat)

  def offsetDateTimeEncoder(format: String): Encoder[E, OffsetDateTime, T] = offsetDateTimeEncoder(Format(format))
  def offsetDateTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, OffsetDateTime, T] =
    offsetDateTimeEncoder(Format(format))
  def offsetDateTimeEncoder(format: Format): Encoder[E, OffsetDateTime, T] =
    encoderFrom(offsetDateTimeStringCodec(format))
  def defaultOffsetDateTimeEncoder: Encoder[E, OffsetDateTime, T] =
    offsetDateTimeEncoder(defaultOffsetDateTimeFormat)

  def zonedDateTimeEncoder(format: String): Encoder[E, ZonedDateTime, T] = zonedDateTimeEncoder(Format(format))
  def zonedDateTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, ZonedDateTime, T] =
    zonedDateTimeEncoder(Format(format))
  def zonedDateTimeEncoder(format: Format): Encoder[E, ZonedDateTime, T] =
    encoderFrom(zonedDateTimeStringCodec(format))
  def defaultZonedDateTimeEncoder: Encoder[E, ZonedDateTime, T] =
    zonedDateTimeEncoder(defaultZonedDateTimeFormat)

  def instantEncoder(format: String): Encoder[E, Instant, T]              = instantEncoder(Format(format))
  def instantEncoder(format: ⇒ DateTimeFormatter): Encoder[E, Instant, T] = instantEncoder(Format(format))
  def instantEncoder(format: Format): Encoder[E, Instant, T] =
    encoderFrom(instantStringCodec(format))
  def defaultInstantEncoder: Encoder[E, Instant, T] = instantEncoder(defaultInstantFormat)
}
