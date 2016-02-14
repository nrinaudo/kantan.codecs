package kantan.codecs

trait Decoder[E, D, F] {
  /** Decodes encoded data. */
  def decode(e: E): DecodeResult[F, D]

  def map[DD](f: D ⇒ DD): Decoder[E, DD, F] = Decoder(e ⇒ decode(e).map(f))
  def mapResult[DD](f: D ⇒ DecodeResult[F, DD]): Decoder[E, DD, F] = Decoder(e ⇒ decode(e).flatMap(f))
}

object Decoder {
  def apply[E, D, F](f: E ⇒ DecodeResult[F, D]): Decoder[E, D, F] = new Decoder[E, D, F] {
    override def decode(e: E) = f(e)
  }
}
