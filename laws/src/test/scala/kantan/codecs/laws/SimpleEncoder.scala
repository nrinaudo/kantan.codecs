package kantan.codecs.laws

import kantan.codecs.{DecodeResult, Encoder}

trait SimpleEncoder[A] extends Encoder[String, A, SimpleEncoder] {
  override protected def copy[DD](f: DD => String) = SimpleEncoder(f)
}

object SimpleEncoder {
  def apply[A](f: A ⇒ String) = new SimpleEncoder[A] {
    override def encode(a: A): String = f(a)
  }
}
