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

package kantan.codecs.shapeless.laws

import org.scalacheck.{Arbitrary, Gen}

sealed trait Or[+A, +B]
case class Left[A](a: A) extends Or[A, Nothing]
case class Right[B](b: B) extends Or[Nothing, B]

object Or {
  implicit def arb[A, B](implicit aa: Arbitrary[A], ab: Arbitrary[B]): Arbitrary[A Or B] =
    Arbitrary(Gen.oneOf(aa.arbitrary.map(Left.apply), ab.arbitrary.map(Right.apply)))
}
