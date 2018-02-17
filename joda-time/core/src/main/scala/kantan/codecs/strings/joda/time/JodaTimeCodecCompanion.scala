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
package joda.time

import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.joda.time.format.DateTimeFormatter

/** Provides useful methods for a joda-time codec companions.
  *
  * Usage note: when declaring default implicit instances, be sure to wrap them in an [[export.Exported]]. Otherwise,
  * custom instances and default ones are very likely to conflict.
  */
trait JodaTimeCodecCompanion[E, F, T] extends JodaTimeDecoderCompanion[E, F, T] with JodaTimeEncoderCompanion[E, T] {

  // - DateTime --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.dateTimeCodec("yyyy-MM-DD'T'HH:mm:ss.SSSzz")
    *
    * scala> val encoded = codec.encode(new DateTime(2000, 1, 1, 12, 0, 0, DateTimeZone.UTC))
    * res1: String = 2000-01-01T12:00:00.000UTC
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, DateTime] = Right(2000-01-01T12:00:00.000Z)
    * }}}
    */
  def dateTimeCodec(format: String): Codec[E, DateTime, F, T] = dateTimeCodec(Format(format))

  // TODO:  re-enable the type annotation on res2 when support for scala 2.11 is dropped
  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.dateTimeCodec(ISODateTimeFormat.dateTime())
    *
    * scala> val encoded = codec.encode(new DateTime(2000, 1, 1, 12, 0, 0, DateTimeZone.UTC))
    * res1: String = 2000-01-01T12:00:00.000UTC
    *
    * scala> codec.decode(encoded).right.map(_.withZone(DateTimeZone.UTC))
    * res2 = Right(2000-01-01T12:00:00.000Z)
    * }}}
    */
  def dateTimeCodec(format: ⇒ DateTimeFormatter): Codec[E, DateTime, F, T] = dateTimeCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.dateTimeCodec(Format("yyyy-MM-DD'T'HH:mm:ss.SSSzz"))
    *
    * scala> val encoded = codec.encode(new DateTime(2000, 1, 1, 12, 0, 0, DateTimeZone.UTC))
    * res1: String = 2000-01-01T12:00:00.000UTC
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, DateTime] = Right(2000-01-01T12:00:00.000Z)
    * }}}
    */
  def dateTimeCodec(format: Format): Codec[E, DateTime, F, T] =
    Codec.from(dateTimeDecoder(format), dateTimeEncoder(format))

  // - LocalDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.localDateTimeCodec("yyyy-MM-DD'T'HH:mm:ss.SSS")
    *
    * scala> val encoded = codec.encode(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, LocalDateTime] = Right(2000-01-01T12:00:00.000)
    * }}}
    */
  def localDateTimeCodec[D](format: String): Codec[E, LocalDateTime, F, T] =
    localDateTimeCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.localDateTimeCodec(new DateTimeFormatter(
    *      |   ISODateTimeFormat.dateTime().getPrinter,
    *      |   ISODateTimeFormat.localDateOptionalTimeParser().getParser
    *      | ))
    *
    * scala> val encoded = codec.encode(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, LocalDateTime] = Right(2000-01-01T12:00:00.000)
    * }}}
    */
  def localDateTimeCodec[D](format: ⇒ DateTimeFormatter): Codec[E, LocalDateTime, F, T] =
    localDateTimeCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.localDateTimeCodec(Format("yyyy-MM-DD'T'HH:mm:ss.SSS"))
    *
    * scala> val encoded = codec.encode(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, LocalDateTime] = Right(2000-01-01T12:00:00.000)
    * }}}
    */
  def localDateTimeCodec[D](format: Format): Codec[E, LocalDateTime, F, T] =
    Codec.from(localDateTimeDecoder(format), localDateTimeEncoder(format))

  // - LocalDate -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.localDateCodec("yyyy-MM-DD")
    *
    * scala> val encoded = codec.encode(new LocalDate(2000, 1, 1))
    * res1: String = 2000-01-01
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, LocalDate] = Right(2000-01-01)
    * }}}
    */
  def localDateCodec[D](format: String): Codec[E, LocalDate, F, T] = localDateCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.localDateCodec(new DateTimeFormatter(
    *      |   ISODateTimeFormat.date().getPrinter,
    *      |   ISODateTimeFormat.localDateParser().getParser
    *      | ))
    *
    * scala> val encoded = codec.encode(new LocalDate(2000, 1, 1))
    * res1: String = 2000-01-01
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, LocalDate] = Right(2000-01-01)
    * }}}
    */
  def localDateCodec[D](format: ⇒ DateTimeFormatter): Codec[E, LocalDate, F, T] = localDateCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.localDateCodec(Format("yyyy-MM-DD"))
    *
    * scala> val encoded = codec.encode(new LocalDate(2000, 1, 1))
    * res1: String = 2000-01-01
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, LocalDate] = Right(2000-01-01)
    * }}}
    */
  def localDateCodec[D](format: Format): Codec[E, LocalDate, F, T] =
    Codec.from(localDateDecoder(format), localDateEncoder(format))

  // - LocalTime -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.localTimeCodec("HH:mm:ss.SSS")
    *
    * scala> val encoded = codec.encode(new LocalTime(12, 0, 0))
    * res1: String = 12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, LocalTime] = Right(12:00:00.000)
    * }}}
    */
  def localTimeCodec[D](format: String): Codec[E, LocalTime, F, T] = localTimeCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.localTimeCodec(new DateTimeFormatter(
    *      |   ISODateTimeFormat.time().getPrinter,
    *      |   ISODateTimeFormat.localTimeParser().getParser
    *      | ))
    *
    * scala> val encoded = codec.encode(new LocalTime(12, 0, 0))
    * res1: String = 12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, LocalTime] = Right(12:00:00.000)
    * }}}
    */
  def localTimeCodec[D](format: ⇒ DateTimeFormatter): Codec[E, LocalTime, F, T] = localTimeCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> val codec = Foo.localTimeCodec(Format("HH:mm:ss.SSS"))
    *
    * scala> val encoded = codec.encode(new LocalTime(12, 0, 0))
    * res1: String = 12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: Either[DecodeError, LocalTime] = Right(12:00:00.000)
    * }}}
    */
  def localTimeCodec[D](format: Format): Codec[E, LocalTime, F, T] =
    Codec.from(localTimeDecoder(format), localTimeEncoder(format))

}
