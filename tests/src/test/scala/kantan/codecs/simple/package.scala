package kantan.codecs

package object simple {
  type SimpleDecoder[A] = Decoder[String, A, Boolean, Codecs.type]
  type SimpleEncoder[A] = Encoder[String, A, Codecs.type]
  type SimpleCodec[A] = Codec[String, A, Boolean, Codecs.type]
}
