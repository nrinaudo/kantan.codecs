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

  protected def formatter: DateTimeFormatter

  def parseDateTime(str: String): DateTime           = formatter.parseDateTime(str)
  def parseLocalDateTime(str: String): LocalDateTime = formatter.parseLocalDateTime(str)
  def parseLocalTime(str: String): LocalTime         = formatter.parseLocalTime(str)
  def parseLocalDate(str: String): LocalDate         = formatter.parseLocalDate(str)

  def format(instant: ReadableInstant): String = formatter.print(instant)
  def format(instant: ReadablePartial): String = formatter.print(instant)

}

object Format {

  def apply(fmt: ⇒ DateTimeFormatter): Format = new Format with Serializable {
    @transient override lazy val formatter = fmt
  }

  def apply(str: String): Format = {
    val format: Format = new Format with Serializable {
      val pattern: String                    = str
      @transient override lazy val formatter = DateTimeFormat.forPattern(pattern)
    }
    format.formatter
    format
  }

  /** Default `DateTime` format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    *
    * scala> Format.defaultDateTimeFormat
    *      |   .format(new DateTime(2000, 1, 1, 12, 0, 0, DateTimeZone.UTC))
    * res1: String = 2000-01-01T12:00:00.000Z
    *
    * scala> Format.defaultDateTimeFormat
    *      |   .parseDateTime("2000-01-01T12:00:00.000Z")
    *      |   .withZone(DateTimeZone.UTC)
    * res2: DateTime = 2000-01-01T12:00:00.000Z
    * }}}
    */
  val defaultDateTimeFormat: Format = Format(ISODateTimeFormat.dateTime())

  /** Default `LocalDateTime` format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .format(new LocalDateTime(2000, 1, 1, 12, 0, 0))
    * res1: String = 2000-01-01T12:00:00.000
    *
    * scala> Format.defaultLocalDateTimeFormat
    *      |   .parseLocalDateTime("2000-01-01T12:00:00.000")
    * res2: LocalDateTime = 2000-01-01T12:00:00.000
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
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .format(new LocalDate(2000, 1, 1))
    * res1: String = 2000-01-01
    *
    * scala> Format.defaultLocalDateFormat
    *      |   .parseLocalDate("2000-01-01")
    * res2: LocalDate = 2000-01-01
    * }}}
    */
  val defaultLocalDateFormat: Format = Format(
    new DateTimeFormatter(ISODateTimeFormat.date().getPrinter, ISODateTimeFormat.localDateParser().getParser)
  )

  /** Default `LocalTime` format.
    *
    * @example
    * {{{
    * scala> import org.joda.time._
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .format(new LocalTime(12, 0, 0))
    * res1: String = 12:00:00.000
    *
    * scala> Format.defaultLocalTimeFormat
    *      |   .parseLocalTime("12:00:00.000")
    * res2: LocalTime = 12:00:00.000
    * }}}
    */
  val defaultLocalTimeFormat: Format = Format(
    new DateTimeFormatter(ISODateTimeFormat.time().getPrinter, ISODateTimeFormat.localTimeParser().getParser)
  )

}
