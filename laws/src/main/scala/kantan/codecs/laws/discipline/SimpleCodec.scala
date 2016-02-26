package kantan.codecs.laws.discipline

import kantan.codecs.{Codec, Result}

/** Simple codec implementation.
  *
  * This is mostly meant for the cats & scalaz modules that need valid encoder and decoder implementations to test
  * their abstraction instances.
  */
trait SimpleCodec[A] extends Codec[String, A, Boolean, SimpleDecoder, SimpleEncoder] with SimpleDecoder[A] with SimpleEncoder[A]

object SimpleCodec {
  def apply[D](d: String ⇒ Result[Boolean, D])(e: D ⇒ String): SimpleCodec[D] = new SimpleCodec[D] {
    override def decode(e: String) = d(e)
    override def encode(d: D) = e(d)
  }
}
