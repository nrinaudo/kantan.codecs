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

import java.time._
import org.scalacheck.{Arbitrary, Cogen, Gen}
import scala.collection.JavaConverters._

object arbitrary extends ArbitraryInstances with kantan.codecs.laws.discipline.ArbitraryInstances

// This is mostly ripped straight from circe's implementation.
trait ArbitraryInstances {
  // - Instant instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  private[this] val minInstant: Instant = Instant.EPOCH
  private[this] val maxInstant: Instant = Instant.parse("3000-01-01T00:00:00.00Z")


  implicit val arbInstant: Arbitrary[Instant] =
    Arbitrary(Gen.choose(minInstant.getEpochSecond, maxInstant.getEpochSecond).map(Instant.ofEpochSecond))

  implicit val cogenInstant: Cogen[Instant] = Cogen(_.toEpochMilli)


  // - LocalDate instances ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbLocalDate: Arbitrary[LocalDate] = Arbitrary(Arbitrary.arbitrary[LocalDateTime].map(_.toLocalDate))

  implicit val cogenLocalDate: Cogen[LocalDate] =
    Cogen.tuple3[Int, Int, Int].contramap(date ⇒ (date.getYear, date.getMonthValue, date.getDayOfMonth))



  // - LocalTime instances ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbLocalTime: Arbitrary[LocalTime] = Arbitrary(Arbitrary.arbitrary[LocalDateTime].map(_.toLocalTime))

  implicit val cogenLocalTime: Cogen[LocalTime] =
    Cogen.tuple4[Int, Int, Int, Int].contramap(time ⇒ (time.getHour, time.getMinute, time.getSecond, time.getNano))



  // - LocalDateTime instances -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbitraryZoneId: Arbitrary[ZoneId] = Arbitrary(
    Gen.oneOf(ZoneId.getAvailableZoneIds.asScala.map(ZoneId.of).toSeq)
  )

  implicit val arbLocalDateTime: Arbitrary[LocalDateTime] = Arbitrary(
    for {
      instant ← Arbitrary.arbitrary[Instant]
      zoneId  ← Arbitrary.arbitrary[ZoneId]
    } yield LocalDateTime.ofInstant(instant, zoneId)
  )

  implicit val cogenLocalDateTime: Cogen[LocalDateTime] = Cogen.tuple2[LocalDate, LocalTime].contramap { dt ⇒
    (dt.toLocalDate, dt.toLocalTime)
  }



  // - ZonedDateTime instances -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbZonedDateTime: Arbitrary[ZonedDateTime] = Arbitrary(
    for {
      instant ← Arbitrary.arbitrary[Instant]
      zoneId  ← Arbitrary.arbitrary[ZoneId].suchThat(_ != ZoneId.of("GMT0")) // avoid JDK-8138664
    } yield ZonedDateTime.ofInstant(instant, zoneId)
  )

  implicit val cogenZonedDateTime: Cogen[ZonedDateTime] =
    Cogen.tuple2[LocalDate, LocalTime].contramap(dt ⇒ (dt.toLocalDate, dt.toLocalTime))



  // - OffsetDateTime instances ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbOffsetDateTime: Arbitrary[OffsetDateTime] = Arbitrary(
    for {
      instant ← Arbitrary.arbitrary[Instant]
      zoneId  ← Arbitrary.arbitrary[ZoneId]
    } yield OffsetDateTime.ofInstant(instant, zoneId)
  )

  implicit val cogenOffsetDateTime: Cogen[OffsetDateTime] =
    Cogen.tuple2[LocalDate, LocalTime].contramap(dt ⇒ (dt.toLocalDate, dt.toLocalTime))
}
