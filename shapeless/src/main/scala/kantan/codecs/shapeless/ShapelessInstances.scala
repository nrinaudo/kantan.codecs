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
import kantan.codecs.export.Exported
import shapeless.{Coproduct, Generic, HList, Lazy}

trait ShapelessInstances {
  implicit def caseClassEncoder[E, D, T, H <: HList]
  (implicit gen: Generic.Aux[D, H], er: Lazy[Encoder[E, H, T]]): Exported[Encoder[E, D, T]] =
    Exported(Encoder(s ⇒ er.value.encode(gen.to(s))))

  implicit def caseClassDecoder[E, D, F, T, H <: HList]
  (implicit gen: Generic.Aux[D, H], dr: Lazy[Decoder[E, H, F, T]]): Exported[Decoder[E, D, F, T]] =
    Exported(Decoder(s ⇒ dr.value.decode(s).map(gen.from)))

  implicit def sumTypeEncoder[E, D, T, C <: Coproduct]
  (implicit gen: Generic.Aux[D, C], er: Lazy[Encoder[E, C, T]]): Exported[Encoder[E, D, T]] =
    Exported(Encoder(m ⇒ er.value.encode(gen.to(m))))

  implicit def sumTypeDecoder[E, D, F, T, C <: Coproduct]
  (implicit gen: Generic.Aux[D, C], dr: Lazy[Decoder[E, C, F, T]]): Exported[Decoder[E, D, F, T]] =
    Exported(Decoder(m ⇒ dr.value.decode(m).map(gen.from)))
}
