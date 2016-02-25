package kantan.codecs

/** Parent trait for all type classes used to encode types.
  *
  * @tparam E encoded type - what to encode to.
  * @tparam D decoded type - what to encode from.
  * @tparam R implementation type - what will be returned by [[contramap]].
  */
trait Encoder[E, D, R[DD] <: Encoder[E, DD, R]] { self: R[D] ⇒
  /** Encodes the specified value. */
  def encode(d: D): E

  /** Clones this encoder to create a new one with a different encoding function.
    *
    * This is not meant to be called directly. If you feel you need to, you're probably looking for [[contramap]].
    */
  protected def copy[DD](f: DD ⇒ E): R[DD]

  /** Creates a new [[Encoder]] instances that applies the specified function before encoding.
    *
    * This is a convenient way of creating [[Encoder]] instances: if you already have an `Encoder[E, D, R]`, need to
    * write an `Encoder[E, DD, R]` and know how to turn a `DD` into a `D`, you need but call [[contramap]].
    */
  def contramap[DD](f: DD ⇒ D): R[DD] = copy(f andThen encode)
}
