package kantan.codecs.strings

import kantan.codecs.Encoder

object StringEncoder {
  def apply[D](f: D ⇒ String): StringEncoder[D] = Encoder(f)
}

trait StringEncoderInstances {
  implicit def optionEncoder[A](implicit ca: StringEncoder[A]): StringEncoder[Option[A]] =
    StringEncoder(_.map(ca.encode).getOrElse(""))

  implicit def eitherEncoder[A, B](implicit ca: StringEncoder[A], cb: StringEncoder[B]): StringEncoder[Either[A, B]] =
    StringEncoder {_ match {
    case Left(a) ⇒ ca.encode(a)
    case Right(b) ⇒ cb.encode(b)
  }}
}


