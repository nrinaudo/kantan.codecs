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

package kantan.codecs.strings.java8

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.strings.codecs
import org.scalacheck.Arbitrary

object codec {
  implicit def arbLegalValue[D](
    implicit arb: Arbitrary[LegalValue[String, D, codecs.type]]
  ): Arbitrary[LegalValue[String, D, codec.type]] =
    Arbitrary(arb.arbitrary.map(_.tag[codec.type]))

  implicit def arbIllegalValue[D](
    implicit arb: Arbitrary[IllegalValue[String, D, codecs.type]]
  ): Arbitrary[IllegalValue[String, D, codec.type]] =
    Arbitrary(arb.arbitrary.map(_.tag[codec.type]))
}
