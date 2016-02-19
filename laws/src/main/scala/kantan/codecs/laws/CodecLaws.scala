package kantan.codecs.laws

import kantan.codecs._

trait CodecLaws[E, D, F, Dec[DD] <: Decoder[E, DD, F, Dec], Enc[DD] <: Encoder[E, DD, Enc]]
  extends DecoderLaws[E, D, F, Dec] with EncoderLaws[E, D, Enc] {

  def roundTripEncoding(d: D): Boolean = decoder.decode(encoder.encode(d)) == Result.success(d)
  def roundTripDecoding(e: E): Boolean = decoder.decode(e).map(encoder.encode) == Result.success(e)
}

object CodecLaws {
  implicit def apply[E, D, F, Dec[DD] <: Decoder[E, DD, F, Dec], Enc[DD] <: Encoder[E, DD, Enc]](implicit de: Decoder[E, D, F, Dec], ee: Encoder[E, D, Enc])
  : CodecLaws[E, D, F, Dec, Enc] = new CodecLaws[E, D, F, Dec, Enc] {
    override def encoder = ee
    override def decoder = de
  }
}
