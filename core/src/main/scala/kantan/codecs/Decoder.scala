package kantan.codecs

trait Decoder[E, D, F, R[DD] <: Decoder[E, DD, F, R]] { self: R[D] ⇒
  /** Decodes encoded data. */
  def decode(e: E): Result[F, D]

  protected def copy[DD](f: E ⇒ Result[F, DD]): R[DD]

  def map[DD](f: D ⇒ DD): R[DD] = copy(e ⇒ decode(e).map(f))
  def mapResult[DD](f: D ⇒ Result[F, DD]): R[DD] = copy(e ⇒ decode(e).flatMap(f))
}