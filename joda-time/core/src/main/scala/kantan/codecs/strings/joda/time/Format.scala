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

import org.joda.time._
import org.joda.time.format._

/** `Serializable` wrapper around `DateTimeFormatter`.
  *
  * The main purpose of this is to make joda-time codecs `Serializable` and thus usable with frameworks like spark.
  */
sealed trait Format {

  /** Internal joda formatter.
    *
    * This is protected to prevent anyone from calling it directly - doing so instantly turns the calling class
    * non-serializable, which rather defeats the purpose of [[Format]].
    */
  protected def formatter: DateTimeFormatter

  // TODO:  re-enable the type annotation on res1 when support for scala 2.11 is dropped
  /** Attempts to parse the specified string as a `DateTime`.
    *
    * @example
    * {{{
    * scala> import kantan.codecs.strings._
    * scala> import org.joda.time._
    *
    * scala> Format.defaultDateTimeFormat
    *      |   .parseDateTime("2000-01-01T12:00:00.000Z")
    *      |   .right.map(_.withZone(DateTimeZone.UTC))
    * res1 = Right(2000-01-01T12:00:00.000Z)
    * }}}
    */
  def parseDateTime(str: String): StringResult[DateTime] =
    StringDecoder.makeSafe("DateTime")(formatter.parseDateTime)(str)

  /** Attempts to parse the specified string as a `LocalDateTime`.
    *
    * @example
    * {{{
    * scala> import kantan.codecs.strings._
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .parseLocalDateTime("2000-01-01T12:00:00.000")
    * res1: StringResult[LocalDateTime] = Right(2000-01-01T12:00:00.000)
    * }}}
    */
  def parseLocalDateTime(str: String): StringResult[LocalDateTime] =
    StringDecoder.makeSafe("LocalDateTime")(formatter.parseLocalDateTime)(str)

  /** Attempts to parse the specified string as a `LocalTime`.
    *
    * @example
    * {{{
    * scala> import kantan.codecs.strings._
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .parseLocalTime("12:00:00.000")
    * res1: StringResult[LocalTime] = Right(12:00:00.000)
    * }}}
    */
  def parseLocalTime(str: String): StringResult[LocalTime] =
    StringDecoder.makeSafe("LocalTime")(formatter.parseLocalTime)(str)

  /** Attempts to parse the specified string as a `LocalDate`.
    *
    * @example
    * {{{
    * scala> import kantan.codecs.strings._
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .parseLocalDate("2000-01-01")
    * res2: StringResult[LocalDate] = Right(2000-01-01)
    * }}}
    */
  def parseLocalDate(str: String): StringResult[LocalDate] =
    StringDecoder.makeSafe("LocalDate")(formatter.parseLocalDate)(str)

  /** Formats the specified `ReadableInstant` as a string.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .format(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    * }}}
    */
  def format(instant: ReadableInstant): String = formatter.print(instant)

  /** Formats the specified `ReadablePartial` as a string.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .format(new LocalTime(12, 0, 0))
    * res1: String = 12:00:00.000
    * }}}
    */
  def format(instant: ReadablePartial): String = formatter.print(instant)

}

/** Provides instance creation methods and default values for [[Format]]. */
object Format {

  def apply(fmt: ⇒ DateTimeFormatter): Format = new Format with Serializable {
    @transient override lazy val formatter = fmt
  }

  /** Attempts to create a new [[Format]] from the given pattern.
    *
    * Syntax for pattern strings can be found
    * [[http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html here]].
    */
  def from(pattern: String): Either[String, Format] =
    try {

      val format = Format(DateTimeFormat.forPattern(pattern))

      // Forces evaluation of the format to catch invalid patterns.
      format.formatter
      Right(format)

    } catch {
      case _: Exception ⇒ Left(s"Invalid pattern: '$pattern'")
    }

  // TODO:  re-enable the type annotation on res1 when support for scala 2.11 is dropped
  /** Default `DateTime` format.
    *
    * @example
    * {{{
    * scala> import kantan.codecs.strings._
    * scala> import org.joda.time._
    *
    * scala> Format.defaultDateTimeFormat
    *      |   .format(new DateTime(2000, 1, 1, 12, 0, 0, DateTimeZone.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    *
    * scala> Format.defaultDateTimeFormat
    *      |   .parseDateTime("2000-01-01T12:00:00.000Z")
    *      |   .right.map(_.withZone(DateTimeZone.UTC))
    * res2 = Right(2000-01-01T12:00:00.000Z)
    * }}}
    */
  val defaultDateTimeFormat: Format = Format(ISODateTimeFormat.dateTime())

  /** Default `LocalDateTime` format.
    *
    * @example
    * {{{
    * scala> import kantan.codecs.strings._
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .format(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .parseLocalDateTime("2000-01-01T12:00:00.000")
    * res2: StringResult[LocalDateTime] = Right(2000-01-01T12:00:00.000)
    * }}}
    */
  val defaultLocalDateTimeFormat: Format = Format(
    new DateTimeFormatter(
      ISODateTimeFormat.dateTime().getPrinter,
      ISODateTimeFormat.localDateOptionalTimeParser().getParser
    )
  )

  /** Default `LocalDate` format.
    *
    * @example
    * {{{
    * scala> import kantan.codecs.strings._
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .format(new LocalDate(2000, 1, 1))
    * res1: String = 2000-01-01
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .parseLocalDate("2000-01-01")
    * res2: StringResult[LocalDate] = Right(2000-01-01)
    * }}}
    */
  val defaultLocalDateFormat: Format = Format(
    new DateTimeFormatter(ISODateTimeFormat.date().getPrinter, ISODateTimeFormat.localDateParser().getParser)
  )

  /** Default `LocalTime` format.
    *
    * @example
    * {{{
    * scala> import kantan.codecs.strings._
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .format(new LocalTime(12, 0, 0))
    * res1: String = 12:00:00.000
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .parseLocalTime("12:00:00.000")
    * res2: StringResult[LocalTime] = Right(12:00:00.000)
    * }}}
    */
  val defaultLocalTimeFormat: Format = Format(
    new DateTimeFormatter(ISODateTimeFormat.time().getPrinter, ISODateTimeFormat.localTimeParser().getParser)
  )

}
