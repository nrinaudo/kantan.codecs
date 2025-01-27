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

import kantan.codecs.strings.StringDecoder
import kantan.codecs.strings.StringResult

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

/** `Serializable` wrapper around `DateTimeFormatter`.
  *
  * The main purpose of this is to make java8 codecs `Serializable` and thus usable with frameworks like spark.
  */
sealed trait Format {

  /** Internal date/time formatter.
    *
    * This is protected to prevent anyone from calling it directly - doing so instantly turns the calling class
    * non-serializable, which rather defeats the purpose of [[Format]].
    */
  protected def formatter: DateTimeFormatter

  /** Attempts to parse the specified string as an `Instant`.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultInstantFormat
    *      |   .parseInstant("2000-01-01T12:00:00.000Z")
    * res1: StringResult[Instant] = Right(2000-01-01T12:00:00Z)
    *   }}}
    */
  def parseInstant(str: String): StringResult[Instant] =
    StringDecoder.makeSafe("Instant")(s => Instant.from(formatter.parse(s)))(str)

  /** Attempts to parse the specified string as a `LocalDateTime`.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .parseLocalDateTime("2000-01-01T12:00:00.000")
    * res2: StringResult[LocalDateTime] = Right(2000-01-01T12:00)
    *   }}}
    */
  def parseLocalDateTime(str: String): StringResult[LocalDateTime] =
    StringDecoder.makeSafe("LocalDateTime")(s => LocalDateTime.parse(s, formatter))(str)

  /** Attempts to parse the specified string as a `ZonedDateTime`.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultZonedDateTimeFormat
    *      |   .parseZonedDateTime("2000-01-01T12:00:00.000Z")
    * res1: StringResult[ZonedDateTime] = Right(2000-01-01T12:00Z)
    *   }}}
    */
  def parseZonedDateTime(str: String): StringResult[ZonedDateTime] =
    StringDecoder.makeSafe("ZonedDateTime")(s => ZonedDateTime.parse(s, formatter))(str)

  /** Attempts to parse the specified string as a `LocalDateTime`.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .parseLocalTime("12:00:00.000")
    * res1: StringResult[LocalTime] = Right(12:00)
    *   }}}
    */
  def parseLocalTime(str: String): StringResult[LocalTime] =
    StringDecoder.makeSafe("LocalTime")(s => LocalTime.parse(s, formatter))(str)

  /** Attempts to parse the specified string as a `LocalDate`.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .parseLocalDate("2000-01-01")
    * res2: StringResult[LocalDate] = Right(2000-01-01)
    *   }}}
    */
  def parseLocalDate(str: String): StringResult[LocalDate] =
    StringDecoder.makeSafe("LocalDate")(s => LocalDate.parse(s, formatter))(str)

  /** Attempts to parse the specified string as a `LocalDate`.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultOffsetDateTimeFormat
    *      |   .parseOffsetDateTime("2000-01-01T12:00:00.000Z")
    * res1: StringResult[OffsetDateTime] = Right(2000-01-01T12:00Z)
    *   }}}
    */
  def parseOffsetDateTime(str: String): StringResult[OffsetDateTime] =
    StringDecoder.makeSafe("OffsetDateTime")(s => OffsetDateTime.parse(s, formatter))(str)

  /** Formats the specified `TemporalAccessor` as a string.
    *
    * @example
    *   {{{
    * scala> import java.time._
    *
    * scala> Format.defaultZonedDateTimeFormat
    *      |   .format(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00Z
    *   }}}
    */
  def format(temporal: TemporalAccessor): String =
    formatter.format(temporal)

}

object Format {

  def apply(fmt: => DateTimeFormatter): Format =
    new Format with Serializable {
      @transient override lazy val formatter = fmt
    }

  /** Attempts to create a new [[Format]] from the given pattern.
    *
    * Syntax for pattern strings can be found
    * [[https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html here]].
    */
  def from(pattern: String): Either[String, Format] =
    try {

      val format = Format(DateTimeFormatter.ofPattern(pattern))

      // Forces evaluation of the format to catch invalid patterns.
      format.formatter
      Right(format)

    } catch {
      case _: Exception => Left(s"Invalid pattern: '$pattern'")
    }

  /** Default `Instant` format.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultInstantFormat
    *      |   .format(Instant.parse("2000-01-01T12:00:00.000Z"))
    * res1: String = 2000-01-01T12:00:00Z
    *
    * scala> Format.defaultInstantFormat
    *      |   .parseInstant("2000-01-01T12:00:00.000Z")
    * res2: StringResult[Instant] = Right(2000-01-01T12:00:00Z)
    *   }}}
    */
  val defaultInstantFormat: Format = Format(DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC))

  /** Default `LocalDateTime` format.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .format(LocalDateTime.of(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .parseLocalDateTime("2000-01-01T12:00:00.000")
    * res2: StringResult[LocalDateTime] = Right(2000-01-01T12:00)
    *   }}}
    */
  val defaultLocalDateTimeFormat: Format = Format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

  /** Default `ZonedDateTime` format.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultZonedDateTimeFormat
    *      |   .format(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00Z
    *
    * scala> Format.defaultZonedDateTimeFormat
    *      |   .parseZonedDateTime("2000-01-01T12:00:00.000Z")
    * res2: StringResult[ZonedDateTime] = Right(2000-01-01T12:00Z)
    *   }}}
    */
  val defaultZonedDateTimeFormat: Format = Format(DateTimeFormatter.ISO_ZONED_DATE_TIME)

  /** Default `OffsetDateTime` format.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultOffsetDateTimeFormat
    *      |   .format(OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00Z
    *
    * scala> Format.defaultOffsetDateTimeFormat
    *      |   .parseOffsetDateTime("2000-01-01T12:00:00.000Z")
    * res2: StringResult[OffsetDateTime] = Right(2000-01-01T12:00Z)
    *   }}}
    */
  val defaultOffsetDateTimeFormat: Format = Format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

  /** Default `LocalDate` format.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .format(LocalDate.of(2000, 1, 1))
    * res1: String = 2000-01-01
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .parseLocalDate("2000-01-01")
    * res2: StringResult[LocalDate] = Right(2000-01-01)
    *   }}}
    */
  val defaultLocalDateFormat: Format = Format(DateTimeFormatter.ISO_LOCAL_DATE)

  /** Default `LocalTime` format.
    *
    * @example
    *   {{{
    * scala> import java.time._
    * scala> import kantan.codecs.strings._
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .format(LocalTime.of(12, 0, 0))
    * res1: String = 12:00:00
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .parseLocalTime("12:00:00.000")
    * res2: StringResult[LocalTime] = Right(12:00)
    *   }}}
    */
  val defaultLocalTimeFormat: Format = Format(DateTimeFormatter.ISO_LOCAL_TIME)

}
