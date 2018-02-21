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

/** Provides useful methods for a joda-time encoder companions.
  *
  * Usage note: when declaring default implicit instances, be sure to wrap them in an [[export.Exported]]. Otherwise,
  * custom instances and default ones are very likely to conflict.
  */
trait JodaTimeEncoderCompanion[E, T] {

  /** Transforms a `String`-based encoder into one of the proper type. */
  def encoderFrom[D](e: StringEncoder[D]): Encoder[E, D, T]

  // - DateTime --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.dateTimeEncoder("yyyy-MM-DD'T'HH:mm:ss.SSSzz")
    *      |   .encode(new DateTime(2000, 1, 1, 12, 0, 0, DateTimeZone.UTC))
    * res1: String = 2000-01-01T12:00:00.000UTC
    * }}}
    */
  def dateTimeEncoder(format: String): Encoder[E, DateTime, T] = dateTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.dateTimeEncoder(ISODateTimeFormat.dateTime())
    *      |   .encode(new DateTime(2000, 1, 1, 12, 0, 0, DateTimeZone.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    * }}}
    */
  def dateTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, DateTime, T] = dateTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.dateTimeEncoder(Format(ISODateTimeFormat.dateTime()))
    *      |   .encode(new DateTime(2000, 1, 1, 12, 0, 0, DateTimeZone.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    * }}}
    */
  def dateTimeEncoder(format: Format): Encoder[E, DateTime, T] = encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultDateTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultDateTimeEncoder
    *      |   .encode(new DateTime(2000, 1, 1, 12, 0, 0, DateTimeZone.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    * }}}
    */
  def defaultDateTimeEncoder: Encoder[E, DateTime, T] = dateTimeEncoder(Format.defaultDateTimeFormat)

  // - LocalDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateTimeEncoder("yyyy-MM-DD'T'HH:mm:ss.SSS")
    *      |   .encode(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    * }}}
    */
  def localDateTimeEncoder(format: String): Encoder[E, LocalDateTime, T] = localDateTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateTimeEncoder(ISODateTimeFormat.dateTime())
    *      |   .encode(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    * }}}
    */
  def localDateTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, LocalDateTime, T] =
    localDateTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateTimeEncoder(Format("yyyy-MM-DD'T'HH:mm:ss.SSS"))
    *      |   .encode(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    * }}}
    */
  def localDateTimeEncoder(format: Format): Encoder[E, LocalDateTime, T] =
    encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultLocalDateTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultLocalDateTimeEncoder
    *      |   .encode(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    * }}}
    */
  def defaultLocalDateTimeEncoder: Encoder[E, LocalDateTime, T] =
    localDateTimeEncoder(Format.defaultLocalDateTimeFormat)

  // - LocalDate -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateEncoder("yyyy-MM-DD")
    *      |   .encode(new LocalDate(2000, 1, 1))
    * res1: String = 2000-01-01
    * }}}
    */
  def localDateEncoder(format: String): Encoder[E, LocalDate, T] = localDateEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateEncoder(ISODateTimeFormat.date())
    *      |   .encode(new LocalDate(2000, 1, 1))
    * res1: String = 2000-01-01
    * }}}
    */
  def localDateEncoder(format: ⇒ DateTimeFormatter): Encoder[E, LocalDate, T] = localDateEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localDateEncoder(Format("yyyy-MM-DD"))
    *      |   .encode(new LocalDate(2000, 1, 1))
    * res1: String = 2000-01-01
    * }}}
    */
  def localDateEncoder(format: Format): Encoder[E, LocalDate, T] = encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultLocalDateFormat default format]].
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultLocalDateEncoder
    *      |   .encode(new LocalDate(2000, 1, 1))
    * res1: String = 2000-01-01
    * }}}
    */
  def defaultLocalDateEncoder: Encoder[E, LocalDate, T] = localDateEncoder(Format.defaultLocalDateFormat)

  // - LocalTime -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localTimeEncoder("HH:mm:ss.SSS")
    *      |   .encode(new LocalTime(12, 0, 0, 0))
    * res1: String = 12:00:00.000
    * }}}
    */
  def localTimeEncoder(format: String): Encoder[E, LocalTime, T] = localTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localTimeEncoder(ISODateTimeFormat.time())
    *      |   .encode(new LocalTime(12, 0, 0, 0))
    * res1: String = 12:00:00.000
    * }}}
    */
  def localTimeEncoder(format: ⇒ DateTimeFormatter): Encoder[E, LocalTime, T] = localTimeEncoder(Format(format))

  /** Creates an [[Encoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.localTimeEncoder(Format("HH:mm:ss.SSS"))
    *      |   .encode(new LocalTime(12, 0, 0, 0))
    * res1: String = 12:00:00.000
    * }}}
    */
  def localTimeEncoder(format: Format): Encoder[E, LocalTime, T] = encoderFrom(StringEncoder.from(format.format))

  /** Creates an [[Encoder]] instance using the [[Format.defaultLocalTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeEncoderCompanion[String, codecs.type] {
    *      |   override def encoderFrom[D](e: StringEncoder[D]) = e
    *      | }
    *
    * scala> Foo.defaultLocalTimeEncoder
    *      |   .encode(new LocalTime(12, 0, 0, 0))
    * res1: String = 12:00:00.000
    * }}}
    */
  def defaultLocalTimeEncoder: Encoder[E, LocalTime, T] = localTimeEncoder(Format.defaultLocalTimeFormat)

}
