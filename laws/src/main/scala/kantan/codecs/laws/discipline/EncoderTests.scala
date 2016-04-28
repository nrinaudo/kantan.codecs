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

import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.EncoderLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

trait EncoderTests[E, D, T] extends Laws {
  def laws: EncoderLaws[E, D, T]

  implicit def arbLegal: Arbitrary[LegalValue[E, D]]
  implicit val arbD: Arbitrary[D] = Arbitrary(arbLegal.arbitrary.map(_.decoded))

  def encoder[A: Arbitrary, B: Arbitrary]: RuleSet = new SimpleRuleSet("core",
    "encode"                 → forAll(laws.encode _),
    "contramap identity"     → forAll(laws.contramapIdentity _),
    "contramap composition"  → forAll(laws.contramapComposition[A, B] _),
    "mapEncoded identity"    → forAll(laws.mapEncodedIdentity _),
    "mapEncoded composition" → forAll(laws.mapEncodedComposition[A, B] _)
  )
}

object EncoderTests {
  def apply[E, D, T](implicit l: EncoderLaws[E, D, T], al: Arbitrary[LegalValue[E, D]]): EncoderTests[E, D, T] =
    new EncoderTests[E, D, T] {
      override val laws = l
      override val arbLegal = al
    }
}