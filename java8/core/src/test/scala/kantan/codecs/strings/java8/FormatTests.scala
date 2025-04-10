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

import kantan.codecs.laws.SerializableLaws
import kantan.codecs.laws.discipline.DisciplineSuite
import kantan.codecs.laws.discipline.SerializableTests
import kantan.codecs.strings.java8.laws.discipline.arbitrary._

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FormatTests extends DisciplineSuite {

  checkAll("Format (from literal)", SerializableTests(SerializableLaws(fmt"yyyyMM")).serializable)
  checkAll(
    "Format (from DateTimeFormatter)",
    SerializableTests(SerializableLaws(Format(DateTimeFormatter.ofPattern("yyyyMM")))).serializable
  )

  test("Format should yield the same results as the corresponding DateTimeFormatter for LocalDateTime") {

    val formatStr = "yyyy-MM-dd'T'HH:mm:ss"

    val format    = Format.from(formatStr).getOrElse(sys.error(s"Not a valid format: '$formatStr"))
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { (d: LocalDateTime) =>
      val str: String = formatter.format(d)

      format.parseLocalDateTime(str) should be(Right(LocalDateTime.parse(str, formatter)))

    }
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for ZonedDateTime") {

    val formatStr = "yyyy-MM-dd'T'HH:mm:ssxx"

    val format    = Format.from(formatStr).getOrElse(sys.error(s"Not a valid format: '$formatStr"))
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { (d: ZonedDateTime) =>
      val str: String = formatter.format(d)

      format.parseZonedDateTime(str) should be(Right(ZonedDateTime.parse(str, formatter)))
    }
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for LocalTime") {

    val formatStr = "HH:mm:ss"

    val format    = Format.from(formatStr).getOrElse(sys.error(s"Not a valid format: '$formatStr"))
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { (d: LocalTime) =>
      val str: String = formatter.format(d)

      format.parseLocalTime(str) should be(Right(LocalTime.parse(str, formatter)))

    }
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for LocalDate") {

    val formatStr = "yyyy-MM-dd"

    val format    = Format.from(formatStr).getOrElse(sys.error(s"Not a valid format: '$formatStr"))
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { (d: LocalDate) =>
      val str: String = formatter.format(d)

      format.parseLocalDate(str) should be(Right(LocalDate.parse(str, formatter)))
    }
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for OffsetDateTime") {

    val formatStr = "yyyy-MM-dd'T'HH:mm:ssxx"

    val format    = Format.from(formatStr).getOrElse(sys.error(s"Not a valid format: '$formatStr"))
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { (d: ZonedDateTime) =>
      val str: String = formatter.format(d)

      format.parseZonedDateTime(str) should be(Right(ZonedDateTime.parse(str, formatter)))

    }
  }

  // Skipping tests for Instant, as this is not supported without ZoneId information that is impossible to convey
  // in a pattern.

}
