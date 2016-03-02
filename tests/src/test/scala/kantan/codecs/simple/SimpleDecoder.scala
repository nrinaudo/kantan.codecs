package kantan.codecs.simple

import kantan.codecs.{Decoder, Result}

object SimpleDecoder {
  def apply[D](f: String ⇒ Result[Boolean, D]): SimpleDecoder[D] = Decoder(f)
}
