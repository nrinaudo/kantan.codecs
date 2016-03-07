package kantan.codecs.strings

import kantan.codecs.{Decoder, Result}

object StringDecoder {
  def apply[D](f: String ⇒ Result[Throwable, D]): StringDecoder[D] = Decoder(f)
}

trait StringDecoderInstances {
  implicit def optionDecoder[A](implicit ca: StringDecoder[A]): StringDecoder[Option[A]] = StringDecoder{ s ⇒
    if(s.isEmpty) Result.success(None)
    else          ca.decode(s).map(Some.apply)
  }

  implicit def eitherDecoder[A, B](implicit ca: StringDecoder[A], cb: StringDecoder[B]): StringDecoder[Either[A, B]] = StringDecoder { s ⇒
    ca.decode(s).map(a ⇒ Left(a): Either[A, B]).orElse(cb.decode(s).map(b ⇒ Right(b): Either[A, B]))
  }
}