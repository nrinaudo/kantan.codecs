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

package kantan.codecs.libra.laws.discipline

import kantan.codecs.laws.CodecValue.IllegalValue
import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.IllegalString
import kantan.codecs.laws.LegalString
import kantan.codecs.strings.StringEncoder
import libra.Quantity
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.Cogen
import shapeless.HList

object arbitrary extends ArbitraryInstances with kantan.codecs.laws.discipline.ArbitraryInstances

trait ArbitraryInstances {

  implicit def arbQuantity[A: Arbitrary, D <: HList]: Arbitrary[Quantity[A, D]] =
    Arbitrary(arb[A].map(Quantity(_)))

  implicit def cogenQuantity[A: Cogen, D <: HList]: Cogen[Quantity[A, D]] =
    Cogen[A].contramap(_.value)

  implicit def arbLegalQuantity[A: Arbitrary: StringEncoder, D <: HList]: Arbitrary[LegalString[Quantity[A, D]]] =
    Arbitrary(arb[A].map(a => LegalValue(StringEncoder[A].encode(a), Quantity(a))))

  implicit def arbIllegalQuantity[A, D <: HList](implicit
    ai: Arbitrary[IllegalString[A]]
  ): Arbitrary[IllegalString[Quantity[A, D]]] =
    Arbitrary(ai.arbitrary.map(i => IllegalValue(i.encoded)))

}
