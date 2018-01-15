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
package joda
package time

import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.joda.time.format.DateTimeFormatter

trait JodaTimeEncoderCompanion[E, T] {
  def encoderFrom[D](d: StringEncoder[D]): Encoder[E, D, T]

  def dateTimeEncoder(format: String): Encoder[E, DateTime, T]              = dateTimeEncoder(Format(format))
  def dateTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, DateTime, T] = dateTimeEncoder(Format(format))
  def dateTimeEncoder(format: Format): Encoder[E, DateTime, T]              = encoderFrom(dateTimeStringEncoder(format))
  def defaultDateTimeEncoder: Encoder[E, DateTime, T]                       = dateTimeEncoder(defaultDateTimeFormat)

  def localDateTimeEncoder(format: String): Encoder[E, LocalDateTime, T] = localDateTimeEncoder(Format(format))
  def localDateTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, LocalDateTime, T] =
    localDateTimeEncoder(Format(format))
  def localDateTimeEncoder(format: Format): Encoder[E, LocalDateTime, T] =
    encoderFrom(localDateTimeStringEncoder(format))
  def defaultLocalDateTimeEncoder: Encoder[E, LocalDateTime, T] = localDateTimeEncoder(defaultLocalDateTimeFormat)

  def localDateEncoder(format: String): Encoder[E, LocalDate, T]              = localDateEncoder(Format(format))
  def localDateEncoder(format: ⇒ DateTimeFormatter): Encoder[E, LocalDate, T] = localDateEncoder(Format(format))
  def localDateEncoder(format: Format): Encoder[E, LocalDate, T]              = encoderFrom(localDateStringEncoder(format))
  def defaultLocalDateEncoder: Encoder[E, LocalDate, T]                       = localDateEncoder(defaultLocalDateFormat)

  def localTimeEncoder(format: String): Encoder[E, LocalTime, T]              = localTimeEncoder(Format(format))
  def localTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, LocalTime, T] = localTimeEncoder(Format(format))
  def localTimeEncoder(format: Format): Encoder[E, LocalTime, T]              = encoderFrom(localTimeStringEncoder(format))
  def defaultLocalTimeEncoder: Encoder[E, LocalTime, T]                       = localTimeEncoder(defaultLocalTimeFormat)
}
