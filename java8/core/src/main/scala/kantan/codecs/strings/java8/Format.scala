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
package java8

import java.time._
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

/** `Serializable` wrapper around `DateTimeFormatter`.
  *
  * The main purpose of this is to make joda-time codecs `Serializable` and thus usable with frameworks like spark.
  */
sealed trait Format {

  protected def formatter: DateTimeFormatter

  def parseInstant(str: String): Instant               = Instant.from(formatter.parse(str))
  def parseLocalDateTime(str: String): LocalDateTime   = LocalDateTime.parse(str, formatter)
  def parseZonedDateTime(str: String): ZonedDateTime   = ZonedDateTime.parse(str, formatter)
  def parseLocalTime(str: String): LocalTime           = LocalTime.parse(str, formatter)
  def parseLocalDate(str: String): LocalDate           = LocalDate.parse(str, formatter)
  def parseOffsetDateTime(str: String): OffsetDateTime = OffsetDateTime.parse(str, formatter)

  def format(temporal: TemporalAccessor): String = formatter.format(temporal)

}

object Format {

  def apply(fmt: ⇒ DateTimeFormatter): Format = new Format with Serializable {
    @transient override lazy val formatter = fmt
  }

  def apply(str: String): Format = {
    val format: Format = new Format with Serializable {
      val pattern: String                    = str
      @transient override lazy val formatter = DateTimeFormatter.ofPattern(pattern)
    }
    format.formatter
    format
  }

  /** Default `ZonedDateTime` format.
    *
    * @example
    * {{{
    * scala> import java.time._
    *
    * scala> Format.defaultInstantFormat
    *      |   .format(Instant.parse("2000-01-01T12:00:00.000Z"))
    * res1: String = 2000-01-01T12:00:00Z
    *
    * scala> Format.defaultInstantFormat
    *      |   .parseInstant("2000-01-01T12:00:00.000Z")
    * res2: Instant = 2000-01-01T12:00:00Z
    * }}}
    */
  val defaultInstantFormat: Format = Format(DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC))

  /** Default `LocalDateTime` format.
    *
    * @example
    * {{{
    * scala> import java.time._
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .format(LocalDateTime.of(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .parseLocalDateTime("2000-01-01T12:00:00.000")
    * res2: LocalDateTime = 2000-01-01T12:00
    * }}}
    */
  val defaultLocalDateTimeFormat: Format = Format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

  /** Default `ZonedDateTime` format.
    *
    * @example
    * {{{
    * scala> import java.time._
    *
    * scala> Format.defaultZonedDateTimeFormat
    *      |   .format(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00Z
    *
    * scala> Format.defaultZonedDateTimeFormat
    *      |   .parseZonedDateTime("2000-01-01T12:00:00.000Z")
    * res2: ZonedDateTime = 2000-01-01T12:00Z
    * }}}
    */
  val defaultZonedDateTimeFormat: Format = Format(DateTimeFormatter.ISO_ZONED_DATE_TIME)

  /** Default `OffsetDateTime` format.
    *
    * @example
    * {{{
    * scala> import java.time._
    *
    * scala> Format.defaultOffsetDateTimeFormat
    *      |   .format(OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC))
    * res1: String = 2000-01-01T12:00:00Z
    *
    * scala> Format.defaultOffsetDateTimeFormat
    *      |   .parseOffsetDateTime("2000-01-01T12:00:00.000Z")
    * res2: OffsetDateTime = 2000-01-01T12:00Z
    * }}}
    */
  val defaultOffsetDateTimeFormat: Format = Format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

  /** Default `LocalDate` format.
    *
    * @example
    * {{{
    * scala> import java.time._
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .format(LocalDate.of(2000, 1, 1))
    * res1: String = 2000-01-01
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .parseLocalDate("2000-01-01")
    * res2: LocalDate = 2000-01-01
    * }}}
    */
  val defaultLocalDateFormat: Format = Format(DateTimeFormatter.ISO_LOCAL_DATE)

  /** Default `LocalTime` format.
    *
    * @example
    * {{{
    * scala> import java.time._
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .format(LocalTime.of(12, 0, 0))
    * res1: String = 12:00:00
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .parseLocalTime("12:00:00.000")
    * res2: LocalTime = 12:00
    * }}}
    */
  val defaultLocalTimeFormat: Format = Format(DateTimeFormatter.ISO_LOCAL_TIME)

}
