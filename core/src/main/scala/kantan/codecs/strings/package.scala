package kantan.codecs

package object strings {
  type StringEncoder[A] = Encoder[String, A, Codecs.type]
  type StringDecoder[A] = Decoder[String, A, Throwable, Codecs.type]
  type StringCodec[A] = Codec[String, A, Throwable, Codecs.type]
}
