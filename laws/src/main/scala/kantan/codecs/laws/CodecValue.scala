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

package kantan.codecs.laws

import kantan.codecs.{Decoder, Encoder}
import org.scalacheck.{Arbitrary, Gen}

// TODO: investigate what type variance annotations can be usefully applied to CodecValue.
sealed abstract class CodecValue[E, D, T] extends Product with Serializable {
  def encoded: E
  def mapEncoded[EE](f: E ⇒ EE): CodecValue[EE, D, T]
  def mapDecoded[DD](f: D ⇒ DD): CodecValue[E, DD, T]
  def tag[TT]: CodecValue[E, D, TT]
}

object CodecValue {
  final case class LegalValue[E, D, T](encoded: E, decoded: D) extends CodecValue[E, D, T] {
    override def mapDecoded[DD](f: D => DD) = copy(decoded = f(decoded))
    override def mapEncoded[EE](f: E ⇒ EE) = copy(encoded = f(encoded))
    override def tag[TT] = this.asInstanceOf[LegalValue[E, D, TT]]
  }
  final case class IllegalValue[E, D, T](encoded: E) extends CodecValue[E, D, T] {
    override def mapDecoded[DD](f: D => DD) = IllegalValue(encoded)
    override def mapEncoded[EE](f: E => EE) = copy(encoded = f(encoded))
    override def tag[TT] = this.asInstanceOf[IllegalValue[E, D, TT]]
  }



  // - Helpers / bug workarounds ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbValue[E, D, T](implicit arbL: Arbitrary[LegalValue[E, D, T]], arbI: Arbitrary[IllegalValue[E, D, T]])
  : Arbitrary[CodecValue[E, D, T]] =
  Arbitrary(Gen.oneOf(arbL.arbitrary, arbI.arbitrary))


  // - Derived instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbLegalValueFromEnc[E, A: Arbitrary, T](implicit ea: Encoder[E, A, T]): Arbitrary[LegalValue[E, A, T]] =
    arbLegalValue(ea.encode)

  implicit def arbIllegalValueFromDec[E: Arbitrary, A, F, T](implicit da: Decoder[E, A, F, T])
  : Arbitrary[IllegalValue[E, A, T]] =
    arbIllegalValue(e ⇒ da.decode(e).isFailure)


  def arbLegalValue[E, A, T](encode: A ⇒ E)(implicit arbA: Arbitrary[A]): Arbitrary[LegalValue[E, A, T]] = Arbitrary {
    arbA.arbitrary.map(a ⇒ LegalValue(encode(a), a))
  }

  def arbIllegalValue[E, A, T](illegal: E ⇒ Boolean)(implicit arbE: Arbitrary[E]): Arbitrary[IllegalValue[E, A, T]] =
    Arbitrary {
      arbE.arbitrary.suchThat(illegal).map(e ⇒ IllegalValue(e))
    }
}
