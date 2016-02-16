package kantan.codecs

trait Encoder[E, D, R[DD] <: Encoder[E, DD, R]] { self: R[D] ⇒
  def encode(d: D): E

  protected def copy[DD](f: DD ⇒ E): R[DD]

  def contramap[DD](f: DD ⇒ D): R[DD] = copy(f andThen encode)
}
