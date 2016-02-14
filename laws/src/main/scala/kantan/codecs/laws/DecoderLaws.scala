package kantan.codecs.laws

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.{DecodeResult, Decoder}

trait DecoderLaws[E, D, F] {
  def decoder: Decoder[E, D, F]

  private def cmp(result: DecodeResult[F, D], cv: CodecValue[E, D]): Boolean = (cv, result) match {
    case (IllegalValue(_),  DecodeResult.Failure(_))  ⇒ true
    case (LegalValue(_, d), DecodeResult.Success(d2)) ⇒ d == d2
    case _                                            ⇒ false
  }



  // - Simple laws -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def decode(v: CodecValue[E, D]): Boolean =
    cmp(decoder.decode(v.encoded), v)


  // - Functor laws ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapIdentity(v: CodecValue[E, D]): Boolean =
    decoder.decode(v.encoded) == decoder.map(identity).decode(v.encoded)

  def mapComposition[A, B](v: CodecValue[E, D], f: D ⇒ A, g: A ⇒ B): Boolean =
    decoder.map(f andThen g).decode(v.encoded) == decoder.map(f).map(g).decode(v.encoded)


  // - "Kleisli" laws --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapResultIdentity(v: CodecValue[E, D]): Boolean =
    decoder.decode(v.encoded) == decoder.mapResult(DecodeResult.success).decode(v.encoded)

  def mapResultComposition[A, B](v: CodecValue[E, D], f: D ⇒ DecodeResult[F, A], g: A ⇒ DecodeResult[F, B]): Boolean =
    decoder.mapResult(d ⇒ f(d).flatMap(g)).decode(v.encoded) ==  decoder.mapResult(f).mapResult(g).decode(v.encoded)
}

object DecoderLaws {
  implicit def apply[E, D, F](implicit de: Decoder[E, D, F]): DecoderLaws[E, D, F] = new DecoderLaws[E, D, F] {
    override val decoder = de
  }
}