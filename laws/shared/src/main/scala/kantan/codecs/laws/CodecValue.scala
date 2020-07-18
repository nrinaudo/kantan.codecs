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

import imp.imp
import org.scalacheck.{Arbitrary, Gen, Shrink}

/** Represents possible encoded and decoded values.
  *
  * There are two main categories of values:
  * - legal ones: an encoded / decoded couple, where decoding one should yield the other and vice versa.
  * - illegal ones: an encoded value that cannot be decoded.
  *
  * The purpose of this type is to test encoder, decoder and codec laws - which means we'll almost always need
  * `Arbitrary` instances for them. These can be tedious to write, but that tedium can be alleviated somewhat through
  * the [[HasIllegalValues]] and [[HasLegalValues]] type classes.
  */
sealed abstract class CodecValue[Encoded, Decoded, Tag] extends Product with Serializable {
  def encoded: Encoded
  def mapEncoded[E](f: Encoded => E): CodecValue[E, Decoded, Tag]
  def mapDecoded[D](f: Decoded => D): CodecValue[Encoded, D, Tag]
  def isLegal: Boolean
  def isIllegal: Boolean = !isLegal
  def tag[T]: CodecValue[Encoded, Decoded, T]
}

object CodecValue {

  /** Represents a legal value: one that can be safely encoded and decoded. */
  final case class LegalValue[Encoded, Decoded, Tag](encoded: Encoded, decoded: Decoded)
      extends CodecValue[Encoded, Decoded, Tag] {

    override def mapDecoded[D](f: Decoded => D) = LegalValue(encoded, f(decoded))
    override def mapEncoded[E](f: Encoded => E) = LegalValue(f(encoded), decoded)
    override val isLegal                        = true
    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    override def tag[T]: LegalValue[Encoded, Decoded, T] = this.asInstanceOf[LegalValue[Encoded, Decoded, T]]
  }

  /** Represents an illegal value: one that cannot be decoded. */
  final case class IllegalValue[Encoded, Decoded, Tag](encoded: Encoded) extends CodecValue[Encoded, Decoded, Tag] {
    override def mapDecoded[D](f: Decoded => D) = IllegalValue(encoded)
    override def mapEncoded[E](f: Encoded => E) = IllegalValue(f(encoded))
    override val isLegal                        = false
    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    override def tag[T]: IllegalValue[Encoded, Decoded, T] = this.asInstanceOf[IllegalValue[Encoded, Decoded, T]]
  }

  // - Arbitrary instances ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbValue[E, D, T](
    implicit arbLegal: Arbitrary[LegalValue[E, D, T]],
    arbIllegal: Arbitrary[IllegalValue[E, D, T]]
  ): Arbitrary[CodecValue[E, D, T]] =
    Arbitrary(Gen.oneOf(arbLegal.arbitrary, arbIllegal.arbitrary))

  implicit def arbLegalValue[E, D: Arbitrary, T](
    implicit alv: HasLegalValues[E, D, T]
  ): Arbitrary[LegalValue[E, D, T]] =
    Arbitrary(imp[Arbitrary[D]].arbitrary.map(alv.asLegalValue))

  implicit def arbIllegalValue[E: Arbitrary, D, T](
    implicit aiv: HasIllegalValues[E, D, T]
  ): Arbitrary[IllegalValue[E, D, T]] =
    Arbitrary(imp[Arbitrary[E]].arbitrary.map(aiv.asIllegalValue))

  // - Shrink instances ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit def shrinkCodecValue[E, D, T](
    implicit shrinkLegal: Shrink[LegalValue[E, D, T]],
    shrinkIllegal: Shrink[IllegalValue[E, D, T]]
  ): Shrink[CodecValue[E, D, T]] =
    Shrink {
      case legal @ LegalValue(_, _)  => shrinkLegal.shrink(legal)
      case illegal @ IllegalValue(_) => shrinkIllegal.shrink(illegal)
    }

  implicit def shrinkLegalValue[E, D: Shrink, T](implicit alv: HasLegalValues[E, D, T]): Shrink[LegalValue[E, D, T]] =
    Shrink { value =>
      imp[Shrink[D]]
        .shrink(value.decoded)
        .map(alv.asLegalValue)
    }

  implicit def shrinkIllegalValue[E: Shrink, D, T](
    implicit aiv: HasIllegalValues[E, D, T]
  ): Shrink[IllegalValue[E, D, T]] =
    Shrink { value =>
      imp[Shrink[E]]
        .shrink(value.encoded)
        .filter(aiv.isInvalid)
        .map(IllegalValue.apply[E, D, T])
    }

}
