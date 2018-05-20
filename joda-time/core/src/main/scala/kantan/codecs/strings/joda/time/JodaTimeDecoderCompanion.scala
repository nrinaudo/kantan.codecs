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

/** Provides useful methods for a joda-time decoder companions.
  *
  * Usage note: when declaring default implicit instances, be sure to wrap them in an [[export.Exported]]. Otherwise,
  * custom instances and default ones are very likely to conflict.
  */
trait JodaTimeDecoderCompanion[E, F, T] {

  def decoderFrom[D](d: StringDecoder[D]): Decoder[E, D, F, T]

  // - DateTime --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Decoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.dateTimeDecoder(ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC))
    *      | .decode("2000-01-01T12:00:00.000Z")
    * res1: StringResult[DateTime] = Right(2000-01-01T12:00:00.000Z)
    * }}}
    */
  def dateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, DateTime, F, T] = dateTimeDecoder(Format(format))

  /** Creates a [[Decoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.dateTimeDecoder(fmt"yyyy-MM-DD'T'HH:mm:ss.SSSzz")
    *      | .decode("2000-01-01T12:00:00.000UTC")
    * res1: StringResult[DateTime] = Right(2000-01-01T12:00:00.000Z)
    * }}}
    */
  def dateTimeDecoder(format: Format): Decoder[E, DateTime, F, T] =
    decoderFrom(StringDecoder.from(format.parseDateTime))

  // TODO:  re-enable the type annotation on res1 when support for scala 2.11 is dropped
  /** Creates a [[Decoder]] instance using the [[Format.defaultDateTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.defaultDateTimeDecoder
    *      | .decode("2000-01-01T12:00:00.000Z")
    *      | .right.map(_.withZone(DateTimeZone.UTC))
    * res1 = Right(2000-01-01T12:00:00.000Z)
    * }}}
    */
  def defaultDateTimeDecoder: Decoder[E, DateTime, F, T] = dateTimeDecoder(Format.defaultDateTimeFormat)

  // - LocalDateTime ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Decoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.localDateTimeDecoder(ISODateTimeFormat.localDateOptionalTimeParser())
    *      | .decode("2000-01-01T12:00:00.000")
    * res1: StringResult[LocalDateTime] = Right(2000-01-01T12:00:00.000)
    * }}}
    */
  def localDateTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalDateTime, F, T] =
    localDateTimeDecoder(Format(format))

  /** Creates a [[Decoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.localDateTimeDecoder(fmt"yyyy-MM-DD'T'HH:mm:ss.SSS")
    *      | .decode("2000-01-01T12:00:00.000")
    * res1: StringResult[LocalDateTime] = Right(2000-01-01T12:00:00.000)
    * }}}
    */
  def localDateTimeDecoder(format: Format): Decoder[E, LocalDateTime, F, T] =
    decoderFrom(StringDecoder.from(format.parseLocalDateTime))

  /** Creates a [[Decoder]] instance using the [[Format.defaultLocalDateTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.defaultLocalDateTimeDecoder
    *      | .decode("2000-01-01T12:00:00.000")
    * res1: StringResult[LocalDateTime] = Right(2000-01-01T12:00:00.000)
    * }}}
    */
  def defaultLocalDateTimeDecoder: Decoder[E, LocalDateTime, F, T] =
    localDateTimeDecoder(Format.defaultLocalDateTimeFormat)

  // - LocalDate -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Decoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.localDateDecoder(ISODateTimeFormat.localDateParser())
    *      | .decode("2000-01-01")
    * res1: StringResult[LocalDate] = Right(2000-01-01)
    * }}}
    */
  def localDateDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalDate, F, T] = localDateDecoder(Format(format))

  /** Creates a [[Decoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.localDateDecoder(fmt"yyyy-MM-DD")
    *      | .decode("2000-01-01")
    * res1: StringResult[LocalDate] = Right(2000-01-01)
    * }}}
    */
  def localDateDecoder(format: Format): Decoder[E, LocalDate, F, T] =
    decoderFrom(StringDecoder.from(format.parseLocalDate))

  /** Creates a [[Decoder]] instance using the [[Format.defaultLocalDateFormat default format]].
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.defaultLocalDateDecoder
    *      | .decode("2000-01-01")
    * res1: StringResult[LocalDate] = Right(2000-01-01)
    * }}}
    */
  def defaultLocalDateDecoder: Decoder[E, LocalDate, F, T] = localDateDecoder(Format.defaultLocalDateFormat)

  // - LocalTime -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Creates a [[Decoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._, format._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.localTimeDecoder(ISODateTimeFormat.localTimeParser())
    *      | .decode("12:00:00.000")
    * res1: StringResult[LocalTime] = Right(12:00:00.000)
    * }}}
    */
  def localTimeDecoder(format: ⇒ DateTimeFormatter): Decoder[E, LocalTime, F, T] = localTimeDecoder(Format(format))

  /** Creates a [[Decoder]] instance that uses the specified format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.localTimeDecoder(fmt"HH:mm:ss.SSS")
    *      | .decode("12:00:00.000")
    * res1: StringResult[LocalTime] = Right(12:00:00.000)
    * }}}
    */
  def localTimeDecoder(format: Format): Decoder[E, LocalTime, F, T] =
    decoderFrom(StringDecoder.from(format.parseLocalTime))

  /** Creates a [[Decoder]] instance using the [[Format.defaultLocalTimeFormat default format]].
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> object Foo extends JodaTimeDecoderCompanion[String, DecodeError, codecs.type] {
    *      |   override def decoderFrom[D](d: StringDecoder[D]) = d
    *      | }
    *
    * scala> Foo.defaultLocalTimeDecoder
    *      | .decode("12:00:00.000")
    * res1: StringResult[LocalTime] = Right(12:00:00.000)
    * }}}
    */
  def defaultLocalTimeDecoder: Decoder[E, LocalTime, F, T] = localTimeDecoder(Format.defaultLocalTimeFormat)

}
