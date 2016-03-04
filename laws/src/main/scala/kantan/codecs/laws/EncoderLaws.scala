package kantan.codecs.laws

import kantan.codecs.Encoder
import kantan.codecs.laws.CodecValue.LegalValue

trait EncoderLaws[E, D, T] {
  def encoder: Encoder[E, D, T]


  // - Simple laws -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def encode(v: LegalValue[E, D]): Boolean = encoder.encode(v.decoded) == v.encoded


  // - Covariant functor laws ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapEncodedIdentity(d: D): Boolean = encoder.encode(d) == encoder.mapEncoded(identity).encode(d)

  def mapEncodedComposition[A, B](d: D, f: E ⇒ A, g: A ⇒ B): Boolean =
    encoder.mapEncoded(f andThen g).encode(d) == encoder.mapEncoded(f).mapEncoded(g).encode(d)



  // - Contravariant functor laws --------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def contramapIdentity(d: D): Boolean = encoder.encode(d) == encoder.contramap(identity[D]).encode(d)

  def contramapComposition[A, B](b: B, f: A ⇒ D, g: B ⇒ A): Boolean =
    encoder.contramap(g andThen f).encode(b) == encoder.contramap(f).contramap(g).encode(b)
}

object EncoderLaws {
  implicit def apply[E, D, T](implicit ee: Encoder[E, D, T]): EncoderLaws[E, D, T] = new EncoderLaws[E, D, T] {
    override val encoder = ee
  }
}