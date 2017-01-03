/*
 * Copyright 2017 Nicolas Rinaudo
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

import kantan.codecs._
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalacheck.Prop

trait DecoderLaws[E, D, F, T] {
  def decoder: Decoder[E, D, F, T]

  private def cmp(result: Result[F, D], cv: CodecValue[E, D, T]): Boolean = (cv, result) match {
    case (IllegalValue(_),  Result.Failure(_))  ⇒ true
    case (LegalValue(_, d), Result.Success(d2)) ⇒ d == d2
    case _                                      ⇒ false
  }



  // - Simple laws -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def decode(v: CodecValue[E, D, T]): Boolean = cmp(decoder.decode(v.encoded), v)

  def decodeFailure(v: IllegalValue[E, D, T]): Boolean =
    Prop.throws(classOf[Exception])(decoder.unsafeDecode(v.encoded))


  // - Functor laws ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapIdentity(v: CodecValue[E, D, T]): Boolean =
    decoder.decode(v.encoded) == decoder.map(identity).decode(v.encoded)

  def mapComposition[A, B](v: CodecValue[E, D, T], f: D ⇒ A, g: A ⇒ B): Boolean =
    decoder.map(f andThen g).decode(v.encoded) == decoder.map(f).map(g).decode(v.encoded)

  def mapErrorIdentity[A](v: CodecValue[E, D, T]): Boolean =
    decoder.decode(v.encoded) == decoder.mapError(identity).decode(v.encoded)

  def mapErrorComposition[A, B](v: CodecValue[E, D, T], f: F ⇒ A, g: A ⇒ B): Boolean =
    decoder.mapError(f andThen g).decode(v.encoded) == decoder.mapError(f).mapError(g).decode(v.encoded)



  // - Contravariant laws ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def contramapEncodedIdentity(v: CodecValue[E, D, T]): Boolean =
    decoder.decode(v.encoded) == decoder.contramapEncoded(identity[E]).decode(v.encoded)

  def contramapEncodedComposition[A, B](b: B, f: A ⇒ E, g: B ⇒ A): Boolean =
    decoder.contramapEncoded(g andThen f).decode(b) == decoder.contramapEncoded(f).contramapEncoded(g).decode(b)



  // - "Kleisli" laws --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def mapResultIdentity(v: CodecValue[E, D, T]): Boolean =
    decoder.decode(v.encoded) == decoder.mapResult(Result.success).decode(v.encoded)

  def mapResultComposition[A, B](v: CodecValue[E, D, T], f: D ⇒ Result[F, A], g: A ⇒ Result[F, B]): Boolean =
    decoder.mapResult(d ⇒ f(d).flatMap(g)).decode(v.encoded) ==  decoder.mapResult(f).mapResult(g).decode(v.encoded)
}

object DecoderLaws {
  implicit def apply[E, D, F, T](implicit de: Decoder[E, D, F, T]): DecoderLaws[E, D, F, T] =
    new DecoderLaws[E, D, F, T] {
      override val decoder = de
    }
}
