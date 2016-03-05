package kantan.codecs.strings

import kantan.codecs.{Codec, Encoder, Decoder}

object tagged {
  implicit def decoder[D](implicit d: StringDecoder[D]): Decoder[String, D, Throwable, tagged.type] = d.tag[tagged.type]
  implicit def encoder[D](implicit e: StringEncoder[D]): Encoder[String, D, tagged.type] = e.tag[tagged.type]

  def codec[D](implicit c: StringCodec[D]): Codec[String, D, Throwable, tagged.type] = c.tag[tagged.type]
}
