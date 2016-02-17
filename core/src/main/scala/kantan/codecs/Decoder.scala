package kantan.codecs

/** Parent trait for all type classes used to decode a useful type from an encoded value.
  *
  * @tparam E encoded type - what to decode from.
  * @tparam D decoded type - what to decode to.
  * @tparam F failure type - how to represent errors.
  * @tparam R implementation type - what will be returned by the various map operations.
  */
trait Decoder[E, D, F, R[DD] <: Decoder[E, DD, F, R]] { self: R[D] ⇒
  /** Decodes encoded data. */
  def decode(e: E): Result[F, D]

  /** Clones this decoder to create a new one with a different decoding function. */
  protected def copy[DD](f: E ⇒ Result[F, DD]): R[DD]

  def map[DD](f: D ⇒ DD): R[DD] = copy(e ⇒ decode(e).map(f))
  def mapResult[DD](f: D ⇒ Result[F, DD]): R[DD] = copy(e ⇒ decode(e).flatMap(f))
}