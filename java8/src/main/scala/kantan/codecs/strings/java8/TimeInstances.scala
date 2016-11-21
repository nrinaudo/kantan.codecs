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

package kantan.codecs.strings.java8

import java.time._
import java.time.format.DateTimeFormatter
import kantan.codecs.{Decoder => RawDecoder, Encoder => RawEncoder}
import kantan.codecs.export.Exported
import kantan.codecs.strings.{StringCodec, StringDecoder, StringEncoder}

trait TimeInstances {
  // - Instant codecs --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def instantStringDecoder(format: DateTimeFormatter): StringDecoder[Instant] =
    StringDecoder.from(StringDecoder.decoder("Instant")(s ⇒ Parse.instant(s, format)))

  def instantStringEncoder(format: DateTimeFormatter): StringEncoder[Instant] =
    StringEncoder.from(format.format)

  def instantStringCodec(format: DateTimeFormatter): StringCodec[Instant] =
    StringCodec.from(instantStringDecoder(format), instantStringEncoder(format))

  implicit val defaultInstantStringCodec: StringCodec[Instant] = instantStringCodec(DateTimeFormatter.ISO_INSTANT)



  // - LocalDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def localDateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[LocalDateTime] =
  StringDecoder.from(StringDecoder.decoder("LocalDateTime")(s ⇒ LocalDateTime.parse(s, format)))

  def localDateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[LocalDateTime] =
    StringEncoder.from(format.format)

  def localDateTimeStringCodec(format: DateTimeFormatter): StringCodec[LocalDateTime] =
    StringCodec.from(localDateTimeStringDecoder(format), localDateTimeStringEncoder(format))

  implicit val defaultLocalDateTimeStringCodec: StringCodec[LocalDateTime] =
    localDateTimeStringCodec(DateTimeFormatter.ISO_LOCAL_DATE_TIME)



  // - ZonedDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def zonedDateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[ZonedDateTime] =
  StringDecoder.from(StringDecoder.decoder("ZonedDateTime")(s ⇒ ZonedDateTime.parse(s, format)))

  def zonedDateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[ZonedDateTime] =
    StringEncoder.from(format.format)

  def zonedDateTimeStringCodec(format: DateTimeFormatter): StringCodec[ZonedDateTime] =
    StringCodec.from(zonedDateTimeStringDecoder(format), zonedDateTimeStringEncoder(format))

  implicit val defaultZonedDateTimeStringCodec: StringCodec[ZonedDateTime] =
    zonedDateTimeStringCodec(DateTimeFormatter.ISO_ZONED_DATE_TIME)


  // - OffsetDateTime codecs -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def offsetDateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[OffsetDateTime] =
  StringDecoder.from(StringDecoder.decoder("OffsetDateTime")(s ⇒ OffsetDateTime.parse(s, format)))

  def offsetDateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[OffsetDateTime] =
    StringEncoder.from(format.format)

  def offsetDateTimeStringCodec(format: DateTimeFormatter): StringCodec[OffsetDateTime] =
    StringCodec.from(offsetDateTimeStringDecoder(format), offsetDateTimeStringEncoder(format))

  implicit val defaultOffsetDateTimeStringCodec: StringCodec[OffsetDateTime] =
    offsetDateTimeStringCodec(DateTimeFormatter.ISO_OFFSET_DATE_TIME)


  // - LocalDate codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def localDateStringDecoder(format: DateTimeFormatter): StringDecoder[LocalDate] =
  StringDecoder.from(StringDecoder.decoder("LocalDate")(s ⇒ LocalDate.parse(s, format)))

  def localDateStringEncoder(format: DateTimeFormatter): StringEncoder[LocalDate] =
    StringEncoder.from(format.format)

  def localDateStringCodec(format: DateTimeFormatter): StringCodec[LocalDate] =
    StringCodec.from(localDateStringDecoder(format), localDateStringEncoder(format))

  implicit val defaultLocalDateStringCodec: StringCodec[LocalDate] =
    localDateStringCodec(DateTimeFormatter.ISO_LOCAL_DATE)


  // - LocalTime codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def localTimeStringDecoder(format: DateTimeFormatter): StringDecoder[LocalTime] =
  StringDecoder.from(StringDecoder.decoder("LocalTime")(s ⇒ LocalTime.parse(s, format)))

  def localTimeStringEncoder(format: DateTimeFormatter): StringEncoder[LocalTime] =
    StringEncoder.from(format.format)

  def localTimeStringCodec(format: DateTimeFormatter): StringCodec[LocalTime] =
    StringCodec.from(localTimeStringDecoder(format), localTimeStringEncoder(format))

