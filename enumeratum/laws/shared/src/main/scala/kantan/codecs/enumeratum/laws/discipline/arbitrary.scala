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

package kantan.codecs.enumeratum.laws.discipline

import enumeratum.values._
import kantan.codecs.laws.CodecValue.IllegalValue
import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.IllegalString
import kantan.codecs.laws.LegalString
import kantan.codecs.strings.StringEncoder
import kantan.codecs.strings.codecs
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.Gen

object arbitrary extends ArbitraryInstances with kantan.codecs.laws.discipline.ArbitraryInstances

trait ArbitraryInstances
    extends _root_.enumeratum.ScalacheckInstances with _root_.enumeratum.values.ScalacheckInstances {

  // - Enumerated instances --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val arbLegalEnumerated: Arbitrary[LegalString[Enumerated]] =
    Arbitrary(Gen.oneOf(Enumerated.values.map(v => LegalValue[String, Enumerated, codecs.type](v.entryName, v))))

  implicit val arbIllegalEnumerated: Arbitrary[IllegalString[Enumerated]] = {
    val legal = Enumerated.values.map(_.entryName)
    Arbitrary(arb[String].suchThat(s => !legal.contains(s)).map(s => IllegalValue(s)))
  }

  // - EnumeratedValue instances ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def arbLegalEnumeratedValue[V: StringEncoder, A <: ValueEnumEntry[V]](
    valueEnum: ValueEnum[V, A]
  ): Arbitrary[LegalString[A]] =
    Arbitrary(
      Gen.oneOf(
        valueEnum.values.map(v => LegalValue[String, A, codecs.type](StringEncoder[V].encode(v.value), v))
      )
    )

  def arbIllegalEnumeratedValue[V: StringEncoder, A <: ValueEnumEntry[V]](
    valueEnum: ValueEnum[V, A]
  ): Arbitrary[IllegalValue[String, A, codecs.type]] = {
    val legal = valueEnum.values.map(a => StringEncoder[V].encode(a.value))
    Arbitrary {
      arb[String].suchThat(s => !legal.contains(s)).map(s => IllegalValue(s))
    }
  }

  implicit val arbLegalEnumeratedInt: Arbitrary[LegalString[EnumeratedInt]] =
    arbLegalEnumeratedValue(EnumeratedInt)

  implicit val arbIllegalEnumeratedInt: Arbitrary[IllegalString[EnumeratedInt]] =
    arbIllegalEnumeratedValue(EnumeratedInt)

  implicit val arbLegalEnumeratedLong: Arbitrary[LegalString[EnumeratedLong]] =
    arbLegalEnumeratedValue(EnumeratedLong)

  implicit val arbIllegalEnumeratedLong: Arbitrary[IllegalString[EnumeratedLong]] =
    arbIllegalEnumeratedValue(EnumeratedLong)

  implicit val arbLegalEnumeratedShort: Arbitrary[LegalString[EnumeratedShort]] =
    arbLegalEnumeratedValue(EnumeratedShort)

  implicit val arbIllegalEnumeratedShort: Arbitrary[IllegalString[EnumeratedShort]] =
    arbIllegalEnumeratedValue(EnumeratedShort)

  implicit val arbLegalEnumeratedString: Arbitrary[LegalString[EnumeratedString]] =
    arbLegalEnumeratedValue(EnumeratedString)

  implicit val arbIllegalEnumeratedString: Arbitrary[IllegalString[EnumeratedString]] =
    arbIllegalEnumeratedValue(EnumeratedString)

  implicit val arbLegalEnumeratedByte: Arbitrary[LegalString[EnumeratedByte]] =
    arbLegalEnumeratedValue(EnumeratedByte)

  implicit val arbIllegalEnumeratedByte: Arbitrary[IllegalString[EnumeratedByte]] =
    arbIllegalEnumeratedValue(EnumeratedByte)

  implicit val arbLegalEnumeratedChar: Arbitrary[LegalString[EnumeratedChar]] =
    arbLegalEnumeratedValue(EnumeratedChar)

  implicit val arbIllegalEnumeratedChar: Arbitrary[IllegalString[EnumeratedChar]] =
    arbIllegalEnumeratedValue(EnumeratedChar)

}
