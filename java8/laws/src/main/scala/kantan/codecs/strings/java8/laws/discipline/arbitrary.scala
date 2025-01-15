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

package kantan.codecs.strings.java8.laws.discipline

import kantan.codecs.laws.IllegalString
import kantan.codecs.laws.LegalString
import org.scalacheck.Arbitrary
import org.scalacheck.Cogen
import org.scalacheck.Gen

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters._
import scala.util.Try

object arbitrary extends ArbitraryInstances with kantan.codecs.laws.discipline.ArbitraryInstances

// This is mostly ripped straight from circe's implementation.
trait ArbitraryInstances {
  import kantan.codecs.laws.discipline.arbitrary._

  // - Instant instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  private[this] val minInstant: Instant = Instant.EPOCH
  private[this] val maxInstant: Instant = Instant.parse("3000-01-01T00:00:00.00Z")

  implicit val arbInstant: Arbitrary[Instant] =
    Arbitrary(Gen.choose(minInstant.getEpochSecond, maxInstant.getEpochSecond).map(Instant.ofEpochSecond))

  implicit val cogenInstant: Cogen[Instant] = Cogen(_.toEpochMilli)

  implicit val arbLegalInstantString: Arbitrary[LegalString[Instant]] =
    arbLegalValue(DateTimeFormatter.ISO_INSTANT.format)
  implicit val arbIllegalInstantString: Arbitrary[IllegalString[Instant]] =
    arbIllegalValue(s => Try(Instant.parse(s)).isFailure)

  // - LocalDate instances ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbLocalDate: Arbitrary[LocalDate] = Arbitrary(Arbitrary.arbitrary[LocalDateTime].map(_.toLocalDate))

  implicit val cogenLocalDate: Cogen[LocalDate] =
    Cogen.tuple3[Int, Int, Int].contramap(date => (date.getYear, date.getMonthValue, date.getDayOfMonth))

  implicit val arbLegalLocalDateString: Arbitrary[LegalString[LocalDate]] =
    arbLegalValue(DateTimeFormatter.ISO_LOCAL_DATE.format)
  implicit val arbIllegalLocalDateString: Arbitrary[IllegalString[LocalDate]] =
    arbIllegalValue(s => Try(LocalDate.parse(s)).isFailure)

  // - LocalTime instances ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbLocalTime: Arbitrary[LocalTime] = Arbitrary(Arbitrary.arbitrary[LocalDateTime].map(_.toLocalTime))

  implicit val cogenLocalTime: Cogen[LocalTime] =
    Cogen.tuple4[Int, Int, Int, Int].contramap(time => (time.getHour, time.getMinute, time.getSecond, time.getNano))

  implicit val arbLegalLocalTimeString: Arbitrary[LegalString[LocalTime]] =
    arbLegalValue(DateTimeFormatter.ISO_LOCAL_TIME.format)
  implicit val arbIllegalLocalTimeString: Arbitrary[IllegalString[LocalTime]] =
    arbIllegalValue(s => Try(LocalTime.parse(s)).isFailure)

  // - LocalDateTime instances -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbitraryZoneId: Arbitrary[ZoneId] = Arbitrary(
    Gen.oneOf(ZoneId.getAvailableZoneIds.asScala.map(ZoneId.of).toSeq)
  )

  implicit val arbLocalDateTime: Arbitrary[LocalDateTime] = Arbitrary(
    for {
      instant <- Arbitrary.arbitrary[Instant]
      zoneId  <- Arbitrary.arbitrary[ZoneId]
    } yield LocalDateTime.ofInstant(instant, zoneId)
  )

  implicit val cogenLocalDateTime: Cogen[LocalDateTime] = Cogen.tuple2[LocalDate, LocalTime].contramap { dt =>
    (dt.toLocalDate, dt.toLocalTime)
  }

  implicit val arbLegalLocalDateTimeString: Arbitrary[LegalString[LocalDateTime]] =
    arbLegalValue(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format)
  implicit val arbIllegalLocalDateTimeString: Arbitrary[IllegalString[LocalDateTime]] =
    arbIllegalValue(s => Try(LocalDateTime.parse(s)).isFailure)

  // - ZonedDateTime instances -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbZonedDateTime: Arbitrary[ZonedDateTime] = Arbitrary(
    for {
      instant <- Arbitrary.arbitrary[Instant]
      zoneId  <- Arbitrary.arbitrary[ZoneId]
    } yield ZonedDateTime.ofInstant(
      instant,
      if(zoneId == ZoneId.of("GMT0")) ZoneId.of("UTC") else zoneId // avoid JDK-8138664
    )
  )

  implicit val cogenZonedDateTime: Cogen[ZonedDateTime] =
    Cogen.tuple2[LocalDate, LocalTime].contramap(dt => (dt.toLocalDate, dt.toLocalTime))

  implicit val arbLegalZonedDateTimeString: Arbitrary[LegalString[ZonedDateTime]] =
    arbLegalValue(DateTimeFormatter.ISO_ZONED_DATE_TIME.format)
  implicit val arbIllegalZonedDateTimeString: Arbitrary[IllegalString[ZonedDateTime]] =
    arbIllegalValue(s => Try(ZonedDateTime.parse(s)).isFailure)

  // - OffsetDateTime instances ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbOffsetDateTime: Arbitrary[OffsetDateTime] = Arbitrary(
    for {
      instant <- Arbitrary.arbitrary[Instant]
      zoneId  <- Arbitrary.arbitrary[ZoneId]
    } yield OffsetDateTime.ofInstant(instant, zoneId)
  )

  implicit val cogenOffsetDateTime: Cogen[OffsetDateTime] =
    Cogen.tuple2[LocalDate, LocalTime].contramap(dt => (dt.toLocalDate, dt.toLocalTime))

  implicit val arbLegalOffsetDateTimeString: Arbitrary[LegalString[OffsetDateTime]] =
    arbLegalValue(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format)
  implicit val arbIllegalOffsetDateTimeString: Arbitrary[IllegalString[OffsetDateTime]] =
    arbIllegalValue(s => Try(OffsetDateTime.parse(s)).isFailure)
}
