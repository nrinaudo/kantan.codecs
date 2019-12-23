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

import kantan.codecs.Decoder
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalacheck.Prop

trait DecoderLaws[Encoded, Decoded, Failure, Tag] {
  def decoder: Decoder[Encoded, Decoded, Failure, Tag]

  private def cmp(result: Either[Failure, Decoded], cv: CodecValue[Encoded, Decoded, Tag]): Boolean =
    (cv, result) match {
      case (IllegalValue(_), Left(_))    => true
      case (LegalValue(_, d), Right(d2)) => d == d2
      case _                             => false
    }

  // - Simple laws -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def decode(v: CodecValue[Encoded, Decoded, Tag]): Boolean = cmp(decoder.decode(v.encoded), v)

  def decodeFailure(v: IllegalValue[Encoded, Decoded, Tag]): Boolean =
    Prop.throws(classOf[Exception])(decoder.unsafeDecode(v.encoded))

  // - Functor laws ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapIdentity(v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded) == decoder.map(identity).decode(v.encoded)

  def mapComposition[A, B](v: CodecValue[Encoded, Decoded, Tag], f: Decoded => A, g: A => B): Boolean =
    decoder.map(f andThen g).decode(v.encoded) == decoder.map(f).map(g).decode(v.encoded)

  def leftMapIdentity[A](v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded) == decoder.leftMap(identity).decode(v.encoded)

  def leftMapComposition[A, B](v: CodecValue[Encoded, Decoded, Tag], f: Failure => A, g: A => B): Boolean =
    decoder.leftMap(f andThen g).decode(v.encoded) == decoder.leftMap(f).leftMap(g).decode(v.encoded)

  // - Contravariant laws ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def contramapEncodedIdentity(v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded) == decoder.contramapEncoded(identity[Encoded]).decode(v.encoded)

  def contramapEncodedComposition[A, B](b: B, f: A => Encoded, g: B => A): Boolean =
    decoder.contramapEncoded(g andThen f).decode(b) == decoder.contramapEncoded(f).contramapEncoded(g).decode(b)

  // - "Kleisli" laws --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def emapIdentity(v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded) == decoder.emap(Right.apply).decode(v.encoded)

  def emapComposition[A, B](
    v: CodecValue[Encoded, Decoded, Tag],
    f: Decoded => Either[Failure, A],
    g: A => Either[Failure, B]
  ): Boolean =
    decoder.emap(d => f(d).flatMap(g)).decode(v.encoded) == decoder.emap(f).emap(g).decode(v.encoded)

  // TODO: filter
  // TODO: collect
}

object DecoderLaws {
  implicit def apply[E, D, F, T](implicit de: Decoder[E, D, F, T]): DecoderLaws[E, D, F, T] =
    new DecoderLaws[E, D, F, T] {
      override val decoder = de
    }
}
