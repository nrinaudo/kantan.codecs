package kantan.codecs

/** Parent trait for all type classes used to decode a useful type from an encoded value.
  *
  * @tparam E encoded type - what to decode from.
  * @tparam D decoded type - what to decode to.
  * @tparam F failure type - how to represent errors.
  * @tparam R implementation type - what will be returned by the various map operations.
  */
trait Decoder[E, D, F, R[DD] <: Decoder[E, DD, F, R]] { self: R[D] ⇒
  /** Decodes encoded data.
    *
    * This method is safe, in that it won't throw for run-of-the-mill errors. Unrecoverable errors such as out of memory
    * exceptions are still thrown, but that's considered valid exceptional cases, where incorrectly encoded data is
    * just... normal.
    *
    * Callers that wish to fail fast and fail hard can use the [[unsafeDecode]] method instead.
    */
  def decode(e: E): Result[F, D]

  /** Decodes encoded data unsafely.
    *
    * The main difference between this and [[decode]] is that the former throws exceptions when errors occur where the
    * later safely encodes error conditions in its return type.
    */
  def unsafeDecode(e: E): D = decode(e).get

  /** Clones this decoder to create a new one with a different decoding function.
    *
    * This is not meant to be called directly. If you feel you need to, you're probably looking for [[map]].
    */
  protected def copy[DD](f: E ⇒ Result[F, DD]): R[DD]

  /** Creates a new [[Decoder]] instance that applies the specified function after decoding.
    *
    * This is convenient when building [[Decoder]] instances: when writing a `Decoder[E, D, F, R]`, it often happens
    * that you already have an instance of `Decoder[E, DD, F, R]` and a `D ⇒ DD`.
    */
  def map[DD](f: D ⇒ DD): R[DD] = copy(e ⇒ decode(e).map(f))

  /** Creates a new [[Decoder]] instance that applies the specified function after decoding.
    *
    * This is similar to [[map]], but is used for these cases when the transformation function is unsafe - you have
    * a `Decoder[String]` and want to turn it into a `Decoder[Int]`, for example.
    */
  def mapResult[DD](f: D ⇒ Result[F, DD]): R[DD] = copy(e ⇒ decode(e).flatMap(f))
}