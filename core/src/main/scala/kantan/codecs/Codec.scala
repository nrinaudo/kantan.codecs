package kantan.codecs

trait Codec[E, D, F, Dec[DD] <: Decoder[E, DD, F, Dec], Enc[DD] <: Encoder[E, DD, Enc]]
  extends Decoder[E, D, F, Dec] with Encoder[E, D, Enc] { self: Dec[D] with Enc[D] =>
}