package kantan.codecs.laws

import kantan.codecs._
import kantan.codecs.laws.CodecValue.LegalValue

trait CodecLaws[E, D, F, T] extends DecoderLaws[E, D, F, T] with EncoderLaws[E, D, T] {
  implicit lazy val codec = Codec(decoder.decode _)(encoder.encode _)


  // - Invariant functor laws ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def imapIdentityEncoding(v: CodecValue[E, D]): Boolean =
    codec.imap(identity[D])(identity[D]).decode(v.encoded) == codec.decode(v.encoded)

  def imapIdentityDecoding(v: LegalValue[E, D]): Boolean =
      codec.imap(identity[D])(identity[D]).encode(v.decoded) == codec.encode(v.decoded)

  def imapCompositionEncoding[A, B](b: B, f1: D => A, f2: A => D, g1: A => B, g2: B => A): Boolean =
      codec.imap(f1)(f2).imap(g1)(g2).encode(b) == codec.imap(g1 compose f1)(f2 compose g2).encode(b)

  def imapCompositionDecoding[A, B](v: CodecValue[E, D], f1: D => A, f2: A => D, g1: A => B, g2: B => A): Boolean =
    codec.imap(f1)(f2).imap(g1)(g2).decode(v.encoded) == codec.imap(g1 compose f1)(f2 compose g2).decode(v.encoded)

  def imapEncodedIdentityEncoding(v: LegalValue[E, D]): Boolean =
    codec.imapEncoded(identity[E])(identity[E]).encode(v.decoded) == codec.encode(v.decoded)

  def imapEncodedIdentityDecoding(v: CodecValue[E, D]): Boolean =
      codec.imapEncoded(identity[E])(identity[E]).decode(v.encoded) == codec.decode(v.encoded)

  def imapEncodedCompositionEncoding[A, B](v: LegalValue[E, D], f1: E => A, f2: A => E, g1: A => B, g2: B => A): Boolean =
    codec.imapEncoded(f1)(f2).imapEncoded(g1)(g2).encode(v.decoded) == codec.imapEncoded(g1 compose f1)(f2 compose g2).encode(v.decoded)

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
