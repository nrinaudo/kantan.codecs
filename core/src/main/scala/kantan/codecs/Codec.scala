package kantan.codecs

/** Combines a [[Decoder]] and an [[Encoder]].
  *
  * Codecs are only meant as a convenience, and should not be considered more powerful or desirable than encoders or
  * decoders. Some types can be both encoded to and decoded from, and being able to define both instances in one call
  * is convenient. It's however very poor practice to request a type to have a [[Codec]] instance - a much preferred
  * alternative would be to require it to have a [[Decoder]] and an [[Encoder]] instance, which a [[Codec]] would
  * fulfill.
  */
trait Codec[E, D, F, Dec[DD] <: Decoder[E, DD, F, Dec], Enc[DD] <: Encoder[E, DD, Enc]]
  extends Decoder[E, D, F, Dec] with Encoder[E, D, Enc] { self: Dec[D] with Enc[D] =>
}