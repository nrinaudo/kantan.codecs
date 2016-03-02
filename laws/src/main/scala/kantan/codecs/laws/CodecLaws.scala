package kantan.codecs.laws

import kantan.codecs._

trait CodecLaws[E, D, F, T] extends DecoderLaws[E, D, F, T] with EncoderLaws[E, D, T] {

  def roundTripEncoding(d: D): Boolean = decoder.decode(encoder.encode(d)) == Result.success(d)
  def roundTripDecoding(e: E): Boolean = decoder.decode(e).map(encoder.encode) == Result.success(e)
}

object CodecLaws {
  implicit def apply[E, D, F, T](implicit de: Decoder[E, D, F, T], ee: Encoder[E, D, T]): CodecLaws[E, D, F, T] =
    new CodecLaws[E, D, F, T] {
      override def encoder = ee
      override def decoder = de
    }
}
