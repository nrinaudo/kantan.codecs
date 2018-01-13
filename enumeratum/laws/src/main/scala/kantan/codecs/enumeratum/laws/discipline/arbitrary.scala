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

package kantan.codecs
package enumeratum
package laws
package discipline

import _root_.enumeratum.values._
import kantan.codecs.laws._, CodecValue._
import org.scalacheck.{Arbitrary, Cogen, Gen}, Arbitrary.{arbitrary ⇒ arb}
import strings._

object arbitrary extends ArbitraryInstances with kantan.codecs.laws.discipline.ArbitraryInstances

trait ArbitraryInstances {

  // - Enumerated instances --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val arbEnumerated: Arbitrary[Enumerated] = Arbitrary(Gen.oneOf(Enumerated.values))

  implicit val cogenEnumerated: Cogen[Enumerated] = Cogen[String].contramap(_.entryName)

  implicit val arbLegalEnumerated: Arbitrary[LegalString[Enumerated]] =
    Arbitrary(Gen.oneOf(Enumerated.values.map(v ⇒ LegalValue[String, Enumerated, codecs.type](v.entryName, v))))

  implicit val arbIllegalEnumerated: Arbitrary[IllegalString[Enumerated]] = {
    val legal = Enumerated.values.map(_.entryName)
    Arbitrary(arb[String].suchThat(s ⇒ !legal.contains(s)).map(s ⇒ IllegalValue(s)))
  }

  // - Generic EnumeratedValue instances -------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  def arbEnumeratedValue[V, A <: ValueEnumEntry[V]](enum: ValueEnum[V, A]): Arbitrary[A] =
    Arbitrary(Gen.oneOf(enum.values))

  def cogenEnumeratedValue[V: Cogen, A <: ValueEnumEntry[V]]: Cogen[A] = Cogen[V].contramap(_.value)

  def arbLegalEnumeratedValue[V: StringEncoder, A <: ValueEnumEntry[V]](
    enum: ValueEnum[V, A]
  ): Arbitrary[LegalString[A]] =
    Arbitrary(
      Gen.oneOf(
        enum.values.map(v ⇒ LegalValue[String, A, codecs.type](StringEncoder[V].encode(v.value), v))
      )
    )

  def arbIllegalEnumeratedValue[V: StringEncoder, A <: ValueEnumEntry[V]](
    enum: ValueEnum[V, A]
  ): Arbitrary[IllegalValue[String, A, codecs.type]] = {
    val legal = enum.values.map(a ⇒ StringEncoder[V].encode(a.value))
    Arbitrary {
      arb[String].suchThat(s ⇒ !legal.contains(s)).map(s ⇒ IllegalValue(s))
    }
  }

  // - EnumeratedInt instances -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val arbEnumeratedInt: Arbitrary[EnumeratedInt] = arbEnumeratedValue(EnumeratedInt)

  implicit val cogenEnumeratedInt: Cogen[EnumeratedInt] = cogenEnumeratedValue[Int, EnumeratedInt]

  implicit val arbLegalEnumeratedInt: Arbitrary[LegalString[EnumeratedInt]] =
    arbLegalEnumeratedValue(EnumeratedInt)

  implicit val arbIllegalEnumeratedInt: Arbitrary[IllegalString[EnumeratedInt]] =
    arbIllegalEnumeratedValue(EnumeratedInt)

  // - EnumeratedLong instances ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val arbEnumeratedLong: Arbitrary[EnumeratedLong] = arbEnumeratedValue(EnumeratedLong)

  implicit val cogenEnumeratedLong: Cogen[EnumeratedLong] = cogenEnumeratedValue[Long, EnumeratedLong]

  implicit val arbLegalEnumeratedLong: Arbitrary[LegalString[EnumeratedLong]] =
    arbLegalEnumeratedValue(EnumeratedLong)

  implicit val arbIllegalEnumeratedLong: Arbitrary[IllegalString[EnumeratedLong]] =
    arbIllegalEnumeratedValue(EnumeratedLong)

  // - EnumeratedShort instances ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val arbEnumeratedShort: Arbitrary[EnumeratedShort] = arbEnumeratedValue(EnumeratedShort)

  implicit val cogenEnumeratedShort: Cogen[EnumeratedShort] = cogenEnumeratedValue[Short, EnumeratedShort]

  implicit val arbLegalEnumeratedShort: Arbitrary[LegalString[EnumeratedShort]] =
    arbLegalEnumeratedValue(EnumeratedShort)

  implicit val arbIllegalEnumeratedShort: Arbitrary[IllegalString[EnumeratedShort]] =
    arbIllegalEnumeratedValue(EnumeratedShort)

  // - EnumeratedString instances --------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val arbEnumeratedString: Arbitrary[EnumeratedString] = arbEnumeratedValue(EnumeratedString)

  implicit val cogenEnumeratedString: Cogen[EnumeratedString] = cogenEnumeratedValue[String, EnumeratedString]

  implicit val arbLegalEnumeratedString: Arbitrary[LegalString[EnumeratedString]] =
    arbLegalEnumeratedValue(EnumeratedString)

  implicit val arbIllegalEnumeratedString: Arbitrary[IllegalString[EnumeratedString]] =
    arbIllegalEnumeratedValue(EnumeratedString)

  // - EnumeratedByte instances ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val arbEnumeratedByte: Arbitrary[EnumeratedByte] = arbEnumeratedValue(EnumeratedByte)

  implicit val cogenEnumeratedByte: Cogen[EnumeratedByte] = cogenEnumeratedValue[Byte, EnumeratedByte]

  implicit val arbLegalEnumeratedByte: Arbitrary[LegalString[EnumeratedByte]] =
    arbLegalEnumeratedValue(EnumeratedByte)

  implicit val arbIllegalEnumeratedByte: Arbitrary[IllegalString[EnumeratedByte]] =
    arbIllegalEnumeratedValue(EnumeratedByte)

  // - EnumeratedChar instances ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val arbEnumeratedChar: Arbitrary[EnumeratedChar] = arbEnumeratedValue(EnumeratedChar)

  implicit val cogenEnumeratedChar: Cogen[EnumeratedChar] = cogenEnumeratedValue[Char, EnumeratedChar]

  implicit val arbLegalEnumeratedChar: Arbitrary[LegalString[EnumeratedChar]] =
    arbLegalEnumeratedValue(EnumeratedChar)

  implicit val arbIllegalEnumeratedChar: Arbitrary[IllegalString[EnumeratedChar]] =
    arbIllegalEnumeratedValue(EnumeratedChar)

}
