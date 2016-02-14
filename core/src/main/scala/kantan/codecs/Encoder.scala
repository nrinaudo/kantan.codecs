package kantan.codecs

trait Encoder[E, D] {
  def encode(d: D): E
  def contramap[DD](f: DD ⇒ D): Encoder[E, DD] = Encoder(f andThen encode)
}

object Encoder {
  def apply[E, D](f: D ⇒ E): Encoder[E, D] = new Encoder[E, D] {
    override def encode(d: D) = f(d)
  }
}