  implicit val defaultLocalTimeStringCodec: StringCodec[LocalTime] =
    localTimeStringCodec(DateTimeFormatter.ISO_LOCAL_TIME)
}


trait TimeDecoderCompanion[E, F, T] {
  type Decoder[D] <: RawDecoder[E, D, F, T]

  def decoderFrom[D](d: StringDecoder[D]): Decoder[D]

  implicit val defaultLocalTimeDecoder: Exported[Decoder[LocalTime]] =
    Exported(decoderFrom(defaultLocalTimeStringCodec))
  def localTimeDecoder(format: DateTimeFormatter): Decoder[LocalTime] = decoderFrom(localTimeStringDecoder(format))

  implicit val defaultLocalDateDecoder: Exported[Decoder[LocalDate]] =
    Exported(decoderFrom(defaultLocalDateStringCodec))
  def localDateDecoder(format: DateTimeFormatter): Decoder[LocalDate] = decoderFrom(localDateStringCodec(format))

  implicit val defaultLocalDateTimeDecoder: Exported[Decoder[LocalDateTime]] =
    Exported(decoderFrom(defaultLocalDateTimeStringCodec))
  def localDateTimeDecoder(format: DateTimeFormatter): Decoder[LocalDateTime] =
    decoderFrom(localDateTimeStringCodec(format))

  implicit val defaultOffsetDateTimeDecoder: Exported[Decoder[OffsetDateTime]] =
    Exported(decoderFrom(defaultOffsetDateTimeStringCodec))
  def offsetDateTimeDecoder(format: DateTimeFormatter): Decoder[OffsetDateTime] =
    decoderFrom(offsetDateTimeStringCodec(format))

  implicit val defaultZonedDateTimeDecoder: Exported[Decoder[ZonedDateTime]] =
    Exported(decoderFrom(defaultZonedDateTimeStringCodec))
  def zonedDateTimeDecoder(format: DateTimeFormatter): Decoder[ZonedDateTime] =
    decoderFrom(zonedDateTimeStringCodec(format))

  implicit val defaultInstantDecoder: Exported[Decoder[Instant]] =
    Exported(decoderFrom(defaultInstantStringCodec))
  def instantDecoder(format: DateTimeFormatter): Decoder[Instant] = decoderFrom(instantStringCodec(format))
}

trait TimeEncoderCompanion[E, T] {
  type Encoder[D] <: RawEncoder[E, D, T]

  def encoderFrom[D](d: StringEncoder[D]): Encoder[D]

  implicit val defaultLocalTimeEncoder: Exported[Encoder[LocalTime]] =
  Exported(encoderFrom(defaultLocalTimeStringCodec))
  def localTimeEncoder(format: DateTimeFormatter): Encoder[LocalTime] = encoderFrom(localTimeStringEncoder(format))

  implicit val defaultLocalDateEncoder: Exported[Encoder[LocalDate]] =
    Exported(encoderFrom(defaultLocalDateStringCodec))
  def localDateEncoder(format: DateTimeFormatter): Encoder[LocalDate] = encoderFrom(localDateStringEncoder(format))

  implicit val defaultLocalDateTimeEncoder: Exported[Encoder[LocalDateTime]] =
    Exported(encoderFrom(defaultLocalDateTimeStringCodec))
  def localDateTimeEncoder(format: DateTimeFormatter): Encoder[LocalDateTime] =
    encoderFrom(localDateTimeStringCodec(format))

  implicit val defaultOffsetDateTimeEncoder: Exported[Encoder[OffsetDateTime]] =
    Exported(encoderFrom(defaultOffsetDateTimeStringCodec))
  def offsetDateTimeEncoder(format: DateTimeFormatter): Encoder[OffsetDateTime] =
    encoderFrom(offsetDateTimeStringCodec(format))

  implicit val defaultZonedDateTimeEncoder: Exported[Encoder[ZonedDateTime]] =
    Exported(encoderFrom(defaultZonedDateTimeStringCodec))
  def zonedDateTimeEncoder(format: DateTimeFormatter): Encoder[ZonedDateTime] =
    encoderFrom(zonedDateTimeStringCodec(format))

  implicit val defaultInstantEncoder: Exported[Encoder[Instant]] =
    Exported(encoderFrom(defaultInstantStringCodec))
  def instantEncoder(format: DateTimeFormatter): Encoder[Instant] = encoderFrom(instantStringCodec(format))
}

trait TimeCodecCompanion[E, F, T] extends TimeDecoderCompanion[E, F, T] with TimeEncoderCompanion[E, T]
