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

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalacheck.Arbitrary
import scalaz.{\/, Maybe}

object arbitrary extends ArbitraryInstances

class ArbitraryInstances {
  implicit def arbLegalMaybe[E, D]
  (implicit al: Arbitrary[LegalValue[E, Option[D]]]): Arbitrary[LegalValue[E, Maybe[D]]] =
    Arbitrary(al.arbitrary.map(_.mapDecoded(v ⇒ Maybe.fromOption(v))))

  implicit def arbIllegalMaybe[E, D]
  (implicit al: Arbitrary[IllegalValue[E, Option[D]]]): Arbitrary[IllegalValue[E, Maybe[D]]] =
      Arbitrary(al.arbitrary.map(_.mapDecoded(v ⇒ Maybe.fromOption(v))))

  implicit def arbLegalDisjunction[E, DL, DR]
  (implicit a: Arbitrary[LegalValue[E, Either[DL, DR]]]): Arbitrary[LegalValue[E, DL \/ DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ \/.fromEither(v))))

  implicit def arbIllegalDisjunction[E, DL, DR]
  (implicit a: Arbitrary[IllegalValue[E, Either[DL, DR]]]): Arbitrary[IllegalValue[E, DL \/ DR]] =
      Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ \/.fromEither(v))))
}
