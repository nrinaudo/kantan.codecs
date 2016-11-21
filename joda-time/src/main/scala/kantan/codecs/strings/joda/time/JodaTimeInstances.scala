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

import kantan.codecs.{Decoder, Encoder}
import kantan.codecs.export.Exported
import kantan.codecs.strings.{StringCodec, StringDecoder, StringEncoder}
import org.joda.time._
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}

trait JodaTimeInstances {
  // - DateTime codecs -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def dateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[DateTime] =
    StringDecoder.from(StringDecoder.decoder("DateTime")(format.parseDateTime))

  def dateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[DateTime] = StringEncoder.from(format.print)

  def dateTimeStringCodec(format: DateTimeFormatter): StringCodec[DateTime] =
    StringCodec.from(dateTimeStringDecoder(format), dateTimeStringEncoder(format))

  implicit val defaultDateTimeStringCodec: StringCodec[DateTime] =
    dateTimeStringCodec(ISODateTimeFormat.dateTime())


  // - LocalDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
   def localDateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[LocalDateTime] =
   StringDecoder.from(StringDecoder.decoder("LocaleDateTime")(format.parseLocalDateTime))

   def localDateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[LocalDateTime] =
     StringEncoder.from(format.print)

   def localDateTimeStringCodec(format: DateTimeFormatter): StringCodec[LocalDateTime] =
     StringCodec.from(localDateTimeStringDecoder(format), localDateTimeStringEncoder(format))

  implicit val defaultLocalDateTimeStringCodec: StringCodec[LocalDateTime] = StringCodec.from(
    localDateTimeStringDecoder(ISODateTimeFormat.localDateOptionalTimeParser()),
    localDateTimeStringEncoder(ISODateTimeFormat.dateTime())
  )




  // - LocalDate codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def localDateStringDecoder(format: DateTimeFormatter): StringDecoder[LocalDate] =
    StringDecoder.from(StringDecoder.decoder("LocaleDate")(format.parseLocalDate))

  def localDateStringEncoder(format: DateTimeFormatter): StringEncoder[LocalDate] = StringEncoder.from(format.print)

  def localDateStringCodec(format: DateTimeFormatter): StringCodec[LocalDate] =
    StringCodec.from(localDateStringDecoder(format), localDateStringEncoder(format))

  implicit val defaultLocalDateStringCodec: StringCodec[LocalDate] = StringCodec.from(
    localDateStringDecoder(ISODateTimeFormat.localDateParser()),
    localDateStringEncoder(ISODateTimeFormat.date())
  )



  // - LocalTime codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def localTimeStringDecoder(format: DateTimeFormatter): StringDecoder[LocalTime] =
    StringDecoder.from(StringDecoder.decoder("LocaleTime")(format.parseLocalTime))

  def localTimeStringEncoder(format: DateTimeFormatter): StringEncoder[LocalTime] = StringEncoder.from(format.print)

  def localTimeStringCodec(format: DateTimeFormatter): StringCodec[LocalTime] =
    StringCodec.from(localTimeStringDecoder(format), localTimeStringEncoder(format))

  implicit val defaultLocalTimeStringCodec: StringCodec[LocalTime] = StringCodec.from(
    localTimeStringDecoder(ISODateTimeFormat.localTimeParser()),
    localTimeStringEncoder(ISODateTimeFormat.time())
  )
}

trait JodaTimeDecoderCompanion[E, F, T] {
  def decoderFrom[D](d: StringDecoder[D]): Decoder[E, D, F, T]

  implicit val defaultDateTimeDecoder: Exported[Decoder[E, DateTime, F, T]] =
    Exported(decoderFrom(defaultDateTimeStringCodec))
  def dateTimeDecoder(format: DateTimeFormatter): Decoder[E, DateTime, F, T] =
    decoderFrom(dateTimeStringDecoder(format))

  implicit val defaultLocalDateTimeDecoder: Exported[Decoder[E, LocalDateTime, F, T]] =
    Exported(decoderFrom(defaultLocalDateTimeStringCodec))
  def localDateTimeDecoder(format: DateTimeFormatter): Decoder[E, LocalDateTime, F, T] =
    decoderFrom(localDateTimeStringDecoder(format))

  implicit val defaultLocalDateDecoder: Exported[Decoder[E, LocalDate, F, T]] =
    Exported(decoderFrom(defaultLocalDateStringCodec))
  def localDateDecoder(format: DateTimeFormatter): Decoder[E, LocalDate, F, T] =
    decoderFrom(localDateStringDecoder(format))

  implicit val defaultLocalTimeDecoder: Exported[Decoder[E, LocalTime, F, T]] =
    Exported(decoderFrom(defaultLocalTimeStringCodec))
  def localTimeDecoder(format: DateTimeFormatter): Decoder[E, LocalTime, F, T] =
    decoderFrom(localTimeStringDecoder(format))
}

trait JodaTimeEncoderCompanion[E, T] {
  def encoderFrom[D](d: StringEncoder[D]): Encoder[E, D, T]

  implicit val defaultDateTimeEncoder: Exported[Encoder[E, DateTime, T]] =
      Exported(encoderFrom(defaultDateTimeStringCodec))
  def dateTimeEncoder(format: DateTimeFormatter): Encoder[E, DateTime, T] = encoderFrom(dateTimeStringEncoder(format))

  implicit val defaultLocalDateTimeEncoder: Exported[Encoder[E, LocalDateTime, T]] =
    Exported(encoderFrom(defaultLocalDateTimeStringCodec))
  def localDateTimeEncoder(format: DateTimeFormatter): Encoder[E, LocalDateTime, T] =
    encoderFrom(localDateTimeStringEncoder(format))

  implicit val defaultLocalDateEncoder: Exported[Encoder[E, LocalDate, T]] =
    Exported(encoderFrom(defaultLocalDateStringCodec))
  def localDateEncoder(format: DateTimeFormatter): Encoder[E, LocalDate, T] =
    encoderFrom(localDateStringEncoder(format))

  implicit val defaultLocalTimeEncoder: Exported[Encoder[E, LocalTime, T]] =
    Exported(encoderFrom(defaultLocalTimeStringCodec))
  def localTimeEncoder(format: DateTimeFormatter): Encoder[E, LocalTime, T] =
    encoderFrom(localTimeStringEncoder(format))
}

trait JodaTimeCodecCompanion[E, F, T] extends JodaTimeDecoderCompanion[E, F, T]
                                              with JodaTimeEncoderCompanion[E, T]
