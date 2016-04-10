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

package kantan.codecs.cats.laws.discipline

import cats.data.Xor
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalacheck.Arbitrary

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances {
  implicit def arbLegalXor[E, DL, DR]
  (implicit a: Arbitrary[LegalValue[E, Either[DL, DR]]]): Arbitrary[LegalValue[E, DL Xor DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ Xor.fromEither(v))))

  implicit def arbIllegalXor[E, DL, DR]
  (implicit a: Arbitrary[IllegalValue[E, Either[DL, DR]]]): Arbitrary[IllegalValue[E, DL Xor DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ Xor.fromEither(v))))
}
