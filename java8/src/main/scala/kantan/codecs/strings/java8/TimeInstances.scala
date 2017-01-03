/*
 * Copyright 2017 Nicolas Rinaudo
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
import kantan.codecs.{Codec, Decoder, Encoder}
import kantan.codecs.export.Exported
import kantan.codecs.strings.{StringCodec, StringDecoder, StringEncoder}

trait TimeInstances {
  // - Instant codecs --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultInstantFormat: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

  def instantStringDecoder(format: DateTimeFormatter): StringDecoder[Instant] =
    StringDecoder.from(StringDecoder.decoder("Instant")(s ⇒ Parse.instant(s, format)))

  def instantStringEncoder(format: DateTimeFormatter): StringEncoder[Instant] =
    StringEncoder.from(format.format)

  def instantStringCodec(format: DateTimeFormatter): StringCodec[Instant] =
    StringCodec.from(instantStringDecoder(format), instantStringEncoder(format))

  implicit val defaultInstantStringCodec: StringCodec[Instant] = instantStringCodec(defaultInstantFormat)



  // - LocalDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalDateTimeFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  def localDateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[LocalDateTime] =
  StringDecoder.from(StringDecoder.decoder("LocalDateTime")(s ⇒ LocalDateTime.parse(s, format)))

  def localDateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[LocalDateTime] =
    StringEncoder.from(format.format)

  def localDateTimeStringCodec(format: DateTimeFormatter): StringCodec[LocalDateTime] =
    StringCodec.from(localDateTimeStringDecoder(format), localDateTimeStringEncoder(format))

  implicit val defaultLocalDateTimeStringCodec: StringCodec[LocalDateTime] =
    localDateTimeStringCodec(defaultLocalDateTimeFormat)



  // - ZonedDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultZonedDateTimeFormat: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

  def zonedDateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[ZonedDateTime] =
  StringDecoder.from(StringDecoder.decoder("ZonedDateTime")(s ⇒ ZonedDateTime.parse(s, format)))

  def zonedDateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[ZonedDateTime] =
    StringEncoder.from(format.format)

  def zonedDateTimeStringCodec(format: DateTimeFormatter): StringCodec[ZonedDateTime] =
    StringCodec.from(zonedDateTimeStringDecoder(format), zonedDateTimeStringEncoder(format))

  implicit val defaultZonedDateTimeStringCodec: StringCodec[ZonedDateTime] =
    zonedDateTimeStringCodec(defaultZonedDateTimeFormat)


  // - OffsetDateTime codecs -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultOffsetDateTimeFormat: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  def offsetDateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[OffsetDateTime] =
    StringDecoder.from(StringDecoder.decoder("OffsetDateTime")(s ⇒ OffsetDateTime.parse(s, format)))

  def offsetDateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[OffsetDateTime] =
    StringEncoder.from(format.format)

  def offsetDateTimeStringCodec(format: DateTimeFormatter): StringCodec[OffsetDateTime] =
    StringCodec.from(offsetDateTimeStringDecoder(format), offsetDateTimeStringEncoder(format))

  implicit val defaultOffsetDateTimeStringCodec: StringCodec[OffsetDateTime] =
    offsetDateTimeStringCodec(defaultOffsetDateTimeFormat)


  // - LocalDate codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalDateFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  def localDateStringDecoder(format: DateTimeFormatter): StringDecoder[LocalDate] =
    StringDecoder.from(StringDecoder.decoder("LocalDate")(s ⇒ LocalDate.parse(s, format)))

  def localDateStringEncoder(format: DateTimeFormatter): StringEncoder[LocalDate] =
    StringEncoder.from(format.format)

  def localDateStringCodec(format: DateTimeFormatter): StringCodec[LocalDate] =
    StringCodec.from(localDateStringDecoder(format), localDateStringEncoder(format))

  implicit val defaultLocalDateStringCodec: StringCodec[LocalDate] =
    localDateStringCodec(defaultLocalDateFormat)


  // - LocalTime codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalTimeFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

  def localTimeStringDecoder(format: DateTimeFormatter): StringDecoder[LocalTime] =
    StringDecoder.from(StringDecoder.decoder("LocalTime")(s ⇒ LocalTime.parse(s, format)))

  def localTimeStringEncoder(format: DateTimeFormatter): StringEncoder[LocalTime] =
    StringEncoder.from(format.format)

  def localTimeStringCodec(format: DateTimeFormatter): StringCodec[LocalTime] =
    StringCodec.from(localTimeStringDecoder(format), localTimeStringEncoder(format))

  implicit val defaultLocalTimeStringCodec: StringCodec[LocalTime] =
    localTimeStringCodec(defaultLocalTimeFormat)
}

trait TimeDecoderCompanion[E, F, T] {
  def decoderFrom[D](d: StringDecoder[D]): Decoder[E, D, F, T]

  def localTimeDecoder(format: DateTimeFormatter): Decoder[E, LocalTime, F, T] =
    decoderFrom(localTimeStringDecoder(format))
  implicit val defaultLocalTimeDecoder: Exported[Decoder[E, LocalTime, F, T]] =
    Exported(localTimeDecoder(defaultLocalTimeFormat))

  def localDateDecoder(format: DateTimeFormatter): Decoder[E, LocalDate, F, T] =
    decoderFrom(localDateStringCodec(format))
  implicit val defaultLocalDateDecoder: Exported[Decoder[E, LocalDate, F, T]] =
    Exported(localDateDecoder(defaultLocalDateFormat))

  def localDateTimeDecoder(format: DateTimeFormatter): Decoder[E, LocalDateTime, F, T] =
    decoderFrom(localDateTimeStringCodec(format))
  implicit val defaultLocalDateTimeDecoder: Exported[Decoder[E, LocalDateTime, F, T]] =
    Exported(localDateTimeDecoder(defaultLocalDateTimeFormat))

  def offsetDateTimeDecoder(format: DateTimeFormatter): Decoder[E, OffsetDateTime, F, T] =
    decoderFrom(offsetDateTimeStringCodec(format))
  implicit val defaultOffsetDateTimeDecoder: Exported[Decoder[E, OffsetDateTime, F, T]] =
    Exported(offsetDateTimeDecoder(defaultOffsetDateTimeFormat))

  def zonedDateTimeDecoder(format: DateTimeFormatter): Decoder[E, ZonedDateTime, F, T] =
    decoderFrom(zonedDateTimeStringCodec(format))
  implicit val defaultZonedDateTimeDecoder: Exported[Decoder[E, ZonedDateTime, F, T]] =
    Exported(zonedDateTimeDecoder(defaultZonedDateTimeFormat))

  def instantDecoder(format: DateTimeFormatter): Decoder[E, Instant, F, T] =
    decoderFrom(instantStringCodec(format))
  implicit val defaultInstantDecoder: Exported[Decoder[E, Instant, F, T]] =
    Exported(instantDecoder(defaultInstantFormat))
}

trait TimeEncoderCompanion[E, T] {
  def encoderFrom[D](d: StringEncoder[D]): Encoder[E, D, T]

  def localTimeEncoder(format: DateTimeFormatter): Encoder[E, LocalTime, T] =
    encoderFrom(localTimeStringEncoder(format))
  implicit val defaultLocalTimeEncoder: Exported[Encoder[E, LocalTime, T]] =
    Exported(localTimeEncoder(defaultLocalTimeFormat))

  def localDateEncoder(format: DateTimeFormatter): Encoder[E, LocalDate, T] =
    encoderFrom(localDateStringEncoder(format))
  implicit val defaultLocalDateEncoder: Exported[Encoder[E, LocalDate, T]] =
    Exported(localDateEncoder(defaultLocalDateFormat))

  def localDateTimeEncoder(format: DateTimeFormatter): Encoder[E, LocalDateTime, T] =
    encoderFrom(localDateTimeStringCodec(format))
  implicit val defaultLocalDateTimeEncoder: Exported[Encoder[E, LocalDateTime, T]] =
    Exported(localDateTimeEncoder(defaultLocalDateTimeFormat))

  def offsetDateTimeEncoder(format: DateTimeFormatter): Encoder[E, OffsetDateTime, T] =
    encoderFrom(offsetDateTimeStringCodec(format))
  implicit val defaultOffsetDateTimeEncoder: Exported[Encoder[E, OffsetDateTime, T]] =
    Exported(offsetDateTimeEncoder(defaultOffsetDateTimeFormat))

  def zonedDateTimeEncoder(format: DateTimeFormatter): Encoder[E, ZonedDateTime, T] =
    encoderFrom(zonedDateTimeStringCodec(format))
  implicit val defaultZonedDateTimeEncoder: Exported[Encoder[E, ZonedDateTime, T]] =
    Exported(zonedDateTimeEncoder(defaultZonedDateTimeFormat))

  def instantEncoder(format: DateTimeFormatter): Encoder[E, Instant, T] = encoderFrom(instantStringCodec(format))
  implicit val defaultInstantEncoder: Exported[Encoder[E, Instant, T]] =
    Exported(instantEncoder(defaultInstantFormat))
}

trait TimeCodecCompanion[E, F, T] extends TimeDecoderCompanion[E, F, T] with TimeEncoderCompanion[E, T] {
  def localTimeCodec(format: DateTimeFormatter): Codec[E, LocalTime, F, T] =
    Codec.from(localTimeDecoder(format), localTimeEncoder(format))

  def localDateTimeCodec(format: DateTimeFormatter): Codec[E, LocalDateTime, F, T] =
    Codec.from(localDateTimeDecoder(format), localDateTimeEncoder(format))

  def localDateCodec(format: DateTimeFormatter): Codec[E, LocalDate, F, T] =
    Codec.from(localDateDecoder(format), localDateEncoder(format))

  def instantCodec(format: DateTimeFormatter): Codec[E, Instant, F, T] =
    Codec.from(instantDecoder(format), instantEncoder(format))

  def zonedDateTimeCodec(format: DateTimeFormatter): Codec[E, ZonedDateTime, F, T] =
    Codec.from(zonedDateTimeDecoder(format), zonedDateTimeEncoder(format))

  def offsetDateTimeCodec(format: DateTimeFormatter): Codec[E, OffsetDateTime, F, T] =
    Codec.from(offsetDateTimeDecoder(format), offsetDateTimeEncoder(format))
}
