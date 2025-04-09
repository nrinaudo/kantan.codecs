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

import kantan.codecs.Decoder
import kantan.codecs.Encoder
import kantan.codecs.error.IsError
import kantan.codecs.`export`.DerivedDecoder
import kantan.codecs.`export`.DerivedEncoder
import shapeless.:+:
import shapeless.CNil
import shapeless.Coproduct
import shapeless.Generic
import shapeless.HList
import shapeless.Inl
import shapeless.Inr
import shapeless.LabelledGeneric
import shapeless.Lazy

/** Provides `Codec` instances for case classes and sum types.
  *
  * The purpose of this package is to let concrete `Codec` implementations (such as kantan.csv or kantan.regex) focus on
  * providing instances for `HList` and `Coproduct`. Once such instances exist, this package will take care of the
  * transformation from and to case classes and sum types.
  *
  * Additionally, instances derived that way will be inserted with a sane precedence in the implicit resolution
  * mechanism. This means, for example, that they will not override bespoke `Option` or `Either` instances.
  */
trait ShapelessInstances {
  // - Case classes ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Provides an `Encoder` instance for case classes.
    *
    * Given a case class `D`, this expects an `Encoder` instance for the `HList` type corresponding to `D`. It will then
    * simply turn values of type `D` into values of the corresponding `HList`, then let the encoder take it from there.
    */
  implicit def caseClassEncoder[E, D, T, H <: HList](implicit
    gen: Generic.Aux[D, H],
    er: Lazy[Encoder[E, H, T]]
  ): DerivedEncoder[E, D, T] =
    DerivedEncoder.from(s => er.value.encode(gen.to(s)))

  /** Similar to [[caseClassEncoder]], but working with `LabelledGeneric` rather than just `Generic`. */
  implicit def caseClassEncoderFromLabelled[E, D, T, H <: HList](implicit
    generic: LabelledGeneric.Aux[D, H],
    hEncoder: Lazy[Encoder[E, H, T]]
  ): DerivedEncoder[E, D, T] =
    DerivedEncoder.from(value => hEncoder.value.encode(generic.to(value)))

  /** Provides a `Decoder` instance for case classes.
    *
    * Given a case class `D`, this expects n `Decoder` instance for the `HList` type corresponding to `D`. It will then
    * rely on that to turn encoded values into an `HList`, then turn the resulting value into a `D`.
    */
  implicit def caseClassDecoder[E, D, F, T, H <: HList](implicit
    gen: Generic.Aux[D, H],
    dr: Lazy[Decoder[E, H, F, T]]
  ): DerivedDecoder[E, D, F, T] =
    DerivedDecoder.from(s => dr.value.decode(s).map(gen.from))

  /** Similar to [[caseClassDecoder]], but working with `LabelledGeneric` rather than just `Generic`. */
  implicit def caseClassDecoderFromLabelled[E, D, F, T, H <: HList](implicit
    generic: LabelledGeneric.Aux[D, H],
    hDecoder: Lazy[Decoder[E, H, F, T]]
  ): DerivedDecoder[E, D, F, T] =
    DerivedDecoder.from(value => hDecoder.value.decode(value).map(generic.from))

  // - Sum types -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Provides an `Encoder` instance for sum types.
    *
    * Given a sum type `D`, this expects an `Encoder` instance for the `Coproduct` type corresponding to `D`. It will
    * then simply turn values of type `D` into values of the corresponding `Coproduct`, then let the encoder take it
    * from there.
    */
  implicit def sumTypeEncoder[E, D, T, C <: Coproduct](implicit
    gen: Generic.Aux[D, C],
    er: Lazy[Encoder[E, C, T]]
  ): DerivedEncoder[E, D, T] =
    DerivedEncoder.from(m => er.value.encode(gen.to(m)))

  /** Provides a `Decoder` instance for sum types.
    *
    * Given a case class `D`, this expects n `Decoder` instance for the `Coproduct` type corresponding to `D`. It will
    * then rely on that to turn encoded values into a `Coproduct`, then turn the resulting value into a `D`.
    */
  implicit def sumTypeDecoder[E, D, F, T, C <: Coproduct](implicit
    gen: Generic.Aux[D, C],
    dr: Lazy[Decoder[E, C, F, T]]
  ): DerivedDecoder[E, D, F, T] =
    DerivedDecoder.from(m => dr.value.decode(m).map(gen.from))

  // - Coproducts ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def cnilDecoder[E, F: IsError, T]: Decoder[E, CNil, F, T] =
    Decoder.from(_ => Left(IsError[F].fromMessage("Attempting to decode CNil")))

  implicit def coproductDecoder[E, H, D <: Coproduct, F, T](implicit
    dh: Decoder[E, H, F, T],
    dt: Decoder[E, D, F, T]
  ): Decoder[E, H :+: D, F, T] =
    Decoder.from(e => dh.decode(e).map(Inl.apply).left.flatMap(_ => dt.decode(e).map(Inr.apply)))

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit def cnilEncoder[E, D, T]: Encoder[E, CNil, T] =
    Encoder.from(_ => throw new IllegalStateException("trying to encode CNil, this should not happen"))

  implicit def coproductEncoder[E, H, D <: Coproduct, T](implicit
    eh: Encoder[E, H, T],
    ed: Encoder[E, D, T]
  ): Encoder[E, H :+: D, T] =
    Encoder.from {
      case Inl(h) => eh.encode(h)
      case Inr(d) => ed.encode(d)
    }
}
