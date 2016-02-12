package kantan.codecs

trait Encoder[D, E] {
  def encode(d: D): E
  def contramap[D2](f: D2 ⇒ D): Encoder[D2, E] = Encoder(f andThen encode)
}

object Encoder {
  def apply[D, E](f: D ⇒ E): Encoder[D, E] = new Encoder[D, E] {
    override def encode(d: D) = f(d)
  }
}
