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

package kantan.codecs.shapeless

import kantan.codecs.{Decoder, Encoder}
import kantan.codecs.export.{DerivedDecoder, DerivedEncoder}
import shapeless.{Coproduct, Generic, HList, Lazy}

/** Provides [[kantan.codecs.Codec]] instances for case classes and sum types.
  *
  * The purpose of this package is to let concrete [[kantan.codecs.Codec]] implementations (such as kantan.csv or
  * kantan.regex) focus on providing instances for `HList` and `Coproduct`. Once such instances exist, this package
  * will take care of the transformation from and to case classes and sum types.
  *
  * Additionally, instances derived that way will be inserted with a sane precedence in the implicit resolution
  * mechanism. This means, for example, that they will not override bespoke `Option` or `Either` instances.
  */
trait ShapelessInstances {
  /** Provides an [[Encoder]] instance for case classes.
    *
    * Given a case class `D`, this expects an [[Encoder]] instance for the `HList` type corresponding to `D`. It will
    * then simply turn values of type `D` into values of the corresponding `HList`, then let the encoder take it from
    * there.
    */
  implicit def caseClassEncoder[E, D, T, H <: HList]
  (implicit gen: Generic.Aux[D, H], er: Lazy[Encoder[E, H, T]]): DerivedEncoder[E, D, T] =
  DerivedEncoder(s ⇒ er.value.encode(gen.to(s)))

  /** Provides a [[Decoder]] instance for case classes.
    *
    * Given a case class `D`, this expects n [[Decoder]] instance for the `HList` type corresponding to `D`. It will
    * then rely on that to turn encoded values into an `HList`, then turn the resulting value into a `D`.
    */
  implicit def caseClassDecoder[E, D, F, T, H <: HList]
  (implicit gen: Generic.Aux[D, H], dr: Lazy[Decoder[E, H, F, T]]): DerivedDecoder[E, D, F, T] =
  DerivedDecoder(s ⇒ dr.value.decode(s).map(gen.from))

  /** Provides an [[Encoder]] instance for sum types.
    *
    * Given a sum type `D`, this expects an [[Encoder]] instance for the `Coproduct` type corresponding to `D`. It
    * will then simply turn values of type `D` into values of the corresponding `Coproduct`, then let the encoder take
    * it from there.
    */
  implicit def sumTypeEncoder[E, D, T, C <: Coproduct]
  (implicit gen: Generic.Aux[D, C], er: Lazy[Encoder[E, C, T]]): DerivedEncoder[E, D, T] =
  DerivedEncoder(m ⇒ er.value.encode(gen.to(m)))

  /** Provides a [[Decoder]] instance for sum types.
    *
    * Given a case class `D`, this expects n [[Decoder]] instance for the `Coproduct` type corresponding to `D`.
    * It will then rely on that to turn encoded values into a `Coproduct`, then turn the resulting value into a `D`.
    */
  implicit def sumTypeDecoder[E, D, F, T, C <: Coproduct]
  (implicit gen: Generic.Aux[D, C], dr: Lazy[Decoder[E, C, F, T]]): DerivedDecoder[E, D, F, T] =
  DerivedDecoder(m ⇒ dr.value.decode(m).map(gen.from))
}
