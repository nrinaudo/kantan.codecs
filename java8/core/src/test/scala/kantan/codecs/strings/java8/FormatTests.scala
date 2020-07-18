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

import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import kantan.codecs.strings.StringResult
import kantan.codecs.strings.java8.laws.discipline.{DisciplineSuite, SerializableTests}
import kantan.codecs.strings.java8.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary
import org.scalatest.compatible.Assertion

class FormatTests extends DisciplineSuite {

  // - Serialization laws ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  checkAll("Format (from literal)", SerializableTests(fmt"yyyyMM").serializable)
  checkAll(
    "Format (from DateTimeFormatter)",
    SerializableTests(Format(DateTimeFormatter.ofPattern("yyyyMM"))).serializable
  )

  // - Comparison against Java implementations -------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Makes sure that the specified format is handled identically by our implementation and Java's. */
  def testFormat[A <: TemporalAccessor: Arbitrary](
    format: String,
    kantanParse: (Format, String) => StringResult[A],
    javaParse: (DateTimeFormatter, String) => A
  ): Assertion = {

    val kantanFormat = Format.from(format).getOrElse(sys.error(s"Not a valid format: '$format"))
    val javaFormat   = DateTimeFormatter.ofPattern(format)

    forAll { a: A =>
      val str: String = javaFormat.format(a)

      kantanParse(kantanFormat, str) should be(Right(javaParse(javaFormat, str)))

    }
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for LocalDateTime") {
    testFormat(
      format = "yyyy-MM-dd'T'HH:mm:ss",
      kantanParse = (format, str) => format.parseLocalDateTime(str),
      javaParse = (formatter, str) => LocalDateTime.parse(str, formatter)
    )
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for ZonedDateTime") {
    testFormat(
      format = "yyyy-MM-dd'T'HH:mm:ssxx",
      kantanParse = (format, str) => format.parseZonedDateTime(str),
      javaParse = (formatter, str) => ZonedDateTime.parse(str, formatter)
    )
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for LocalTime") {
    testFormat(
      format = "HH:mm:ss",
      kantanParse = (format, str) => format.parseLocalTime(str),
      javaParse = (formatter, str) => LocalTime.parse(str, formatter)
    )
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for LocalDate") {
    testFormat(
      format = "yyyy-MM-dd",
      kantanParse = (format, str) => format.parseLocalDate(str),
      javaParse = (formatter, str) => LocalDate.parse(str, formatter)
    )
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for OffsetDateTime") {
    testFormat(
      format = "yyyy-MM-dd'T'HH:mm:ssxx",
      kantanParse = (format, str) => format.parseZonedDateTime(str),
      javaParse = (formatter, str) => ZonedDateTime.parse(str, formatter)
    )
  }

  // Skipping tests for Instant, as this is not supported without ZoneId information that is impossible to convey
  // in a pattern.

}
