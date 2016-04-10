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

import kantan.codecs.{Decoder, Result}

object StringDecoder {
  def apply[D](f: String ⇒ Result[Throwable, D]): StringDecoder[D] = Decoder(f)
}

/** Defines default instances of [[StringDecoder]]. */
trait StringDecoderInstances {
  /** Decoder for `Option[A]`, for any `A` that has a [[StringDecoder]] instance. */
  implicit def optionDecoder[A](implicit ca: StringDecoder[A]): StringDecoder[Option[A]] = StringDecoder{ s ⇒
    if(s.isEmpty) Result.success(None)
    else          ca.decode(s).map(Some.apply)
  }

  /** Decoder for `Either[A, B]`, for any `A` and `B` that have a [[StringDecoder]] instance. */
  implicit def eitherDecoder[A, B](implicit ca: StringDecoder[A], cb: StringDecoder[B]): StringDecoder[Either[A, B]] =
    StringDecoder { s ⇒
      ca.decode(s).map(a ⇒ Left(a): Either[A, B]).orElse(cb.decode(s).map(b ⇒ Right(b): Either[A, B]))
    }
}
