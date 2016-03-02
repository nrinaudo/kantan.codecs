package kantan.codecs.simple

import kantan.codecs.Encoder

object SimpleEncoder {
  def apply[D](f: D ⇒ String): SimpleEncoder[D] = Encoder(f)
}
