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

package kantan.codecs.strings.joda

import kantan.codecs.strings._
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}

package object time extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
  // - DateTime codecs -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultDateTimeFormat: Format = Format(ISODateTimeFormat.dateTime())

  def dateTimeStringDecoder(format: Format): StringDecoder[DateTime] =
    StringDecoder.from(StringDecoder.makeSafe("DateTime")(format.parseDateTime))

  def dateTimeStringEncoder(format: Format): StringEncoder[DateTime] = StringEncoder.from(format.format)

  def dateTimeStringCodec(format: Format): StringCodec[DateTime] =
    StringCodec.from(dateTimeStringDecoder(format), dateTimeStringEncoder(format))

  implicit val defaultStringDateTimeCodec: StringCodec[DateTime] = defaultDateTimeCodec

  // - LocalDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalDateTimeFormat: Format = Format(
    new DateTimeFormatter(ISODateTimeFormat.dateTime().getPrinter,
                          ISODateTimeFormat.localDateOptionalTimeParser().getParser)
  )

  def localDateTimeStringDecoder(format: Format): StringDecoder[LocalDateTime] =
    StringDecoder.from(StringDecoder.makeSafe("LocaleDateTime")(format.parseLocalDateTime))

  def localDateTimeStringEncoder(format: Format): StringEncoder[LocalDateTime] = StringEncoder.from(format.format)

  def localDateTimeStringCodec(format: Format): StringCodec[LocalDateTime] =
    StringCodec.from(localDateTimeStringDecoder(format), localDateTimeStringEncoder(format))

  implicit val defaultStringLocalDateTimeCodec: StringCodec[LocalDateTime] = defaultLocalDateTimeCodec

  // - LocalDate codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalDateFormat: Format = Format(
    new DateTimeFormatter(ISODateTimeFormat.date().getPrinter, ISODateTimeFormat.localDateParser().getParser)
  )

  def localDateStringDecoder(format: Format): StringDecoder[LocalDate] =
    StringDecoder.from(StringDecoder.makeSafe("LocaleDate")(format.parseLocalDate))

  def localDateStringEncoder(format: Format): StringEncoder[LocalDate] = StringEncoder.from(format.format)

  def localDateStringCodec(format: Format): StringCodec[LocalDate] =
    StringCodec.from(localDateStringDecoder(format), localDateStringEncoder(format))

  implicit val defaultStringLocalDateCodec: StringCodec[LocalDate] = defaultLocalDateCodec

  // - LocalTime codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalTimeFormat: Format = Format(
    new DateTimeFormatter(ISODateTimeFormat.time().getPrinter, ISODateTimeFormat.localTimeParser().getParser)
  )

  def localTimeStringDecoder(format: Format): StringDecoder[LocalTime] =
    StringDecoder.from(StringDecoder.makeSafe("LocaleTime")(format.parseLocalTime))

  def localTimeStringEncoder(format: Format): StringEncoder[LocalTime] = StringEncoder.from(format.format)

  def localTimeStringCodec(format: Format): StringCodec[LocalTime] =
    StringCodec.from(localTimeStringDecoder(format), localTimeStringEncoder(format))

  implicit val defaultStringLocalTimeCodec: StringCodec[LocalTime] = defaultLocalTimeCodec

  // - JodaTimeCodecCompanion implementation ---------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  override def encoderFrom[D](d: StringEncoder[D]) = d
  override def decoderFrom[D](d: StringDecoder[D]) = d
}
