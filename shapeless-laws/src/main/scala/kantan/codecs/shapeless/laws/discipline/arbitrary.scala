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

import kantan.codecs.shapeless.laws._
import org.scalacheck.{Arbitrary, Gen}
import shapeless._

object arbitrary extends kantan.codecs.laws.discipline.ArbitraryInstances with ArbitraryInstances {
  // Arbitrary instances for Or. This appears to be required for Scala 2.10, and makes compilation faster.
  implicit def arbLeft[A](implicit aa: Arbitrary[A]): Arbitrary[Left[A]] = Arbitrary(aa.arbitrary.map(Left.apply))
  implicit def arbRight[A](implicit aa: Arbitrary[A]): Arbitrary[Right[A]] = Arbitrary(aa.arbitrary.map(Right.apply))
  implicit def arbOr[A: Arbitrary, B: Arbitrary]: Arbitrary[A Or B] =
    Arbitrary(Gen.oneOf(arbLeft[A].arbitrary, arbRight[B].arbitrary))
}

trait ArbitraryInstances extends LowPriorityArbitraryInstances {
  // - Arbitrary coproducts --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbSumType[H, T <: Coproduct](implicit gen: Generic.Aux[H, T], eh: Lazy[Arbitrary[T]])
  : DerivedArbitrary[H] =
  DerivedArbitrary(eh.value.arbitrary.map(gen.from))

  implicit def arbCoproduct[H, T <: Coproduct](implicit ah: Arbitrary[H], at: Arbitrary[T]): Arbitrary[H :+: T] =
    Arbitrary(Gen.oneOf(ah.arbitrary.map(Inl.apply), at.arbitrary.map(Inr.apply)))

  implicit val arbCNil: Arbitrary[CNil] = Arbitrary(Gen.fail[CNil])



  // - Arbitrary hlists ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbCaseClass[H, T <: HList](implicit gen: Generic.Aux[H, T], at: Lazy[Arbitrary[T]])
  : DerivedArbitrary[H] =
  DerivedArbitrary(at.value.arbitrary.map(gen.from))

  implicit def arbHList[H, T <: HList](implicit ah: Arbitrary[H], at: Arbitrary[T]): Arbitrary[H :: T] = Arbitrary {
    for {
      h ← ah.arbitrary
      t ← at.arbitrary
    } yield h :: t
  }

  implicit val arbHNil: Arbitrary[HNil] = Arbitrary(Gen.const(HNil))
}

trait LowPriorityArbitraryInstances {
  implicit def fromExported[A](implicit aa: DerivedArbitrary[A]): Arbitrary[A] = aa.value
}
