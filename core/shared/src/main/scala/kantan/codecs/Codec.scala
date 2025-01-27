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

/** Combines a [[Decoder]] and an [[Encoder]].
  *
  * Codecs are only meant as a convenience, and should not be considered more powerful or desirable than encoders or
  * decoders. Some types can be both encoded to and decoded from, and being able to define both instances in one call is
  * convenient. It's however very poor practice to request a type to have a [[Codec]] instance - a much preferred
  * alternative would be to require it to have a [[Decoder]] and an [[Encoder]] instance, which a [[Codec]] would
  * fulfill.
  */
trait Codec[E, D, F, T] extends Decoder[E, D, F, T] with Encoder[E, D, T] {
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def tag[TT]: Codec[E, D, F, TT] =
    this.asInstanceOf[Codec[E, D, F, TT]]

  override def leftMap[FF](f: F => FF): Codec[E, D, FF, T] =
    Codec.from(super.leftMap(f), this)

  @deprecated("Use leftMap instead", "0.2.1")
  override def mapError[FF](f: F => FF): Codec[E, D, FF, T] =
    leftMap(f)

  def imap[DD](f: D => DD)(g: DD => D): Codec[E, DD, F, T] =
    Codec.from((e: E) => decode(e).map(f))(g.andThen(encode))
  def imapEncoded[EE](f: E => EE)(g: EE => E): Codec[EE, D, F, T] =
    Codec.from(g.andThen(decode))(d => f(encode(d)))
}

trait CodecCompanion[E, F, T] {
  @inline def from[D](f: E => Either[F, D])(g: D => E): Codec[E, D, F, T] =
    Codec.from(f)(g)
  @inline def from[D](d: Decoder[E, D, F, T], e: Encoder[E, D, T]): Codec[E, D, F, T] =
    Codec.from(d, e)
}

/** Provides instance creation methods for [[Codec]]. */
object Codec {
  def from[E, D, F, T](f: E => Either[F, D])(g: D => E): Codec[E, D, F, T] =
    new Codec[E, D, F, T] {
      override def encode(d: D) =
        g(d)
      override def decode(e: E) =
        f(e)
    }

  def from[E, D, F, T](d: Decoder[E, D, F, T], e: Encoder[E, D, T]): Codec[E, D, F, T] =
    from(d.decode _)(e.encode)
}
