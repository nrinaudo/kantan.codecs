package kantan.codecs.laws.discipline

import java.util.UUID

import kantan.codecs.{Decoder, Result}

/** Simple decoder implementation.
  *
  * This is mostly meant for the cats & scalaz modules that need valid Decoder and decoder implementations to test
  * their abstraction instances.
  */
trait SimpleDecoder[A] extends Decoder[String, A, Boolean, SimpleDecoder] {
  override protected def copy[DD](f: String => Result[Boolean, DD]) = SimpleDecoder(f)
}

object SimpleDecoder {
  def apply[A](f: String ⇒ Result[Boolean, A]) = new SimpleDecoder[A] {
    override def decode(e: String): Result[Boolean, A] = f(e)
  }

  implicit val bigDecimal: SimpleDecoder[BigDecimal] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(BigDecimal(s)))
  implicit val bigInt: SimpleDecoder[BigInt] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(BigInt(s)))
  implicit val boolean: SimpleDecoder[Boolean] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s.toBoolean))
  implicit val char: SimpleDecoder[Char] = SimpleDecoder { s ⇒
    if(s.length == 1) Result.success(s.charAt(0))
    else              Result.failure(true)
  }
  implicit val double: SimpleDecoder[Double] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s.toDouble))
  implicit val byte: SimpleDecoder[Byte] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s.toByte))
  implicit val float: SimpleDecoder[Float] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s.toFloat))
  implicit val int: SimpleDecoder[Int] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s.toInt))
  implicit val long: SimpleDecoder[Long] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s.toLong))
  implicit val short: SimpleDecoder[Short] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s.toShort))
  implicit val string: SimpleDecoder[String] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(s))
  implicit val uuid: SimpleDecoder[UUID] = SimpleDecoder(s ⇒ Result.nonFatalOr(true)(UUID.fromString(s)))
  implicit def option[A](implicit da: SimpleDecoder[A]): SimpleDecoder[Option[A]] = SimpleDecoder { s ⇒
    if(s.isEmpty) Result.success(None)
    else          da.decode(s).map(Some.apply)
  }
  implicit def either[A, B](implicit da: SimpleDecoder[A], db: SimpleDecoder[B]): SimpleDecoder[Either[A, B]] =
    SimpleDecoder { s ⇒
      da.decode(s).map(a ⇒ Left(a): Either[A, B]).orElse(db.decode(s).map(b ⇒ Right(b): Either[A, B]))
    }
}