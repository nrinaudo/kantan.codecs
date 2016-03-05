package kantan.codecs

package object strings {
  type StringEncoder[A] = Encoder[String, A, codecs.type]
  type StringDecoder[A] = Decoder[String, A, Throwable, codecs.type]
  type StringCodec[A] = Codec[String, A, Throwable, codecs.type]
}
