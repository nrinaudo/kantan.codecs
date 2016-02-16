package kantan.codecs

trait Decoder[E, D, F, R[DD] <: Decoder[E, DD, F, R]] { self: R[D] ⇒
  /** Decodes encoded data. */
  def decode(e: E): DecodeResult[F, D]

  protected def copy[DD](f: E ⇒ DecodeResult[F, DD]): R[DD]

  def map[DD](f: D ⇒ DD): R[DD] = copy(e ⇒ decode(e).map(f))
  def mapResult[DD](f: D ⇒ DecodeResult[F, DD]): R[DD] = copy(e ⇒ decode(e).flatMap(f))
}