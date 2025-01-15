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

package kantan.codecs.libra

import kantan.codecs.Decoder
import kantan.codecs.Encoder
import libra.Quantity
import shapeless.HList

trait DecoderInstances {

  implicit def libraQuantityDecoder[E, D, F, T, X <: HList](implicit
    decoder: Decoder[E, D, F, T]
  ): Decoder[E, Quantity[D, X], F, T] =
    decoder.map(Quantity.apply)

}

trait EncoderInstances {

  implicit def libraQuantyEncoder[E, D, T, X <: HList](implicit
    encoder: Encoder[E, D, T]
  ): Encoder[E, Quantity[D, X], T] =
    encoder.contramap(_.value)

}
