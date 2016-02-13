package kantan.codecs.laws

import kantan.codecs.Encoder
import kantan.codecs.laws.CodecValue.LegalValue

trait EncoderLaws[D, E] {
  def encoder: Encoder[D, E]


  // - Simple laws -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def encode(v: LegalValue[E, D]): Boolean = encoder.encode(v.decoded) == v.encoded


  // - Contravariant functor laws --------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def contramapIdentity(d: D): Boolean = encoder.encode(d) == encoder.contramap(identity[D]).encode(d)

  def contramapComposition[A, B](b: B, f: A ⇒ D, g: B ⇒ A): Boolean =
    encoder.contramap(g andThen f).encode(b) == encoder.contramap(f).contramap(g).encode(b)
}

object EncoderLaws {
  implicit def apply[D, E](implicit ee: Encoder[D, E]): EncoderLaws[D, E] = new EncoderLaws[D, E] {
    override val encoder = ee
  }
}