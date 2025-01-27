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

// TODO: investigate what type variance annotations can be usefully applied to CodecValue.
sealed abstract class CodecValue[E, D, T] extends Product with Serializable {
  def encoded: E
  def mapEncoded[EE](f: E => EE): CodecValue[EE, D, T]
  def mapDecoded[DD](f: D => DD): CodecValue[E, DD, T]
  def tag[TT]: CodecValue[E, D, TT]

  def isLegal: Boolean
  def isIllegal: Boolean =
    !isLegal
}

object CodecValue {
  final case class LegalValue[E, D, T](encoded: E, decoded: D) extends CodecValue[E, D, T] {
    override def mapDecoded[DD](f: D => DD): LegalValue[E, DD, T] =
      LegalValue(encoded, f(decoded))
    override def mapEncoded[EE](f: E => EE): LegalValue[EE, D, T] =
      LegalValue(f(encoded), decoded)
    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    override def tag[TT]: LegalValue[E, D, TT] =
      this.asInstanceOf[LegalValue[E, D, TT]]
    override val isLegal = true
  }

  final case class IllegalValue[E, D, T](encoded: E) extends CodecValue[E, D, T] {
    override def mapDecoded[DD](f: D => DD): IllegalValue[E, DD, T] =
      IllegalValue(encoded)
    override def mapEncoded[EE](f: E => EE): IllegalValue[EE, D, T] =
      IllegalValue(f(encoded))
    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    override def tag[TT]: IllegalValue[E, D, TT] =
      this.asInstanceOf[IllegalValue[E, D, TT]]
    override val isLegal = false
  }
}
