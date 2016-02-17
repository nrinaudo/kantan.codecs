package kantan.codecs.laws

import kantan.codecs.{Result, Decoder}

trait SimpleDecoder[A] extends Decoder[String, A, Boolean, SimpleDecoder] {
  override protected def copy[DD](f: String => Result[Boolean, DD]) = SimpleDecoder(f)
}

object SimpleDecoder {
  def apply[A](f: String ⇒ Result[Boolean, A]) = new SimpleDecoder[A] {
    override def decode(e: String): Result[Boolean, A] = f(e)
  }
}