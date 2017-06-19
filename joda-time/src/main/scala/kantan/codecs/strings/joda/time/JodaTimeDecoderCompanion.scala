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

package kantan.codecs.strings.joda.time

import kantan.codecs.Decoder
import kantan.codecs.strings.StringDecoder
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.joda.time.format.DateTimeFormatter

trait JodaTimeDecoderCompanion[E, F, T] {
  def decoderFrom[D](d: StringDecoder[D]): Decoder[E, D, F, T]

  def dateTimeDecoder(format: String): Decoder[E, DateTime, F, T] = dateTimeDecoder(Format(format))
  def dateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, DateTime, F, T] = dateTimeDecoder(Format(format))
  def dateTimeDecoder(format: Format): Decoder[E, DateTime, F, T] = decoderFrom(dateTimeStringDecoder(format))
  def defaultDateTimeDecoder: Decoder[E, DateTime, F, T] = dateTimeDecoder(defaultDateTimeFormat)

  def localDateTimeDecoder(format: String): Decoder[E, LocalDateTime, F, T] = localDateTimeDecoder(Format(format))
  def localDateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalDateTime, F, T] =
    localDateTimeDecoder(Format(format))
  def localDateTimeDecoder(format: Format): Decoder[E, LocalDateTime, F, T] =
    decoderFrom(localDateTimeStringDecoder(format))
  def defaultLocalDateTimeDecoder: Decoder[E, LocalDateTime, F, T] = localDateTimeDecoder(defaultLocalDateTimeFormat)

  def localDateDecoder(format: String): Decoder[E, LocalDate, F, T] = localDateDecoder(Format(format))
  def localDateDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalDate, F, T] = localDateDecoder(Format(format))
  def localDateDecoder(format: Format): Decoder[E, LocalDate, F, T] = decoderFrom(localDateStringDecoder(format))
  def defaultLocalDateDecoder: Decoder[E, LocalDate, F, T] = localDateDecoder(defaultLocalDateFormat)

  def localTimeDecoder(format: String): Decoder[E, LocalTime, F, T] = localTimeDecoder(Format(format))
  def localTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalTime, F, T] = localTimeDecoder(Format(format))
  def localTimeDecoder(format: Format): Decoder[E, LocalTime, F, T] = decoderFrom(localTimeStringDecoder(format))
  def defaultLocalTimeDecoder: Decoder[E, LocalTime, F, T] = localTimeDecoder(defaultLocalTimeFormat)
}
