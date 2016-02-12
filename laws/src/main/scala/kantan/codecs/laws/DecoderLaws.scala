package kantan.codecs.laws

import kantan.codecs.{Encoder, DecodeResult, Decoder}

trait DecoderLaws[E, D, F] {
  def decoder: Decoder[E, D, F]
  def encode(d: D): E



  // - Simple laws -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def decode(d: D): Boolean = decoder.decode(encode(d)) == d


  // - Functor laws ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapIdentity(d: D): Boolean = decoder.decode(encode(d)) == decoder.map(identity).decode(encode(d))
  def mapComposition[A, B](d: D, f: D ⇒ A, g: A ⇒ B): Boolean =
    decoder.map(f andThen g).decode(encode(d)) == decoder.map(f).map(g).decode(encode(d))


  // - "Kleisli" laws --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapResultIdentity(d: D): Boolean =
    decoder.decode(encode(d)) == decoder.mapResult(DecodeResult.success).decode(encode(d))
  def mapResultComposition[A, B](d: D, f: D ⇒ DecodeResult[F, A], g: A ⇒ DecodeResult[F, B]): Boolean =
    decoder.mapResult(d ⇒ f(d).flatMap(g)).decode(encode(d)) ==  decoder.mapResult(f).mapResult(g).decode(encode(d))



  // - Monad laws ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // TODO
}

object DecoderLaws {
  def apply[E, D, F](f: D ⇒ E)(implicit de: Decoder[E, D, F]): DecoderLaws[E, D, F] = new DecoderLaws[E, D, F] {
    override val decoder = de
    override def encode(d: D) = f(d)
  }

  def fromCodec[E, D, F](implicit de: Decoder[E, D, F], en: Encoder[D, E]): DecoderLaws[E, D, F] =
    DecoderLaws(en.encode)
}