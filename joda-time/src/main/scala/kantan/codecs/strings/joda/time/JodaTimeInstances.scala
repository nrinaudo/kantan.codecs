package kantan.codecs.strings.joda.time

import kantan.codecs.Result
import kantan.codecs.strings.{StringCodec, _}
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}
import org.joda.time.format.DateTimeFormatter

trait JodaTimeInstances {
  implicit def dateTimeCodec(implicit format: DateTimeFormatter): StringCodec[DateTime] =
    StringCodec(str ⇒ Result.nonFatal(format.parseDateTime(str)))(format.print)

  implicit def localDateCodec(implicit format: DateTimeFormatter): StringCodec[LocalDate] =
    StringCodec(str ⇒ Result.nonFatal(format.parseLocalDate(str)))(format.print)

  implicit def localDateTimeCodec(implicit format: DateTimeFormatter): StringCodec[LocalDateTime] =
    StringCodec(str ⇒ Result.nonFatal(format.parseLocalDateTime(str)))(format.print)

  implicit def localTimeCodec(implicit format: DateTimeFormatter): StringCodec[LocalTime] =
    StringCodec(str ⇒ Result.nonFatal(format.parseLocalTime(str)))(format.print)
}
