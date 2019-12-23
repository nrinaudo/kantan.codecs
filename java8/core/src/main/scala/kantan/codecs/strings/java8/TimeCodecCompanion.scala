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

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, OffsetDateTime, ZonedDateTime}
import java.time.format.DateTimeFormatter
import kantan.codecs.Codec

/** Provides useful methods for a java8 time codec companions.
  *
  * Usage note: when declaring default implicit instances, be sure to wrap them in an [[export.Exported]]. Otherwise,
  * custom instances and default ones are very likely to conflict.
  */
trait TimeCodecCompanion[Encoded, Failure, Tag]
    extends TimeDecoderCompanion[Encoded, Failure, Tag] with TimeEncoderCompanion[Encoded, Tag] {

  // - LocalTime -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.localTimeCodec(DateTimeFormatter.ISO_LOCAL_TIME)
    *
    * scala> val encoded = codec.encode(LocalTime.of(12, 0, 0, 0))
    * res1: String = 12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[LocalTime] = Right(12:00)
    * }}}
    */
  def localTimeCodec(format: => DateTimeFormatter): Codec[Encoded, LocalTime, Failure, Tag] =
    localTimeCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.localTimeCodec(fmt"HH:mm:ss.SSS")
    *
    * scala> val encoded = codec.encode(LocalTime.of(12, 0, 0, 0))
    * res1: String = 12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[LocalTime] = Right(12:00)
    * }}}
    */
  def localTimeCodec(format: Format): Codec[Encoded, LocalTime, Failure, Tag] =
    Codec.from(localTimeDecoder(format), localTimeEncoder(format))

  // - LocalDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.localDateTimeCodec(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    *
    * scala> val encoded = codec.encode(LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[LocalDateTime] = Right(2000-01-01T12:00)
    * }}}
    */
  def localDateTimeCodec(format: => DateTimeFormatter): Codec[Encoded, LocalDateTime, Failure, Tag] =
    localDateTimeCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.localDateTimeCodec(fmt"yyyy-MM-DD'T'HH:mm:ss.SSS")
    *
    * scala> val encoded = codec.encode(LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[LocalDateTime] = Right(2000-01-01T12:00)
    * }}}
    */
  def localDateTimeCodec(format: Format): Codec[Encoded, LocalDateTime, Failure, Tag] =
    Codec.from(localDateTimeDecoder(format), localDateTimeEncoder(format))

  // - LocalDate -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.localDateCodec(DateTimeFormatter.ISO_LOCAL_DATE)
    *
    * scala> val encoded = codec.encode(LocalDate.of(2000, 1, 1))
    * res1: String = 2000-01-01
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[LocalDate] = Right(2000-01-01)
    * }}}
    */
  def localDateCodec(format: => DateTimeFormatter): Codec[Encoded, LocalDate, Failure, Tag] =
    localDateCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.localDateCodec(fmt"yyyy-MM-DD")
    *
    * scala> val encoded = codec.encode(LocalDate.of(2000, 1, 1))
    * res1: String = 2000-01-01
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[LocalDate] = Right(2000-01-01)
    * }}}
    */
  def localDateCodec(format: Format): Codec[Encoded, LocalDate, Failure, Tag] =
    Codec.from(localDateDecoder(format), localDateEncoder(format))

  // - Instant ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.instantCodec(DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC))
    *
    * scala> val encoded = codec.encode(Instant.parse("2000-01-01T12:00:00.000Z"))
    * res1: String = 2000-01-01T12:00:00Z
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[Instant] = Right(2000-01-01T12:00:00Z)
    * }}}
    */
  def instantCodec(format: => DateTimeFormatter): Codec[Encoded, Instant, Failure, Tag] =
    instantCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.instantCodec(Format(DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC)))
    *
    * scala> val encoded = codec.encode(Instant.parse("2000-01-01T12:00:00.000Z"))
    * res1: String = 2000-01-01T12:00:00Z
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[Instant] = Right(2000-01-01T12:00:00Z)
    * }}}
    */
  def instantCodec(format: Format): Codec[Encoded, Instant, Failure, Tag] =
    Codec.from(instantDecoder(format), instantEncoder(format))

  // - ZonedDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.zonedDateTimeCodec(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    *
    * scala> val encoded = codec.encode(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[ZonedDateTime] = Right(2000-01-01T12:00Z)
    * }}}
    */
  def zonedDateTimeCodec(format: => DateTimeFormatter): Codec[Encoded, ZonedDateTime, Failure, Tag] =
    zonedDateTimeCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.zonedDateTimeCodec(fmt"yyyy-MM-DD'T'HH:mm:ss.SSSzz")
    *
    * scala> val encoded = codec.encode(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[ZonedDateTime] = Right(2000-01-01T12:00Z)
    * }}}
    */
  def zonedDateTimeCodec(format: Format): Codec[Encoded, ZonedDateTime, Failure, Tag] =
    Codec.from(zonedDateTimeDecoder(format), zonedDateTimeEncoder(format))

  // - OffsetDateTime --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.offsetDateTimeCodec(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    *
    * scala> val encoded = codec.encode(OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[OffsetDateTime] = Right(2000-01-01T12:00Z)
    * }}}
    */
  def offsetDateTimeCodec(format: => DateTimeFormatter): Codec[Encoded, OffsetDateTime, Failure, Tag] =
    offsetDateTimeCodec(Format(format))

  /** Creates a [[Codec]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeCodecCompanion[String, DecodeError, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> val codec = Foo.offsetDateTimeCodec(fmt"yyyy-MM-DD'T'HH:mm:ss.SSSXX")
    *
    * scala> val encoded = codec.encode(OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    *
    * scala> codec.decode(encoded)
    * res2: StringResult[OffsetDateTime] = Right(2000-01-01T12:00Z)
    * }}}
    */
  def offsetDateTimeCodec(format: Format): Codec[Encoded, OffsetDateTime, Failure, Tag] =
    Codec.from(offsetDateTimeDecoder(format), offsetDateTimeEncoder(format))

}
