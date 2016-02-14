package kantan.codecs

trait Decoder[E, D, F] {
  /** Decodes encoded data. */
  def decode(e: E): DecodeResult[F, D]

  def map[D2](f: D ⇒ D2): Decoder[E, D2, F] = Decoder(e ⇒ decode(e).map(f))
  def mapResult[D2](f: D ⇒ DecodeResult[F, D2]): Decoder[E, D2, F] = Decoder(e ⇒ decode(e).flatMap(f))
}

object Decoder {
  def apply[E, D, F](f: E ⇒ DecodeResult[F, D]): Decoder[E, D, F] = new Decoder[E, D, F] {
    override def decode(e: E) = f(e)
  }
}
