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

package kantan.codecs.java8.strings

import java.time._
import java.time.format.DateTimeFormatter
import kantan.codecs.strings.{StringCodec, StringDecoder, StringEncoder}

trait TimeInstances {
  // - Instant codecs --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def instantDecoder(format: DateTimeFormatter): StringDecoder[Instant] =
    StringDecoder.from(StringDecoder.decoder("Instant")(s ⇒ Parse.instant(s, format)))

  def instantEncoder(format: DateTimeFormatter): StringEncoder[Instant] =
    StringEncoder.from(format.format)

  def instantCodec(format: DateTimeFormatter): StringCodec[Instant] =
    StringCodec.from(instantDecoder(format), instantEncoder(format))

  implicit val defaultInstantCodec: StringCodec[Instant] = instantCodec(DateTimeFormatter.ISO_INSTANT)



  // - LocalDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def localDateTimeDecoder(format: DateTimeFormatter): StringDecoder[LocalDateTime] =
    StringDecoder.from(StringDecoder.decoder("LocalDateTime")(s ⇒ LocalDateTime.parse(s, format)))

  def localDateTimeEncoder(format: DateTimeFormatter): StringEncoder[LocalDateTime] =
    StringEncoder.from(format.format)

  def localDateTimeCodec(format: DateTimeFormatter): StringCodec[LocalDateTime] =
    StringCodec.from(localDateTimeDecoder(format), localDateTimeEncoder(format))

  implicit val defaultLocalDateTimeCodec: StringCodec[LocalDateTime] =
    localDateTimeCodec(DateTimeFormatter.ISO_LOCAL_DATE_TIME)



  // - ZonedDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def zonedDateTimeDecoder(format: DateTimeFormatter): StringDecoder[ZonedDateTime] =
    StringDecoder.from(StringDecoder.decoder("ZonedDateTime")(s ⇒ ZonedDateTime.parse(s, format)))

  def zonedDateTimeEncoder(format: DateTimeFormatter): StringEncoder[ZonedDateTime] =
    StringEncoder.from(format.format)

  def zonedDateTimeCodec(format: DateTimeFormatter): StringCodec[ZonedDateTime] =
    StringCodec.from(zonedDateTimeDecoder(format), zonedDateTimeEncoder(format))

  implicit val defaultZonedDateTimeCodec: StringCodec[ZonedDateTime] =
    zonedDateTimeCodec(DateTimeFormatter.ISO_ZONED_DATE_TIME)


  // - OffsetDateTime codecs -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def offsetDateTimeDecoder(format: DateTimeFormatter): StringDecoder[OffsetDateTime] =
    StringDecoder.from(StringDecoder.decoder("OffsetDateTime")(s ⇒ OffsetDateTime.parse(s, format)))

  def offsetDateTimeEncoder(format: DateTimeFormatter): StringEncoder[OffsetDateTime] =
    StringEncoder.from(format.format)

  def offsetDateTimeCodec(format: DateTimeFormatter): StringCodec[OffsetDateTime] =
    StringCodec.from(offsetDateTimeDecoder(format), offsetDateTimeEncoder(format))

  implicit val defaultOffsetDateTimeCodec: StringCodec[OffsetDateTime] =
    offsetDateTimeCodec(DateTimeFormatter.ISO_OFFSET_DATE_TIME)


  // - LocalDate codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def localDateDecoder(format: DateTimeFormatter): StringDecoder[LocalDate] =
  StringDecoder.from(StringDecoder.decoder("LocalDate")(s ⇒ LocalDate.parse(s, format)))

  def localDateEncoder(format: DateTimeFormatter): StringEncoder[LocalDate] =
    StringEncoder.from(format.format)

  def localDateCodec(format: DateTimeFormatter): StringCodec[LocalDate] =
    StringCodec.from(localDateDecoder(format), localDateEncoder(format))

  implicit val defaultLocalDateCodec: StringCodec[LocalDate] = localDateCodec(DateTimeFormatter.ISO_LOCAL_DATE)


  // - LocalTime codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def localTimeDecoder(format: DateTimeFormatter): StringDecoder[LocalTime] =
    StringDecoder.from(StringDecoder.decoder("LocalTime")(s ⇒ LocalTime.parse(s, format)))

  def localTimeEncoder(format: DateTimeFormatter): StringEncoder[LocalTime] =
    StringEncoder.from(format.format)

  def localTimeCodec(format: DateTimeFormatter): StringCodec[LocalTime] =
    StringCodec.from(localTimeDecoder(format), localTimeEncoder(format))

  implicit val defaultLocalTimeCodec: StringCodec[LocalTime] = localTimeCodec(DateTimeFormatter.ISO_LOCAL_TIME)
}
