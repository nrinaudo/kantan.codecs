package kantan.codecs.laws

import kantan.codecs.{DecodeResult, Decoder}

trait SimpleDecoder[A] extends Decoder[String, A, Boolean, SimpleDecoder] {
  def decode(e: String): DecodeResult[Boolean, A]
  override protected def copy[DD](f: String => DecodeResult[Boolean, DD]) = SimpleDecoder(f)
}

object SimpleDecoder {
  def apply[A](f: String ⇒ DecodeResult[Boolean, A]) = new SimpleDecoder[A] {
    override def decode(e: String): DecodeResult[Boolean, A] = f(e)
  }
}