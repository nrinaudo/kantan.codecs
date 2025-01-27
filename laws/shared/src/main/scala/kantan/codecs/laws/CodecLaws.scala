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

import kantan.codecs.Codec
import kantan.codecs.Decoder
import kantan.codecs.Encoder
import kantan.codecs.laws.CodecValue.LegalValue

trait CodecLaws[E, D, F, T] extends DecoderLaws[E, D, F, T] with EncoderLaws[E, D, T] {
  implicit lazy val codec: Codec[E, D, F, T] = Codec.from(decoder.decode _)(encoder.encode _)

  // - Misc. laws ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def leftMapIdentityEncoding[A](d: D): Boolean =
    codec.encode(d) == codec.leftMap(identity).encode(d)

  def leftMapCompositionEncoding[A, B](d: D, f: F => A, g: A => B): Boolean =
    codec.leftMap(f.andThen(g)).encode(d) == codec.leftMap(f).leftMap(g).encode(d)

  // - Invariant functor laws ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def imapIdentityEncoding(v: CodecValue[E, D, T]): Boolean =
    codec.imap(identity[D])(identity[D]).decode(v.encoded) == codec.decode(v.encoded)

  def imapIdentityDecoding(d: D): Boolean =
    codec.imap(identity[D])(identity[D]).encode(d) == codec.encode(d)

  def imapCompositionEncoding[A, B](b: B, f1: D => A, f2: A => D, g1: A => B, g2: B => A): Boolean =
    codec.imap(f1)(f2).imap(g1)(g2).encode(b) == codec.imap(g1.compose(f1))(f2.compose(g2)).encode(b)

  def imapCompositionDecoding[A, B](v: CodecValue[E, D, T], f1: D => A, f2: A => D, g1: A => B, g2: B => A): Boolean =
    codec.imap(f1)(f2).imap(g1)(g2).decode(v.encoded) == codec.imap(g1.compose(f1))(f2.compose(g2)).decode(v.encoded)

  def imapEncodedIdentityEncoding(d: D): Boolean =
    codec.imapEncoded(identity[E])(identity[E]).encode(d) == codec.encode(d)

  def imapEncodedIdentityDecoding(v: CodecValue[E, D, T]): Boolean =
    codec.imapEncoded(identity[E])(identity[E]).decode(v.encoded) == codec.decode(v.encoded)

  def imapEncodedCompositionEncoding[A, B](d: D, f1: E => A, f2: A => E, g1: A => B, g2: B => A): Boolean =
    codec.imapEncoded(f1)(f2).imapEncoded(g1)(g2).encode(d) == codec
      .imapEncoded(g1.compose(f1))(f2.compose(g2))
      .encode(d)

  def imapEncodedCompositionDecoding[A, B](b: B, f1: E => A, f2: A => E, g1: A => B, g2: B => A): Boolean =
    codec.imapEncoded(f1)(f2).imapEncoded(g1)(g2).decode(b) == codec
      .imapEncoded(g1.compose(f1))(f2.compose(g2))
      .decode(b)

  // - Round trip laws -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def roundTripEncoding(d: D): Boolean =
    decoder.decode(encoder.encode(d)) == Right(d)
  def roundTripDecoding(v: LegalValue[E, D, T]): Boolean =
    decoder.decode(v.encoded).map(encoder.encode) == Right(v.encoded)
}

object CodecLaws {
  implicit def apply[E, D, F, T](implicit de: Decoder[E, D, F, T], ee: Encoder[E, D, T]): CodecLaws[E, D, F, T] =
    new CodecLaws[E, D, F, T] {
      override val encoder = ee
      override val decoder = de
    }
}
