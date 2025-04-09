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

import kantan.codecs.`export`.DerivedEncoder

/** Type class for types that can be encoded into others.
  *
  * @tparam E
  *   encoded type - what to encode to.
  * @tparam D
  *   decoded type - what to encode from.
  * @tparam T
  *   tag type.
  */
trait Encoder[E, D, T] extends Serializable {

  /** Encodes the specified value. */
  def encode(d: D): E

  def mapEncoded[EE](f: E => EE): Encoder[EE, D, T] =
    Encoder.from(d => f(encode(d)))

  /** Creates a new [[Encoder]] instances that applies the specified function before encoding.
    *
    * This is a convenient way of creating [[Encoder]] instances: if you already have an `Encoder[E, D, R]`, need to
    * write an `Encoder[E, DD, R]` and know how to turn a `DD` into a `D`, you need but call [[contramap]].
    */
  def contramap[DD](f: DD => D): Encoder[E, DD, T] =
    Encoder.from(f.andThen(encode))

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def tag[TT]: Encoder[E, D, TT] =
    this.asInstanceOf[Encoder[E, D, TT]]
}

trait EncoderCompanion[E, T] {

  /** Summons an implicit instance of [[Encoder]] if one is found, fails compilation otherwise.
    *
    * This is a slightly faster, less verbose version of `implicitly`.
    */
  def apply[D](implicit ev: Encoder[E, D, T]): Encoder[E, D, T] =
    ev

  /** Creates a new instance of [[Encoder]] from the specified function. */
  @inline def from[D](f: D => E): Encoder[E, D, T] =
    Encoder.from(f)
}

object Encoder {
  def from[E, D, T](f: D => E): Encoder[E, D, T] =
    new Encoder[E, D, T] {
      override def encode(d: D) =
        f(d)
    }

  implicit def encoderFromExported[E, D, T](implicit ea: DerivedEncoder[E, D, T]): Encoder[E, D, T] =
    ea.value

  implicit def optionalEncoder[E: Optional, D, T](implicit ea: Encoder[E, D, T]): Encoder[E, Option[D], T] =
    Encoder.from(_.map(ea.encode).getOrElse(Optional[E].empty))

  implicit def eitherEncoder[E, D1, D2, T](implicit
    ea: Encoder[E, D1, T],
    eb: Encoder[E, D2, T]
  ): Encoder[E, Either[D1, D2], T] =
    Encoder.from {
      case Left(a)  => ea.encode(a)
      case Right(b) => eb.encode(b)
    }
}
