package kantan.codecs

/** Combines a [[Decoder]] and an [[Encoder]].
  *
  * Codecs are only meant as a convenience, and should not be considered more powerful or desirable than encoders or
  * decoders. Some types can be both encoded to and decoded from, and being able to define both instances in one call
  * is convenient. It's however very poor practice to request a type to have a [[Codec]] instance - a much preferred
  * alternative would be to require it to have a [[Decoder]] and an [[Encoder]] instance, which a [[Codec]] would
  * fulfill.
  */
trait Codec[E, D, F, T] extends Any with Decoder[E, D, F, T] with Encoder[E, D, T]

object Codec {
  def apply[E, D, F, T](f: E ⇒ Result[F, D])(g: D ⇒ E): Codec[E, D, F, T] = new Codec[E, D, F, T] {
    override def encode(d: D) = g(d)
    override def decode(e: E) = f(e)
  }
}