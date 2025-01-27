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

package kantan.codecs.enumeratum

import enumeratum.Enum
import enumeratum.EnumEntry
import kantan.codecs.Decoder
import kantan.codecs.Encoder
import kantan.codecs.error.IsError

import scala.annotation.nowarn

/** Defines implicit `Decoder` instances for any enumeratum `Enum` type. */
trait DecoderInstances {

  implicit def enumeratumDecoder[E, D <: EnumEntry, F, T](implicit
    enumD: Enum[D],
    decoder: Decoder[E, String, F, T],
    error: IsError[F]
  ): Decoder[E, D, F, T] = {
    // Enum is not serializable. The following is to make sure that the generated Decoder doesn't embark a
    // non-serializable value and becomes non-serializable itself.
    val map       = enumD.namesToValuesMap
    val enumLabel = enumD.values.map(_.entryName).mkString("[", ", ", "]")

    decoder.emap { name =>
      map.get(name).toRight(error.fromMessage(s"'$name' is not a member of enumeration $enumLabel"))
    }
  }

}

/** Defines implicit `Encoder` instances for any enumeratum `Enum` type. */
trait EncoderInstances {

  // We're adding an `Enum` constraint here because:
  // - it has a major impact for `ValueEnum`
  // - I haven't been able to decide whether it was useful here or not, but it certainly can't hurt to reduce the
  //   implicit search space.
  @nowarn
  implicit def enumeratumEncoder[E, D <: EnumEntry: Enum, T](implicit
    encoder: Encoder[E, String, T]
  ): Encoder[E, D, T] =
    encoder.contramap(_.entryName)

}
