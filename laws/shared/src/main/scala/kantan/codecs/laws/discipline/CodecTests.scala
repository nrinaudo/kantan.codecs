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

package kantan.codecs.laws.discipline

import imp.imp
import kantan.codecs.Codec
import kantan.codecs.laws.{CodecLaws, CodecValue}
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalacheck.{Arbitrary, Cogen, Shrink}
import org.scalacheck.Prop.forAll

trait CodecTests[Encoded, Decoded, Failure, Tag]
    extends DecoderTests[Encoded, Decoded, Failure, Tag] with EncoderTests[Encoded, Decoded, Tag] {
  def laws: CodecLaws[Encoded, Decoded, Failure, Tag]

  private def coreRules[A: Arbitrary: Cogen: Shrink, B: Arbitrary: Cogen: Shrink](
    implicit av: Arbitrary[CodecValue[Encoded, Decoded, Tag]],
    shrinkFoo: Shrink[IllegalValue[Encoded, Decoded, Tag]],
    shrinkBar: Shrink[LegalValue[Encoded, Decoded, Tag]]
  ): RuleSet =
    new DefaultRuleSet(
      "round trip",
      Some(encoder[A, B]),
      "round trip (encoding)"              -> forAll(laws.roundTripEncoding _),
      "round trip (decoding)"              -> forAll(laws.roundTripDecoding _),
      "leftMap identity(encoding)"         -> forAll(laws.leftMapIdentityEncoding _),
      "leftMap composition(decoding)"      -> forAll(laws.leftMapCompositionEncoding[A, B] _),
      "imap identity (encoding)"           -> forAll(laws.imapIdentityEncoding _),
      "imap identity (decoding)"           -> forAll(laws.imapIdentityDecoding _),
      "imap composition (encoding)"        -> forAll(laws.imapCompositionEncoding[A, B] _),
      "imap composition (decoding)"        -> forAll(laws.imapCompositionDecoding[A, B] _),
      "imapEncoded identity (encoding)"    -> forAll(laws.imapEncodedIdentityEncoding _),
      "imapEncoded identity (decoding)"    -> forAll(laws.imapEncodedIdentityDecoding _),
      "imapEncoded composition (encoding)" -> forAll(laws.imapEncodedCompositionEncoding[A, B] _),
      "imapEncoded composition(decoding)"  -> forAll(laws.imapEncodedCompositionDecoding[A, B] _)
    )

  def bijectiveCodec[A: Arbitrary: Cogen: Shrink, B: Arbitrary: Cogen: Shrink]: RuleSet = new RuleSet {
    implicit val arbValues: Arbitrary[CodecValue[Encoded, Decoded, Tag]] = Arbitrary(arbLegal.arbitrary)

    val name    = "bijective codec"
    val bases   = Nil
    val parents = Seq(coreRules[A, B], bijectiveDecoder[A, B])
    val props   = Seq.empty
  }

  def codec[A: Arbitrary: Cogen: Shrink, B: Arbitrary: Cogen: Shrink](
    implicit ai: Arbitrary[IllegalValue[Encoded, Decoded, Tag]],
    shrinkFoo: Shrink[IllegalValue[Encoded, Decoded, Tag]],
    shrinkBar: Shrink[LegalValue[Encoded, Decoded, Tag]]
  ): RuleSet =
    new RuleSet {
      val name    = "codec"
      val bases   = Nil
      val parents = Seq(coreRules[A, B], decoder[A, B])
      val props   = Seq.empty
    }
}

object CodecTests {
  def apply[E: Arbitrary: Cogen, D: Arbitrary: Cogen: Shrink, F: Cogen: Arbitrary, T](
    implicit c: Codec[E, D, F, T],
    al: Arbitrary[LegalValue[E, D, T]]
  ): CodecTests[E, D, F, T] =
    new CodecTests[E, D, F, T] {
      override val laws     = CodecLaws[E, D, F, T]
      override val arbLegal = al
      override val arbF     = imp[Arbitrary[F]]
      override val cogenF   = Cogen[F]
      override val cogenD   = Cogen[D]
      override val cogenE   = Cogen[E]
      override val arbD     = imp[Arbitrary[D]]
      override val arbE     = imp[Arbitrary[E]]
      override val shrinkD  = imp[Shrink[D]]
    }
}
