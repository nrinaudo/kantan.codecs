/*
 * Copyright 2016 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.codecs.laws

import kantan.codecs.{Codec, Decoder, Encoder}
import kantan.codecs.laws.CodecValue.LegalValue

trait CodecLaws[Encoded, Decoded, Failure, Tag]
    extends DecoderLaws[Encoded, Decoded, Failure, Tag] with EncoderLaws[Encoded, Decoded, Tag] {
  lazy val codec: Codec[Encoded, Decoded, Failure, Tag] = Codec.from(decoder.decode _)(encoder.encode _)

  // - Misc. laws ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def leftMapIdentityEncoding[A](d: Decoded): Boolean =
    codec.encode(d) == codec.leftMap(identity).encode(d)

  def leftMapCompositionEncoding[A, B](d: Decoded, f: Failure => A, g: A => B): Boolean =
    codec.leftMap(f andThen g).encode(d) == codec.leftMap(f).leftMap(g).encode(d)

  // - Invariant functor laws ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def imapIdentityEncoding(v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    codec.imap(identity[Decoded])(identity[Decoded]).decode(v.encoded) == codec.decode(v.encoded)

  def imapIdentityDecoding(d: Decoded): Boolean =
    codec.imap(identity[Decoded])(identity[Decoded]).encode(d) == codec.encode(d)

  def imapCompositionEncoding[A, B](b: B, f1: Decoded => A, f2: A => Decoded, g1: A => B, g2: B => A): Boolean =
    codec.imap(f1)(f2).imap(g1)(g2).encode(b) == codec.imap(g1 compose f1)(f2 compose g2).encode(b)

  def imapCompositionDecoding[A, B](
    v: CodecValue[Encoded, Decoded, Tag],
    f1: Decoded => A,
    f2: A => Decoded,
    g1: A => B,
    g2: B => A
  ): Boolean =
    codec.imap(f1)(f2).imap(g1)(g2).decode(v.encoded) == codec.imap(g1 compose f1)(f2 compose g2).decode(v.encoded)

  def imapEncodedIdentityEncoding(d: Decoded): Boolean =
    codec.imapEncoded(identity[Encoded])(identity[Encoded]).encode(d) == codec.encode(d)

  def imapEncodedIdentityDecoding(v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    codec.imapEncoded(identity[Encoded])(identity[Encoded]).decode(v.encoded) == codec.decode(v.encoded)

  def imapEncodedCompositionEncoding[A, B](
    d: Decoded,
    f1: Encoded => A,
    f2: A => Encoded,
    g1: A => B,
    g2: B => A
  ): Boolean =
    codec.imapEncoded(f1)(f2).imapEncoded(g1)(g2).encode(d) == codec.imapEncoded(g1 compose f1)(f2 compose g2).encode(d)

  def imapEncodedCompositionDecoding[A, B](b: B, f1: Encoded => A, f2: A => Encoded, g1: A => B, g2: B => A): Boolean =
    codec.imapEncoded(f1)(f2).imapEncoded(g1)(g2).decode(b) == codec.imapEncoded(g1 compose f1)(f2 compose g2).decode(b)

  // - Round trip laws -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def roundTripEncoding(d: Decoded): Boolean = decoder.decode(encoder.encode(d)) == Right(d)
  def roundTripDecoding(v: LegalValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded).map(encoder.encode) == Right(v.encoded)
}

object CodecLaws {
  def apply[E, D, F, T](implicit de: Decoder[E, D, F, T], ee: Encoder[E, D, T]): CodecLaws[E, D, F, T] =
    new CodecLaws[E, D, F, T] {
      override val encoder = ee
      override val decoder = de
    }
}
