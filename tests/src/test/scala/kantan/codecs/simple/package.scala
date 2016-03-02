package kantan.codecs

package object simple {
  type Simple = SimpleCodec.type

  type SimpleDecoder[A] = Decoder[String, A, Boolean, Simple]
  type SimpleEncoder[A] = Encoder[String, A, Simple]
  type SimpleCodec[A] = Codec[String, A, Boolean, Simple]
}
