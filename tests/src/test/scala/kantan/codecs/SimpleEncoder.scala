package kantan.codecs

import java.util.UUID

/** Simple encoder implementation.
  *
  * This is mostly meant for the cats & scalaz modules that need valid encoder and decoder implementations to test
  * their abstraction instances.
  */
trait SimpleEncoder[A] extends Encoder[String, A, SimpleEncoder] {
  override protected def copy[DD](f: DD => String) = SimpleEncoder(f)
}

object SimpleEncoder {
  def apply[A](f: A ⇒ String) = new SimpleEncoder[A] {
    override def encode(a: A): String = f(a)
  }

  implicit val bigDecimal: SimpleEncoder[BigDecimal] = SimpleEncoder(_.toString)
  implicit val bigInt: SimpleEncoder[BigInt] = SimpleEncoder(_.toString)
  implicit val boolean: SimpleEncoder[Boolean] = SimpleEncoder(_.toString)
  implicit val char: SimpleEncoder[Char] = SimpleEncoder(_.toString)
  implicit val double: SimpleEncoder[Double] = SimpleEncoder(_.toString)
  implicit val byte: SimpleEncoder[Byte] = SimpleEncoder(_.toString)
  implicit val float: SimpleEncoder[Float] = SimpleEncoder(_.toString)
  implicit val int: SimpleEncoder[Int] = SimpleEncoder(_.toString)
  implicit val long: SimpleEncoder[Long] = SimpleEncoder(_.toString)
  implicit val short: SimpleEncoder[Short] = SimpleEncoder(_.toString)
  implicit val string: SimpleEncoder[String] = SimpleEncoder(_.toString)
  implicit val uuid: SimpleEncoder[UUID] = SimpleEncoder(_.toString)
  implicit def option[A](implicit ea: SimpleEncoder[A]): SimpleEncoder[Option[A]] =
    SimpleEncoder(_.map(ea.encode).getOrElse(""))

  implicit def either[A, B](implicit ea: SimpleEncoder[A], eb: SimpleEncoder[B]): SimpleEncoder[Either[A, B]] =
    SimpleEncoder { _ match {
      case Left(a) ⇒ ea.encode(a)
      case Right(b) ⇒ eb.encode(b)
    }}
}
