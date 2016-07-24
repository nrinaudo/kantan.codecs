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
sealed abstract class CodecValue[E, D, T] {
  def encoded: E
  def mapEncoded[EE](f: E ⇒ EE): CodecValue[EE, D, T]
  def mapDecoded[DD](f: D ⇒ DD): CodecValue[E, DD, T]
  def tag[TT]: CodecValue[E, D, TT]
}

object CodecValue {
  final class LegalValue[E, D, T](val encoded: E, val decoded: D) extends CodecValue[E, D, T] {
    override def mapDecoded[DD](f: D => DD) = LegalValue(encoded, f(decoded))
    override def mapEncoded[EE](f: E ⇒ EE) = LegalValue(f(encoded), decoded)
    override def tag[TT] = this.asInstanceOf[LegalValue[E, D, TT]]

    override def equals(obj: Any) = obj match {
      case LegalValue(e, d) ⇒ e == encoded && d == decoded
      case _                ⇒ false
    }

    override def toString: String = s"LegalValue($encoded,$decoded)"
  }

  object LegalValue {
    def apply[E, D, T](e: E, d: D): LegalValue[E, D, T] = new LegalValue(e, d)
    def unapply[E, D, T](v: LegalValue[E, D, T]): Option[(E, D)] = Some((v.encoded, v.decoded))
  }

  final class IllegalValue[E, D, T](val encoded: E) extends CodecValue[E, D, T] {
    override def mapDecoded[DD](f: D => DD) = IllegalValue(encoded)
    override def mapEncoded[EE](f: E => EE) = IllegalValue(f(encoded))
    override def tag[TT] = this.asInstanceOf[IllegalValue[E, D, TT]]

    override def equals(obj: Any) = obj match {
      case IllegalValue(e) ⇒ e == encoded
      case _               ⇒ false
    }

    override def toString: String = s"IllegalValue($encoded)"
  }

  object IllegalValue {
    def apply[E, D, T](e: E): IllegalValue[E, D, T] = new IllegalValue(e)
    def unapply[E, D, T](v: IllegalValue[E, D, T]): Option[E] = Some(v.encoded)
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
