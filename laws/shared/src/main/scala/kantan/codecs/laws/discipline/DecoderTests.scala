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

import kantan.codecs.laws.CodecValue
import kantan.codecs.laws.CodecValue.IllegalValue
import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.DecoderLaws
import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary
import org.scalacheck.Cogen
import org.scalacheck.Prop.forAll
import org.typelevel.discipline.Laws

trait DecoderTests[E, D, F, T] extends Laws {
  def laws: DecoderLaws[E, D, F, T]

  implicit def arbLegal: Arbitrary[LegalValue[E, D, T]]

  implicit def arbF: Arbitrary[F]
  implicit val arbD: Arbitrary[D]
  implicit val arbE: Arbitrary[E]
  implicit val cogenF: Cogen[F]
  implicit val cogenD: Cogen[D]

  private def coreRules[A: Arbitrary: Cogen, B: Arbitrary: Cogen](implicit arbED: Arbitrary[CodecValue[E, D, T]]) =
    new SimpleRuleSet(
      "core",
      "decode"                       -> forAll(laws.decode _),
      "map identity"                 -> forAll(laws.mapIdentity _),
      "emap identity"                -> forAll(laws.emapIdentity _),
      "map composition"              -> forAll(laws.mapComposition[A, B] _),
      "emap composition"             -> forAll(laws.emapComposition[A, B] _),
      "contramapEncoded identity"    -> forAll(laws.contramapEncodedIdentity _),
      "contramapEncoded composition" -> forAll(laws.contramapEncodedComposition[A, B] _),
      "leftMap identity"             -> forAll(laws.leftMapIdentity _),
      "leftMap composition"          -> forAll(laws.leftMapComposition[A, B] _)
    )

  def bijectiveDecoder[A: Arbitrary: Cogen, B: Arbitrary: Cogen]: RuleSet = {
    implicit val arbValues: Arbitrary[CodecValue[E, D, T]] = Arbitrary(arbLegal.arbitrary)
    new DefaultRuleSet(
      "bijective decoder",
      Some(coreRules[A, B])
    )
  }

  def decoder[A: Arbitrary: Cogen, B: Arbitrary: Cogen](implicit ai: Arbitrary[IllegalValue[E, D, T]]): RuleSet =
    new DefaultRuleSet(
      "decoder",
      Some(coreRules[A, B]),
      "decode failure" -> forAll(laws.decodeFailure _)
    )
}

object DecoderTests {
  def apply[E: Arbitrary, D: Arbitrary: Cogen, F: Cogen: Arbitrary, T](implicit
    l: DecoderLaws[E, D, F, T],
    al: Arbitrary[LegalValue[E, D, T]]
  ): DecoderTests[E, D, F, T] =
    new DecoderTests[E, D, F, T] {
      override val laws     = l
      override val arbLegal = al
      override val arbF     = implicitly[Arbitrary[F]]
      override val cogenF   = Cogen[F]
      override val cogenD   = Cogen[D]
      override val arbD     = implicitly[Arbitrary[D]]
      override val arbE     = implicitly[Arbitrary[E]]
    }
}
