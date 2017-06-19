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

package kantan.codecs.strings.joda.time

import org.joda.time._
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

trait Format {
  def formatter: DateTimeFormatter

  def parseDateTime(str: String): DateTime = formatter.parseDateTime(str)
  def parseLocalDateTime(str: String): LocalDateTime = formatter.parseLocalDateTime(str)
  def parseLocalTime(str: String): LocalTime = formatter.parseLocalTime(str)
  def parseLocalDate(str: String): LocalDate = formatter.parseLocalDate(str)

  def format(instant: ReadableInstant): String = formatter.print(instant)
  def format(instant: ReadablePartial): String = formatter.print(instant)
}

object Format {
  def apply(fmt: ⇒ DateTimeFormatter): Format = new Format with Serializable {
    @transient override lazy val formatter = fmt
  }

  def apply(str: String): Format = {
    val format: Format = new Format with Serializable {
      val pattern: String = str
      @transient override lazy val formatter = DateTimeFormat.forPattern(pattern)
    }
    format.formatter
    format
  }
}
