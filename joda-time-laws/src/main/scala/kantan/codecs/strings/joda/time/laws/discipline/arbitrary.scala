package kantan.codecs.strings.joda.time.laws.discipline

import kantan.codecs.laws.discipline.GenCodecValue
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.joda.time.format.DateTimeFormatter
import org.scalacheck.Arbitrary

object arbitrary {
  implicit val arbDateTime: Arbitrary[DateTime] =
    Arbitrary(Arbitrary.arbitrary[Long].map(offset ⇒ new DateTime(System.currentTimeMillis + offset)))

  implicit val arbLocalDateTime: Arbitrary[LocalDateTime] = Arbitrary(Arbitrary.arbitrary[Int].map { offset ⇒
    new LocalDateTime(System.currentTimeMillis + offset).withMillisOfSecond(0)
  })

  implicit val arbLocalDate: Arbitrary[LocalDate] = Arbitrary(Arbitrary.arbitrary[Int].map { offset ⇒
    new LocalDate(System.currentTimeMillis + offset)
  })

  implicit val arbLocalTime: Arbitrary[LocalTime] = Arbitrary(Arbitrary.arbitrary[Int].map { offset ⇒
    new LocalTime(System.currentTimeMillis + offset).withMillisOfSecond(0)
  })

  implicit def strDateTime(implicit format: DateTimeFormatter): GenCodecValue[String, DateTime] =
    GenCodecValue.nonFatal[String, DateTime](format.print)(format.parseDateTime)

  implicit def strLocalDateTime(implicit format: DateTimeFormatter): GenCodecValue[String, LocalDateTime] =
    GenCodecValue.nonFatal[String, LocalDateTime](format.print)(format.parseLocalDateTime)

  implicit def strLocalDate(implicit format: DateTimeFormatter): GenCodecValue[String, LocalDate] =
    GenCodecValue.nonFatal[String, LocalDate](format.print)(format.parseLocalDate)

  implicit def strLocalTime(implicit format: DateTimeFormatter): GenCodecValue[String, LocalTime] =
    GenCodecValue.nonFatal[String, LocalTime](format.print)(format.parseLocalTime)
}
