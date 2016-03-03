package kantan.codecs.simple

import java.util.UUID

import kantan.codecs.Result

object Codecs {
  implicit val bigDecimal: SimpleCodec[BigDecimal] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(BigDecimal(s)))(_.toString)
  implicit val bigInt: SimpleCodec[BigInt] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(BigInt(s)))(_.toString)
  implicit val boolean: SimpleCodec[Boolean] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toBoolean))(_.toString)
  implicit val char: SimpleCodec[Char] = SimpleCodec { s ⇒
    if(s.length == 1) Result.success(s.charAt(0))
    else              Result.failure(true)
  }(_.toString)
  implicit val double: SimpleCodec[Double] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toDouble))(_.toString)
  implicit val byte: SimpleCodec[Byte] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toByte))(_.toString)
  implicit val float: SimpleCodec[Float] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toFloat))(_.toString)
  implicit val int: SimpleCodec[Int] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toInt))(_.toString)
  implicit val long: SimpleCodec[Long] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toLong))(_.toString)
  implicit val short: SimpleCodec[Short] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s.toShort))(_.toString)
  implicit val string: SimpleCodec[String] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(s))(_.toString)
  implicit val uuid: SimpleCodec[UUID] = SimpleCodec(s ⇒ Result.nonFatalOr(true)(UUID.fromString(s)))(_.toString)
  implicit def option[A](implicit ca: SimpleCodec[A]): SimpleCodec[Option[A]] = SimpleCodec { s ⇒
    if(s.isEmpty) Result.success(None)
    else          ca.decode(s).map(Some.apply)
  }(_.map(ca.encode).getOrElse(""))
  implicit def either[A, B](implicit ca: SimpleCodec[A], cb: SimpleCodec[B]): SimpleCodec[Either[A, B]] = SimpleCodec { s ⇒
    ca.decode(s).map(a ⇒ Left(a): Either[A, B]).orElse(cb.decode(s).map(b ⇒ Right(b): Either[A, B]))
  }{_ match {
    case Left(a) ⇒ ca.encode(a)
    case Right(b) ⇒ cb.encode(b)
  }}
}
