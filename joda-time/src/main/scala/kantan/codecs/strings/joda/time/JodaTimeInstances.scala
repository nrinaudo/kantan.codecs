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

import kantan.codecs.{Codec, Decoder, Encoder}
import kantan.codecs.export.Exported
import kantan.codecs.strings.{StringCodec, StringDecoder, StringEncoder}
import org.joda.time._
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}

trait JodaTimeInstances {
  // - DateTime codecs -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultDateTimeFormat: DateTimeFormatter = ISODateTimeFormat.dateTime()

  def dateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[DateTime] =
    StringDecoder.from(StringDecoder.decoder("DateTime")(format.parseDateTime))

  def dateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[DateTime] = StringEncoder.from(format.print)

  def dateTimeStringCodec(format: DateTimeFormatter): StringCodec[DateTime] =
    StringCodec.from(dateTimeStringDecoder(format), dateTimeStringEncoder(format))

  implicit val defaultDateTimeStringCodec: StringCodec[DateTime] =
    dateTimeStringCodec(defaultDateTimeFormat)


  // - LocalDateTime codecs --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalDateTimeFormat: DateTimeFormatter = new DateTimeFormatter(ISODateTimeFormat.dateTime().getPrinter,
    ISODateTimeFormat.localDateOptionalTimeParser().getParser)

   def localDateTimeStringDecoder(format: DateTimeFormatter): StringDecoder[LocalDateTime] =
   StringDecoder.from(StringDecoder.decoder("LocaleDateTime")(format.parseLocalDateTime))

   def localDateTimeStringEncoder(format: DateTimeFormatter): StringEncoder[LocalDateTime] =
     StringEncoder.from(format.print)

   def localDateTimeStringCodec(format: DateTimeFormatter): StringCodec[LocalDateTime] =
     StringCodec.from(localDateTimeStringDecoder(format), localDateTimeStringEncoder(format))

  implicit val defaultLocalDateTimeStringCodec: StringCodec[LocalDateTime] =
    localDateTimeStringCodec(defaultLocalDateTimeFormat)




  // - LocalDate codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalDateFormat: DateTimeFormatter = new DateTimeFormatter(ISODateTimeFormat.date().getPrinter,
    ISODateTimeFormat.localDateParser().getParser)

  def localDateStringDecoder(format: DateTimeFormatter): StringDecoder[LocalDate] =
    StringDecoder.from(StringDecoder.decoder("LocaleDate")(format.parseLocalDate))

  def localDateStringEncoder(format: DateTimeFormatter): StringEncoder[LocalDate] = StringEncoder.from(format.print)

  def localDateStringCodec(format: DateTimeFormatter): StringCodec[LocalDate] =
    StringCodec.from(localDateStringDecoder(format), localDateStringEncoder(format))

  implicit val defaultLocalDateStringCodec: StringCodec[LocalDate] =
    localDateStringCodec(defaultLocalDateFormat)



  // - LocalTime codecs ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val defaultLocalTimeFormat: DateTimeFormatter = new DateTimeFormatter(ISODateTimeFormat.time().getPrinter,
    ISODateTimeFormat.localTimeParser().getParser)

  def localTimeStringDecoder(format: DateTimeFormatter): StringDecoder[LocalTime] =
    StringDecoder.from(StringDecoder.decoder("LocaleTime")(format.parseLocalTime))

  def localTimeStringEncoder(format: DateTimeFormatter): StringEncoder[LocalTime] = StringEncoder.from(format.print)

  def localTimeStringCodec(format: DateTimeFormatter): StringCodec[LocalTime] =
    StringCodec.from(localTimeStringDecoder(format), localTimeStringEncoder(format))

  implicit val defaultLocalTimeStringCodec: StringCodec[LocalTime] =
    localTimeStringCodec(defaultLocalTimeFormat)
}

