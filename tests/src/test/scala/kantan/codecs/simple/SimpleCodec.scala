package kantan.codecs.simple

import kantan.codecs.{Codec, Result}

object SimpleCodec {
  def apply[D](f: String ⇒ Result[Boolean, D])(g: D ⇒ String): SimpleCodec[D] =
    Codec(f)(g)
}
