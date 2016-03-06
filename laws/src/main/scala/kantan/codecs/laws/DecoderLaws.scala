package kantan.codecs.laws

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.{Result, Decoder}
import org.scalacheck.Prop

trait DecoderLaws[E, D, F, T] {
  def decoder: Decoder[E, D, F, T]

  private def cmp(result: Result[F, D], cv: CodecValue[E, D]): Boolean = (cv, result) match {
    case (IllegalValue(_),  Result.Failure(_))  ⇒ true
    case (LegalValue(_, d), Result.Success(d2)) ⇒ d == d2
    case _                                      ⇒ false
  }



  // - Simple laws -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def decode(v: CodecValue[E, D]): Boolean = cmp(decoder.decode(v.encoded), v)

  def decodeFailure(v: IllegalValue[E, D]): Boolean =
    Prop.throws(classOf[Exception])(decoder.unsafeDecode(v.encoded))



  // - Functor laws ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapIdentity(v: CodecValue[E, D]): Boolean =
    decoder.decode(v.encoded) == decoder.map(identity).decode(v.encoded)

  def mapComposition[A, B](v: CodecValue[E, D], f: D ⇒ A, g: A ⇒ B): Boolean =
    decoder.map(f andThen g).decode(v.encoded) == decoder.map(f).map(g).decode(v.encoded)

  def mapErrorIdentity[A](v: CodecValue[E, D]): Boolean =
    decoder.decode(v.encoded) == decoder.mapError(identity).decode(v.encoded)

  def mapErrorComposition[A, B](v: CodecValue[E, D], f: F ⇒ A, g: A ⇒ B): Boolean =
    decoder.mapError(f andThen g).decode(v.encoded) == decoder.mapError(f).mapError(g).decode(v.encoded)



  // - Contravariant laws ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def contramapEncodedIdentity(v: CodecValue[E, D]): Boolean =
    decoder.decode(v.encoded) == decoder.contramapEncoded(identity[E]).decode(v.encoded)

  def contramapEncodedComposition[A, B](b: B, f: A ⇒ E, g: B ⇒ A): Boolean =
    decoder.contramapEncoded(g andThen f).decode(b) == decoder.contramapEncoded(f).contramapEncoded(g).decode(b)


  // - "Kleisli" laws --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapResultIdentity(v: CodecValue[E, D]): Boolean =
    decoder.decode(v.encoded) == decoder.mapResult(Result.success).decode(v.encoded)

  def mapResultComposition[A, B](v: CodecValue[E, D], f: D ⇒ Result[F, A], g: A ⇒ Result[F, B]): Boolean =
    decoder.mapResult(d ⇒ f(d).flatMap(g)).decode(v.encoded) ==  decoder.mapResult(f).mapResult(g).decode(v.encoded)
}

object DecoderLaws {
  implicit def apply[E, D, F, T](implicit de: Decoder[E, D, F, T]): DecoderLaws[E, D, F, T] = new DecoderLaws[E, D, F, T] {
    override val decoder = de
  }
}