trait JodaTimeDecoderCompanion[E, F, T] {
  def decoderFrom[D](d: StringDecoder[D]): Decoder[E, D, F, T]

  def dateTimeDecoder(format: DateTimeFormatter): Decoder[E, DateTime, F, T] =
    decoderFrom(dateTimeStringDecoder(format))
  implicit val defaultDateTimeDecoder: Exported[Decoder[E, DateTime, F, T]] =
    Exported(dateTimeDecoder(defaultDateTimeFormat))

  def localDateTimeDecoder(format: DateTimeFormatter): Decoder[E, LocalDateTime, F, T] =
    decoderFrom(localDateTimeStringDecoder(format))
  implicit val defaultLocalDateTimeDecoder: Exported[Decoder[E, LocalDateTime, F, T]] =
    Exported(localDateTimeDecoder(defaultLocalDateTimeFormat))

  def localDateDecoder(format: DateTimeFormatter): Decoder[E, LocalDate, F, T] =
    decoderFrom(localDateStringDecoder(format))
  implicit val defaultLocalDateDecoder: Exported[Decoder[E, LocalDate, F, T]] =
    Exported(localDateDecoder(defaultLocalDateFormat))

  def localTimeDecoder(format: DateTimeFormatter): Decoder[E, LocalTime, F, T] =
    decoderFrom(localTimeStringDecoder(format))
  implicit val defaultLocalTimeDecoder: Exported[Decoder[E, LocalTime, F, T]] =
    Exported(localTimeDecoder(defaultLocalTimeFormat))
}

trait JodaTimeEncoderCompanion[E, T] {
  def encoderFrom[D](d: StringEncoder[D]): Encoder[E, D, T]

  def dateTimeEncoder(format: DateTimeFormatter): Encoder[E, DateTime, T] = encoderFrom(dateTimeStringEncoder(format))
  implicit val defaultDateTimeEncoder: Exported[Encoder[E, DateTime, T]] =
    Exported(dateTimeEncoder(defaultDateTimeFormat))

  def localDateTimeEncoder(format: DateTimeFormatter): Encoder[E, LocalDateTime, T] =
    encoderFrom(localDateTimeStringEncoder(format))
  implicit val defaultLocalDateTimeEncoder: Exported[Encoder[E, LocalDateTime, T]] =
    Exported(localDateTimeEncoder(defaultLocalDateTimeFormat))

  def localDateEncoder(format: DateTimeFormatter): Encoder[E, LocalDate, T] =
    encoderFrom(localDateStringEncoder(format))
  implicit val defaultLocalDateEncoder: Exported[Encoder[E, LocalDate, T]] =
      Exported(localDateEncoder(defaultLocalDateFormat))

  def localTimeEncoder(format: DateTimeFormatter): Encoder[E, LocalTime, T] =
    encoderFrom(localTimeStringEncoder(format))
  implicit val defaultLocalTimeEncoder: Exported[Encoder[E, LocalTime, T]] =
    Exported(localTimeEncoder(defaultLocalTimeFormat))
}

trait JodaTimeCodecCompanion[E, F, T] extends JodaTimeDecoderCompanion[E, F, T]
                                              with JodaTimeEncoderCompanion[E, T] {
  def dateTimeCodec[D](format: DateTimeFormatter): Codec[E, DateTime, F, T] =
    Codec.from(dateTimeDecoder(format), dateTimeEncoder(format))

  def localDateTimeCodec[D](format: DateTimeFormatter): Codec[E, LocalDateTime, F, T] =
    Codec.from(localDateTimeDecoder(format), localDateTimeEncoder(format))

  def localDateCodec[D](format: DateTimeFormatter): Codec[E, LocalDate, F, T] =
    Codec.from(localDateDecoder(format), localDateEncoder(format))

  def localTimeCodec[D](format: DateTimeFormatter): Codec[E, LocalTime, F, T] =
    Codec.from(localTimeDecoder(format), localTimeEncoder(format))
}
