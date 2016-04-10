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

package kantan.codecs.strings

import kantan.codecs.Encoder

object StringEncoder {
  def apply[D](f: D ⇒ String): StringEncoder[D] = Encoder(f)
}

/** Defines default instances of [[StringEncoder]]. */
trait StringEncoderInstances {
  /** Encoder for `Option[A]`, for any `A` that has a [[StringEncoder]] instance. */
  implicit def optionEncoder[A](implicit ca: StringEncoder[A]): StringEncoder[Option[A]] =
    StringEncoder(_.map(ca.encode).getOrElse(""))

  /** Encoder for `Either[A, B]`, for any `A` and `B` that have a [[StringEncoder]] instance. */
  implicit def eitherEncoder[A, B](implicit ca: StringEncoder[A], cb: StringEncoder[B]): StringEncoder[Either[A, B]] =
    StringEncoder {_ match {
      case Left(a) ⇒ ca.encode(a)
      case Right(b) ⇒ cb.encode(b)
    }}
}
