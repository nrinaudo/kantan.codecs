package kantan.codecs.laws

import kantan.codecs.{Result, Codec}

trait SimpleCodec[A] extends Codec[String, A, Boolean, SimpleDecoder, SimpleEncoder] with SimpleDecoder[A] with SimpleEncoder[A]

object SimpleCodec {
  def apply[D](d: String ⇒ Result[Boolean, D])(e: D ⇒ String): SimpleCodec[D] = new SimpleCodec[D] {
    override def decode(e: String) = d(e)
    override def encode(d: D) = e(d)
  }
}
