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

import kantan.codecs.export.Exported

/** Type class for types that can be encoded into others.
  *
  * @tparam E encoded type - what to encode to.
  * @tparam D decoded type - what to encode from.
  * @tparam T tag type.
  */
trait Encoder[E, D, T] extends Any with Serializable {
  /** Encodes the specified value. */
  def encode(d: D): E

  def mapEncoded[EE](f: E ⇒ EE): Encoder[EE, D, T] = Encoder(d ⇒ f(encode(d)))

  /** Creates a new [[Encoder]] instances that applies the specified function before encoding.
    *
    * This is a convenient way of creating [[Encoder]] instances: if you already have an `Encoder[E, D, R]`, need to
    * write an `Encoder[E, DD, R]` and know how to turn a `DD` into a `D`, you need but call [[contramap]].
    */
  def contramap[DD](f: DD ⇒ D): Encoder[E, DD, T] = Encoder(f andThen encode)

  def tag[TT]: Encoder[E, D, TT] = this.asInstanceOf[Encoder[E, D, TT]]
}

object Encoder {
  def apply[E, D, T](f: D ⇒ E): Encoder[E, D, T] = new Encoder[E, D, T] {
    override def encode(d: D) = f(d)
  }

  implicit def encoderFromExported[E, A, T](implicit ea: Exported[Encoder[E, A, T]]): Encoder[E, A, T] =
    ea.value

  implicit def optionalEncoder[E, A, T](implicit ea: Encoder[E, A, T], oe: Optional[E]): Encoder[E, Option[A], T] =
    Encoder(_.map(ea.encode).getOrElse(oe.empty))

  implicit def eitherEncoder[E, A, B, T](implicit ea: Encoder[E, A, T], eb: Encoder[E, B, T])
  : Encoder[E, Either[A, B], T] =
    Encoder {_ match {
      case Left(a)  ⇒ ea.encode(a)
      case Right(b) ⇒ eb.encode(b)
    }}
}
