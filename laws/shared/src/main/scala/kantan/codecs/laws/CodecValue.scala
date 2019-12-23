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
sealed abstract class CodecValue[Encoded, Decoded, Tag] extends Product with Serializable {
  def encoded: Encoded
  def mapEncoded[E](f: Encoded => E): CodecValue[E, Decoded, Tag]
  def mapDecoded[D](f: Decoded => D): CodecValue[Encoded, D, Tag]
  def tag[T]: CodecValue[Encoded, Decoded, T]

  def isLegal: Boolean
  def isIllegal: Boolean = !isLegal
}

object CodecValue {
  final case class LegalValue[Encoded, Decoded, Tag](encoded: Encoded, decoded: Decoded)
      extends CodecValue[Encoded, Decoded, Tag] {

    override def mapDecoded[D](f: Decoded => D) = LegalValue(encoded, f(decoded))
    override def mapEncoded[E](f: Encoded => E) = LegalValue(f(encoded), decoded)
    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    override def tag[T]  = this.asInstanceOf[LegalValue[Encoded, Decoded, T]]
    override val isLegal = true
  }

  final case class IllegalValue[Encoded, Decoded, Tag](encoded: Encoded) extends CodecValue[Encoded, Decoded, Tag] {
    override def mapDecoded[D](f: Decoded => D) = IllegalValue(encoded)
    override def mapEncoded[E](f: Encoded => E) = IllegalValue(f(encoded))
    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    override def tag[T]  = this.asInstanceOf[IllegalValue[Encoded, Decoded, T]]
    override val isLegal = false
  }
}
