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

package kantan.codecs.strings.joda.time.laws.discipline

import java.util.Date
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.scalacheck.{Arbitrary, Cogen}

object arbitrary extends ArbitraryInstances with kantan.codecs.laws.discipline.ArbitraryInstances

trait ArbitraryInstances {
  import kantan.codecs.laws.discipline.arbitrary.arbDate

  implicit val arbDateTime: Arbitrary[DateTime] =
    Arbitrary(Arbitrary.arbitrary[Date].map(date ⇒ new DateTime(date.getTime)))

  implicit val arbLocalDateTime: Arbitrary[LocalDateTime] = Arbitrary(Arbitrary.arbitrary[Date].map { date ⇒
    new LocalDateTime(date.getTime).withMillisOfSecond(0)
  })

  implicit val arbLocalDate: Arbitrary[LocalDate] = Arbitrary(Arbitrary.arbitrary[Date].map { date ⇒
    new LocalDate(date.getTime)
  })

  implicit val arbLocalTime: Arbitrary[LocalTime] = Arbitrary(Arbitrary.arbitrary[Date].map { date ⇒
    new LocalTime(date.getTime).withMillisOfSecond(0)
  })

  implicit val cogenDateTime: Cogen[DateTime] = Cogen(_.getMillis)
  implicit val cogenLocalDateTime: Cogen[LocalDateTime] =
    Cogen.tuple4[Int, Int, Int, Int].contramap(d ⇒ (d.getYear, d.getMonthOfYear, d.getDayOfMonth, d.getMillisOfDay))
  implicit val cogenLocalDate: Cogen[LocalDate] =
    Cogen.tuple3[Int, Int, Int].contramap(d ⇒ (d.getYear, d.getMonthOfYear, d.getDayOfMonth))
  implicit val cogenLocalTime: Cogen[LocalTime] = Cogen.tuple4[Int, Int, Int, Int].contramap(d ⇒ (d.getHourOfDay,
    d.getMinuteOfHour, d.getSecondOfMinute, d.getMillisOfSecond))
}
