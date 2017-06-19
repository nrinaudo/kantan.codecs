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

import java.time._
import java.time.format.DateTimeFormatter

package object java8 extends TimeCodecCompanion[String, DecodeError, codecs.type] {
  // - Instant codecs --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultInstantFormat: Format = Format(DateTimeFormatter.ISO_INSTANT)

  def instantStringDecoder(format: Format): StringDecoder[Instant] =
    StringDecoder.from(StringDecoder.makeSafe("Instant")(s ⇒ format.parseInstant(s)))

  def instantStringEncoder(format: Format): StringEncoder[Instant] =
    StringEncoder.from(format.format)

  def instantStringCodec(format: Format): StringCodec[Instant] =
    StringCodec.from(instantStringDecoder(format), instantStringEncoder(format))

  implicit val defaultStringInstantCodec: StringCodec[Instant] = defaultInstantCodec



  // - LocalDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalDateTimeFormat: Format = Format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

  def localDateTimeStringDecoder(format: Format): StringDecoder[LocalDateTime] =
    StringDecoder.from(StringDecoder.makeSafe("LocalDateTime")(s ⇒ format.parseLocalDateTime(s)))

  def localDateTimeStringEncoder(format: Format): StringEncoder[LocalDateTime] =
    StringEncoder.from(format.format)

  def localDateTimeStringCodec(format: Format): StringCodec[LocalDateTime] =
    StringCodec.from(localDateTimeStringDecoder(format), localDateTimeStringEncoder(format))

  implicit val defaultStringLocalDateTimeCodec: StringCodec[LocalDateTime] = defaultLocalDateTimeCodec



  // - ZonedDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultZonedDateTimeFormat: Format = Format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
  def zonedDateTimeStringDecoder(format: Format): StringDecoder[ZonedDateTime] =
    StringDecoder.from(StringDecoder.makeSafe("ZonedDateTime")(s ⇒ format.parseZonedDateTime(s)))

  def zonedDateTimeStringEncoder(format: Format): StringEncoder[ZonedDateTime] =
    StringEncoder.from(format.format)

  def zonedDateTimeStringCodec(format: Format): StringCodec[ZonedDateTime] =
    StringCodec.from(zonedDateTimeStringDecoder(format), zonedDateTimeStringEncoder(format))

  implicit val defaultStringZonedDateTimeCodec: StringCodec[ZonedDateTime] = defaultZonedDateTimeCodec



  // - OffsetDateTime codecs -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultOffsetDateTimeFormat: Format = Format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

  def offsetDateTimeStringDecoder(format: Format): StringDecoder[OffsetDateTime] =
    StringDecoder.from(StringDecoder.makeSafe("OffsetDateTime")(s ⇒ format.parseOffsetDateTime(s)))

  def offsetDateTimeStringEncoder(format: Format): StringEncoder[OffsetDateTime] =
    StringEncoder.from(format.format)

  def offsetDateTimeStringCodec(format: Format): StringCodec[OffsetDateTime] =
    StringCodec.from(offsetDateTimeStringDecoder(format), offsetDateTimeStringEncoder(format))

  implicit val defaultStringOffsetDateTimeCodec: StringCodec[OffsetDateTime] = defaultOffsetDateTimeCodec



  // - LocalDate codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalDateFormat: Format = Format(DateTimeFormatter.ISO_LOCAL_DATE)

  def localDateStringDecoder(format: Format): StringDecoder[LocalDate] =
    StringDecoder.from(StringDecoder.makeSafe("LocalDate")(s ⇒ format.parseLocalDate(s)))

  def localDateStringEncoder(format: Format): StringEncoder[LocalDate] =
    StringEncoder.from(format.format)

  def localDateStringCodec(format: Format): StringCodec[LocalDate] =
    StringCodec.from(localDateStringDecoder(format), localDateStringEncoder(format))

  implicit val defaultStringLocalDateCodec: StringCodec[LocalDate] = defaultLocalDateCodec



  // - LocalTime codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalTimeFormat: Format = Format(DateTimeFormatter.ISO_LOCAL_TIME)

  def localTimeStringDecoder(format: Format): StringDecoder[LocalTime] =
    StringDecoder.from(StringDecoder.makeSafe("LocalTime")(s ⇒ format.parseLocalTime(s)))

  def localTimeStringEncoder(format: Format): StringEncoder[LocalTime] =
    StringEncoder.from(format.format)

  def localTimeStringCodec(format: Format): StringCodec[LocalTime] =
    StringCodec.from(localTimeStringDecoder(format), localTimeStringEncoder(format))

  implicit val defaultStringLocalTimeCodec: StringCodec[LocalTime] = defaultLocalTimeCodec



  // - TimeCodecCompanion implementation -------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  override def encoderFrom[D](d: StringEncoder[D]) = d
  override def decoderFrom[D](d: StringDecoder[D]) = d
}
