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

package kantan.codecs.shapeless.laws.discipline

import org.scalacheck.{Arbitrary, Gen}
import shapeless._

object arbitrary {
  // - Arbitrary coproducts --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbSumType[H, T <: Coproduct](implicit gen: Generic.Aux[H, T], eh: Lazy[Arbitrary[T]]): Arbitrary[H] =
    Arbitrary(eh.value.arbitrary.map(gen.from))

  implicit def arbCoproduct[H, T <: Coproduct](implicit ah: Arbitrary[H], at: Arbitrary[T]): Arbitrary[H :+: T] =
    Arbitrary(Gen.oneOf(ah.arbitrary.map(Inl.apply), at.arbitrary.map(Inr.apply)))

  implicit val arbCNil: Arbitrary[CNil] = Arbitrary(Gen.fail[CNil])



  // - Arbitrary hlists ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbCaseClass[H, T <: HList](implicit gen: Generic.Aux[H, T], at: Lazy[Arbitrary[T]]): Arbitrary[H] =
    Arbitrary(at.value.arbitrary.map(gen.from))

  implicit def arbHList[H, T <: HList](implicit ah: Arbitrary[H], at: Arbitrary[T]): Arbitrary[H :: T] = Arbitrary {
    for {
      h ← ah.arbitrary
      t ← at.arbitrary
    } yield h :: t
  }

  implicit val arbHNil: Arbitrary[HNil] = Arbitrary(Gen.const(HNil))



  // - Coproduct gen codec ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /*
  implicit def genSumType[E, H: Arbitrary, T <: Coproduct]
  (implicit gen: Generic.Aux[H, T], eh: Lazy[GenCodecValue[E, T]]): GenCodecValue[E, H] =
    eh.value.contramap(gen.to)

  implicit def genCoproduct[E: Arbitrary, H: Arbitrary, T <: Coproduct: Arbitrary]
  (implicit gh: GenCodecValue[E, H], gt: GenCodecValue[E, T]): GenCodecValue[E, H :+: T] =
    GenCodecValue[E, H :+: T](_.eliminate(gh.encode, gt.encode))(e ⇒ gh.isIllegal(e) && gt.isIllegal(e))

  implicit def genCnil[E: Arbitrary]: GenCodecValue[E, CNil] =
    GenCodecValue[E, CNil](_ ⇒ sys.error("Trying to encode CNil"))(_ ⇒ true)
    */
}
