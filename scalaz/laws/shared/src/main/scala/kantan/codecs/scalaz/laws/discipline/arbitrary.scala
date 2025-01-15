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

package kantan.codecs.scalaz.laws.discipline

import kantan.codecs.laws.CodecValue.IllegalValue
import kantan.codecs.laws.CodecValue.LegalValue
import org.scalacheck.Arbitrary
import org.scalacheck.Cogen
import scalaz.Maybe
import scalaz.\/
import scalaz.scalacheck.{ScalazArbitrary => SA}

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances extends kantan.codecs.laws.discipline.ArbitraryInstances {

  // - "Proxy" instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // These instances are already provided by scalaz, but need specific imports. We want all arbitraries to be available
  // here, so we just proxy the original instances.

  implicit def arbDisjunction[A: Arbitrary, B: Arbitrary]: Arbitrary[A \/ B] =
    SA.DisjunctionArbitrary
  implicit def cogenDisjunction[A: Cogen, B: Cogen]: Cogen[A \/ B] =
    SA.cogenDisjunction

  implicit def arbMaybe[A: Arbitrary]: Arbitrary[Maybe[A]] =
    SA.Arbitrary_Maybe[A]
  implicit def cogenMaybe[A: Cogen]: Cogen[Maybe[A]] =
    SA.cogenMaybe[A]

  // - CodecValue instances --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit def arbLegalMaybe[E, D, T](implicit
    al: Arbitrary[LegalValue[E, Option[D], T]]
  ): Arbitrary[LegalValue[E, Maybe[D], T]] =
    Arbitrary(al.arbitrary.map(_.mapDecoded(v => Maybe.fromOption(v))))

  implicit def arbIllegalMaybe[E, D, T](implicit
    al: Arbitrary[IllegalValue[E, Option[D], T]]
  ): Arbitrary[IllegalValue[E, Maybe[D], T]] =
    Arbitrary(al.arbitrary.map(_.mapDecoded(v => Maybe.fromOption(v))))

  implicit def arbLegalDisjunction[E, DL, DR, T](implicit
    a: Arbitrary[LegalValue[E, Either[DL, DR], T]]
  ): Arbitrary[LegalValue[E, DL \/ DR, T]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v => \/.fromEither(v))))

  implicit def arbIllegalDisjunction[E, DL, DR, T](implicit
    a: Arbitrary[IllegalValue[E, Either[DL, DR], T]]
  ): Arbitrary[IllegalValue[E, DL \/ DR, T]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v => \/.fromEither(v))))

}
