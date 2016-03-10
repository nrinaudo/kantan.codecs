package kantan.codecs.strings

import kantan.codecs.Encoder

object StringEncoder {
  def apply[D](f: D ⇒ String): StringEncoder[D] = Encoder(f)
}

/** Defines default instances of [[StringEncoder]]. */
trait StringEncoderInstances {
  /** Encoder for `Option[A]`, for any `A` that has a [[StringEncoder]] instance. */
  implicit def optionEncoder[A](implicit ca: StringEncoder[A]): StringEncoder[Option[A]] =
    StringEncoder(_.map(ca.encode).getOrElse(""))

  /** Encoder for `Either[A, B]`, for any `A` and `B` that have a [[StringEncoder]] instance. */
  implicit def eitherEncoder[A, B](implicit ca: StringEncoder[A], cb: StringEncoder[B]): StringEncoder[Either[A, B]] =
    StringEncoder {_ match {
      case Left(a) ⇒ ca.encode(a)
      case Right(b) ⇒ cb.encode(b)
    }}
}


