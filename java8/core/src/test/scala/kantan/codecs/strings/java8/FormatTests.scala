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

package kantan
package codecs
package strings
package java8

import java.time._
import java.time.format.DateTimeFormatter
import laws.discipline._, arbitrary._
import org.scalatest.EitherValues._

sealed abstract class FormatTests(label: String, f: String ⇒ Format) extends DisciplineSuite {

  checkAll(s"Format[$label]", SerializableTests(kantan.codecs.laws.SerializableLaws(f("yyyyMM"))).serializable)

  test("Format should yield the same results as the corresponding DateTimeFormatter for LocalDateTime") {

    val formatStr = "yyyy-MM-dd'T'HH:mm:ss"

    val format    = f(formatStr)
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { d: LocalDateTime ⇒
      val str: String = formatter.format(d)

      format.parseLocalDateTime(str).right.value should be(LocalDateTime.parse(str, formatter))

    }
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for ZonedDateTime") {

    val formatStr = "yyyy-MM-dd'T'HH:mm:ssxx"

    val format    = f(formatStr)
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { d: ZonedDateTime ⇒
      val str: String = formatter.format(d)

      format.parseZonedDateTime(str).right.value should be(ZonedDateTime.parse(str, formatter))
    }
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for LocalTime") {

    val formatStr = "HH:mm:ss"

    val format    = f(formatStr)
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { d: LocalTime ⇒
      val str: String = formatter.format(d)

      format.parseLocalTime(str).right.value should be(LocalTime.parse(str, formatter))

    }
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for LocalDate") {

    val formatStr = "yyyy-MM-dd"

    val format    = f(formatStr)
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { d: LocalDate ⇒
      val str: String = formatter.format(d)

      format.parseLocalDate(str).right.value should be(LocalDate.parse(str, formatter))
    }
  }

  test("Format should yield the same results as the corresponding DateTimeFormatter for OffsetDateTime") {

    val formatStr = "yyyy-MM-dd'T'HH:mm:ssxx"

    val format    = f(formatStr)
    val formatter = DateTimeFormatter.ofPattern(formatStr)

    forAll { d: ZonedDateTime ⇒
      val str: String = formatter.format(d)

      format.parseZonedDateTime(str).right.value should be(ZonedDateTime.parse(str, formatter))

    }
  }

  // Skipping tests for Instant, as this is not supported without ZoneId information that is impossible to convey
  // in a pattern.

}

class StringFormatTests            extends FormatTests("String", Format.apply)
class DateTimeFormatterFormatTests extends FormatTests("DateTimeFormatter", s ⇒ Format(DateTimeFormatter.ofPattern(s)))
