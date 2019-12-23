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
import kantan.codecs.Encoder
import kantan.codecs.strings.StringEncoder

/** Provides useful methods for a java8 time encoder companions.
  *
  * Usage note: when declaring default implicit instances, be sure to wrap them in an [[export.Exported]]. Otherwise,
  * custom instances and default ones are very likely to conflict.
  */
trait TimeEncoderCompanion[Encoded, Tag] {

  def encoderFrom[D](d: StringEncoder[D]): Encoder[Encoded, D, Tag]

  // - LocalTime -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localTimeEncoder(DateTimeFormatter.ISO_LOCAL_TIME)
    *      |   .encode(LocalTime.of(12, 0, 0, 0))
    * res1: String = 12:00:00
    * }}}
    */
  def localTimeEncoder(format: => DateTimeFormatter): Encoder[Encoded, LocalTime, Tag] =
    localTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localTimeEncoder(fmt"HH:mm:ss.SSS")
    *      |   .encode(LocalTime.of(12, 0, 0, 0))
    * res1: String = 12:00:00.000
    * }}}
    */
  def localTimeEncoder(format: Format): Encoder[Encoded, LocalTime, Tag] =
    encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultLocalTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultLocalTimeEncoder
    *      |   .encode(LocalTime.of(12, 0, 0, 0))
    * res1: String = 12:00:00
    * }}}
    */
  def defaultLocalTimeEncoder: Encoder[Encoded, LocalTime, Tag] = localTimeEncoder(Format.defaultLocalTimeFormat)

  // - LocalDate -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateEncoder(DateTimeFormatter.ISO_LOCAL_DATE)
    *      |   .encode(LocalDate.of(2000, 1, 1))
    * res1: String = 2000-01-01
    * }}}
    */
  def localDateEncoder(format: => DateTimeFormatter): Encoder[Encoded, LocalDate, Tag] =
    localDateEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateEncoder(fmt"yyyy-MM-DD")
    *      |   .encode(LocalDate.of(2000, 1, 1))
    * res1: String = 2000-01-01
    * }}}
    */
  def localDateEncoder(format: Format): Encoder[Encoded, LocalDate, Tag] =
    encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultLocalDateFormat default format]].
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultLocalDateEncoder
    *      |   .encode(LocalDate.of(2000, 1, 1))
    * res1: String = 2000-01-01
    * }}}
    */
  def defaultLocalDateEncoder: Encoder[Encoded, LocalDate, Tag] = localDateEncoder(Format.defaultLocalDateFormat)

  // - LocalDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateTimeEncoder(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    *      |   .encode(LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0))
    * res1: String = 2000-01-01T12:00:00
    * }}}
    */
  def localDateTimeEncoder(format: => DateTimeFormatter): Encoder[Encoded, LocalDateTime, Tag] =
    localDateTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateTimeEncoder(fmt"yyyy-MM-DD'T'HH:mm:ss.SSS")
    *      |   .encode(LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    * }}}
    */
  def localDateTimeEncoder(format: Format): Encoder[Encoded, LocalDateTime, Tag] =
    encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultLocalDateTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultLocalDateTimeEncoder
    *      |   .encode(LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0))
    * res1: String = 2000-01-01T12:00:00
    * }}}
    */
  def defaultLocalDateTimeEncoder: Encoder[Encoded, LocalDateTime, Tag] =
    localDateTimeEncoder(Format.defaultLocalDateTimeFormat)

  // - OffsetDateTime --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.offsetDateTimeEncoder(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    *      |   .encode(OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00Z
    * }}}
    */
  def offsetDateTimeEncoder(format: => DateTimeFormatter): Encoder[Encoded, OffsetDateTime, Tag] =
    offsetDateTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.offsetDateTimeEncoder(fmt"yyyy-MM-DD'T'HH:mm:ss.SSSXX")
    *      |   .encode(OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    * }}}
    */
  def offsetDateTimeEncoder(format: Format): Encoder[Encoded, OffsetDateTime, Tag] =
    encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultOffsetDateTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultOffsetDateTimeEncoder
    *      |   .encode(OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00Z
    * }}}
    */
  def defaultOffsetDateTimeEncoder: Encoder[Encoded, OffsetDateTime, Tag] =
    offsetDateTimeEncoder(Format.defaultOffsetDateTimeFormat)

  // - ZonedDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.zonedDateTimeEncoder(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    *      |   .encode(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00Z
    * }}}
    */
  def zonedDateTimeEncoder(format: => DateTimeFormatter): Encoder[Encoded, ZonedDateTime, Tag] =
    zonedDateTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.zonedDateTimeEncoder(fmt"yyyy-MM-DD'T'HH:mm:ss.SSSzz")
    *      |   .encode(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    * }}}
    */
  def zonedDateTimeEncoder(format: Format): Encoder[Encoded, ZonedDateTime, Tag] =
    encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultZonedDateTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultZonedDateTimeEncoder
    *      |   .encode(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00Z
    * }}}
    */
  def defaultZonedDateTimeEncoder: Encoder[Encoded, ZonedDateTime, Tag] =
    zonedDateTimeEncoder(Format.defaultZonedDateTimeFormat)

  // - Instant ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.instantEncoder(DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC))
    *      |   .encode(Instant.parse("2000-01-01T12:00:00.000Z"))
    * res1: String = 2000-01-01T12:00:00Z
    * }}}
    */
  def instantEncoder(format: => DateTimeFormatter): Encoder[Encoded, Instant, Tag] = instantEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import java.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.instantEncoder(Format(DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC)))
    *      |   .encode(Instant.parse("2000-01-01T12:00:00.000Z"))
    * res1: String = 2000-01-01T12:00:00Z
    * }}}
    */
  def instantEncoder(format: Format): Encoder[Encoded, Instant, Tag] =
    encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultInstantFormat default format]].
    *
    * @example
    * {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends TimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultInstantEncoder
    *      |   .encode(Instant.parse("2000-01-01T12:00:00.000Z"))
    * res1: String = 2000-01-01T12:00:00Z
    * }}}
    */
  def defaultInstantEncoder: Encoder[Encoded, Instant, Tag] = instantEncoder(Format.defaultInstantFormat)

}
