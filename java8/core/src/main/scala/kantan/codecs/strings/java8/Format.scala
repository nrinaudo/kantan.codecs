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

trait Format {
  def formatter: DateTimeFormatter

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

  val defaultInstantFormat: Format        = Format(DateTimeFormatter.ISO_INSTANT)
  val defaultLocalDateTimeFormat: Format  = Format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
  val defaultZonedDateTimeFormat: Format  = Format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
  val defaultOffsetDateTimeFormat: Format = Format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
  val defaultLocalDateFormat: Format      = Format(DateTimeFormatter.ISO_LOCAL_DATE)
  val defaultLocalTimeFormat: Format      = Format(DateTimeFormatter.ISO_LOCAL_TIME)
}
