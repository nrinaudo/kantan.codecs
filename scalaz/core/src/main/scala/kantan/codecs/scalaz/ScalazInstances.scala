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
package scalaz

import _root_.scalaz._

trait ScalazInstances extends LowPriorityScalazInstances {
  // - \/ instances ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def disjunctionDecoder[E, DA, DB, F, T](
    implicit da: Decoder[E, Either[DA, DB], F, T]
  ): Decoder[E, DA \/ DB, F, T] =
    da.map(\/.fromEither)

  implicit def disjunctionEncoder[E, DA, DB, T](implicit ea: Encoder[E, Either[DA, DB], T]): Encoder[E, DA \/ DB, T] =
    ea.contramap(_.toEither)

  // - Maybe instances -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def maybeDecoder[E, D, F, T](implicit da: Decoder[E, Option[D], F, T]): Decoder[E, Maybe[D], F, T] =
    da.map(Maybe.fromOption)

  implicit def maybeEncoder[E, D, T](implicit ea: Encoder[E, Option[D], T]): Encoder[E, Maybe[D], T] =
    ea.contramap(_.toOption)

  // - Decoder instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def decoderFunctor[E, F, T]: Functor[Decoder[E, ?, F, T]] = new Functor[Decoder[E, ?, F, T]] {
    override def map[D, DD](fa: Decoder[E, D, F, T])(f: D ⇒ DD) = fa.map(f)
  }

  // - Encoder instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def encoderContravariant[E, T]: Contravariant[Encoder[E, ?, T]] = new Contravariant[Encoder[E, ?, T]] {
    override def contramap[D, DD](fa: Encoder[E, D, T])(f: DD ⇒ D) = fa.contramap(f)
  }

}
