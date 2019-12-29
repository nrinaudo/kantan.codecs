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

/** Laws that a [[Decoder]] must abide by to be considered lawful.
  *
  * These are executed through [[kantan.codecs.discipline.DecoderTests]].
  */
trait DecoderLaws[Encoded, Decoded, Failure, Tag] {

  /** Decoder whose lawfulness is being checked. */
  def decoder: Decoder[Encoded, Decoded, Failure, Tag]

  // - Simple laws -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // These two laws are the foundation on which the others are built: if we can trust that `decode` is well behaved,
  // then relying on it in all other laws is sane.

  /** Decoding a legal value must yield the expected result. */
  def decode(v: LegalValue[Encoded, Decoded, Tag]): Boolean = decoder.decode(v.encoded) == Right(v.decoded)

  /** Decoding an illegal value must fail. */
  def decodeFailure(v: IllegalValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded).isLeft

  // - Functor laws ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Mapping on `identity` should not change the decoder's behaviour. */
  def mapIdentity(v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded) == decoder.map(identity).decode(v.encoded)

  /** Mapping on the composition of two functions must be the same as mapping on the first, then the second. */
  def mapComposition[A, B](v: CodecValue[Encoded, Decoded, Tag], f: Decoded => A, g: A => B): Boolean =
    decoder.map(f andThen g).decode(v.encoded) == decoder.map(f).map(g).decode(v.encoded)

  // - Bifunctor laws --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Same as [[mapIdentity]], but for `leftMap`. */
  def leftMapIdentity[A](v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded) == decoder.leftMap(identity).decode(v.encoded)

  /** Same as [[mapComposition]], but for `leftMap`. */
  def leftMapComposition[A, B](v: CodecValue[Encoded, Decoded, Tag], f: Failure => A, g: A => B): Boolean =
    decoder.leftMap(f andThen g).decode(v.encoded) == decoder.leftMap(f).leftMap(g).decode(v.encoded)

  // - Contravariant laws ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Same as [[mapIdentity]], but for `contramapEncoded`. */
  def contramapEncodedIdentity(v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded) == decoder.contramapEncoded(identity[Encoded]).decode(v.encoded)

  /** Same as [[mapComposition]], but for `contramapEncoded`. */
  def contramapEncodedComposition[A, B](b: B, f: A => Encoded, g: B => A): Boolean =
    decoder.contramapEncoded(g andThen f).decode(b) == decoder.contramapEncoded(f).contramapEncoded(g).decode(b)

  // - "Kleisli" laws --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Same as [[mapIdentity]], but for `emap`. */
  def emapIdentity(v: CodecValue[Encoded, Decoded, Tag]): Boolean =
    decoder.decode(v.encoded) == decoder.emap(Right.apply).decode(v.encoded)

  /** Same as [[mapComposition]], but for `emap`. */
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

  /** Creates an instance of [[DecoderLaws]] for any type that has a valid decoder instance.
    *
    * There rarely is a need to call this directly. Consider using [[kantan.codecs.discipline.DecoderTests]] instead.
    */
  def apply[E, D, F, T](implicit de: Decoder[E, D, F, T]): DecoderLaws[E, D, F, T] =
    new DecoderLaws[E, D, F, T] {
      override val decoder = de
    }
}
