package kantan.codecs

trait Encoder[E, D] {
  def encode(d: D): E
  def contramap[D2](f: D2 ⇒ D): Encoder[E, D2] = Encoder(f andThen encode)
}

object Encoder {
  def apply[E, D](f: D ⇒ E): Encoder[E, D] = new Encoder[E, D] {
    override def encode(d: D) = f(d)
  }
}
