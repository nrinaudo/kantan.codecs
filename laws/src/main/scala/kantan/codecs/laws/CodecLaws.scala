package kantan.codecs.laws

import kantan.codecs._

trait CodecLaws[E, D, F, T] extends DecoderLaws[E, D, F, T] with EncoderLaws[E, D, T] {
  implicit lazy val codec = Codec(decoder.decode _)(encoder.encode _)


  // - Misc. laws ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapErrorIdentityEncoding[A](d: D): Boolean =
      codec.encode(d) == codec.mapError(identity).encode(d)

  def mapErrorCompositionEncoding[A, B](d: D, f: F ⇒ A, g: A ⇒ B): Boolean =
    codec.mapError(f andThen g).encode(d) == codec.mapError(f).mapError(g).encode(d)



  // - Invariant functor laws ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def imapIdentityEncoding(v: CodecValue[E, D]): Boolean =
    codec.imap(identity[D])(identity[D]).decode(v.encoded) == codec.decode(v.encoded)

  def imapIdentityDecoding(d: D): Boolean =
      codec.imap(identity[D])(identity[D]).encode(d) == codec.encode(d)

  def imapCompositionEncoding[A, B](b: B, f1: D => A, f2: A => D, g1: A => B, g2: B => A): Boolean =
      codec.imap(f1)(f2).imap(g1)(g2).encode(b) == codec.imap(g1 compose f1)(f2 compose g2).encode(b)

  def imapCompositionDecoding[A, B](v: CodecValue[E, D], f1: D => A, f2: A => D, g1: A => B, g2: B => A): Boolean =
    codec.imap(f1)(f2).imap(g1)(g2).decode(v.encoded) == codec.imap(g1 compose f1)(f2 compose g2).decode(v.encoded)

  def imapEncodedIdentityEncoding(d: D): Boolean =
    codec.imapEncoded(identity[E])(identity[E]).encode(d) == codec.encode(d)

  def imapEncodedIdentityDecoding(v: CodecValue[E, D]): Boolean =
      codec.imapEncoded(identity[E])(identity[E]).decode(v.encoded) == codec.decode(v.encoded)

  def imapEncodedCompositionEncoding[A, B](d: D, f1: E => A, f2: A => E, g1: A => B, g2: B => A): Boolean =
    codec.imapEncoded(f1)(f2).imapEncoded(g1)(g2).encode(d) == codec.imapEncoded(g1 compose f1)(f2 compose g2).encode(d)

  def imapEncodedCompositionDecoding[A, B](b: B, f1: E => A, f2: A => E, g1: A => B, g2: B => A): Boolean =
      codec.imapEncoded(f1)(f2).imapEncoded(g1)(g2).decode(b) == codec.imapEncoded(g1 compose f1)(f2 compose g2).decode(b)



  // - Round trip laws -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def roundTripEncoding(d: D): Boolean = decoder.decode(encoder.encode(d)) == Result.success(d)
  def roundTripDecoding(e: E): Boolean = decoder.decode(e).map(encoder.encode) == Result.success(e)
}

object CodecLaws {
  implicit def apply[E, D, F, T](implicit de: Decoder[E, D, F, T], ee: Encoder[E, D, T]): CodecLaws[E, D, F, T] =
    new CodecLaws[E, D, F, T] {
      override val encoder = ee
      override val decoder = de
    }
}
