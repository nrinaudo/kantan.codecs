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

package kantan.codecs.enumeratum.values

import enumeratum.values._
import kantan.codecs.Decoder
import kantan.codecs.Encoder
import kantan.codecs.error.IsError

import scala.annotation.nowarn

// I would really prefer enumeratumDecoder && enumeratumEncoder to be implicit and to be sufficient, but somehow,
// through a mechanism that I have yet to work out, I can get an implicit IntEnum[D] but not one of ValueEnum[Int, D],
// where I thought these were basically the same.

/** Defines implicit `Decoder` instances for any enumeratum `ValueEnum` type. */
trait DecoderInstances {

  @SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
  def enumeratumDecoder[V, E, D <: ValueEnumEntry[V], F, T](implicit
    valueEnum: ValueEnum[V, D],
    decoder: Decoder[E, V, F, T],
    error: IsError[F]
  ): Decoder[E, D, F, T] = {
    // ValueEnum is not serializable. The following is to make sure that the generated Decoder doesn't embark a
    // non-serializable value and becomes non-serializable itself.
    val map       = valueEnum.valuesToEntriesMap
    val enumLabel = valueEnum.values.map(_.value).mkString("[", ", ", "]")

    decoder.emap { value =>
      map.get(value).toRight(error.fromMessage(s"'$value' is not in values $enumLabel"))
    }
  }

  implicit def intEnumeratumDecoder[E, D <: IntEnumEntry: IntEnum, F: IsError, T](implicit
    decoder: Decoder[E, Int, F, T]
  ): Decoder[E, D, F, T] =
    enumeratumDecoder

  implicit def longEnumeratumDecoder[E, D <: LongEnumEntry: LongEnum, F: IsError, T](implicit
    decoder: Decoder[E, Long, F, T]
  ): Decoder[E, D, F, T] =
    enumeratumDecoder

  implicit def shortEnumeratumDecoder[E, D <: ShortEnumEntry: ShortEnum, F: IsError, T](implicit
    decoder: Decoder[E, Short, F, T]
  ): Decoder[E, D, F, T] =
    enumeratumDecoder

  implicit def stringEnumeratumDecoder[E, D <: StringEnumEntry: StringEnum, F: IsError, T](implicit
    decoder: Decoder[E, String, F, T]
  ): Decoder[E, D, F, T] =
    enumeratumDecoder
  implicit def byteEnumeratumDecoder[E, D <: ByteEnumEntry: ByteEnum, F: IsError, T](implicit
    decoder: Decoder[E, Byte, F, T]
  ): Decoder[E, D, F, T] =
    enumeratumDecoder

  implicit def charEnumeratumDecoder[E, D <: CharEnumEntry: CharEnum, F: IsError, T](implicit
    decoder: Decoder[E, Char, F, T]
  ): Decoder[E, D, F, T] =
    enumeratumDecoder

}

/** Defines implicit `Encoder` instances for any enumeratum `ValueEnum` type.
  *
  * A peculiarity of these `Encoder` implementations is that they require an implicit parameter that they do not use:
  * the `Enum` instance. This is meant to make these instances much more restrictive, as otherwise the implicit
  * resolution mechanism goes completely wild and takes up to 20 minutes to compile something relatively
  * straightforward.
  */
trait EncoderInstances {

  def enumeratumEncoder[V, E, D <: ValueEnumEntry[V], T](implicit
    encoder: Encoder[E, V, T]
  ): Encoder[E, D, T] =
    encoder.contramap(_.value)

  @nowarn
  implicit def intEnumeratumEncoder[E, D <: IntEnumEntry: IntEnum, T](implicit
    encoder: Encoder[E, Int, T]
  ): Encoder[E, D, T] =
    enumeratumEncoder(encoder)

  @nowarn
  implicit def longEnumeratumEncoder[E, D <: LongEnumEntry: LongEnum, T](implicit
    encoder: Encoder[E, Long, T]
  ): Encoder[E, D, T] =
    enumeratumEncoder(encoder)

  @nowarn
  implicit def shortEnumeratumEncoder[E, D <: ShortEnumEntry: ShortEnum, T](implicit
    encoder: Encoder[E, Short, T]
  ): Encoder[E, D, T] =
    enumeratumEncoder(encoder)

  @nowarn
  implicit def stringEnumeratumEncoder[E, D <: StringEnumEntry: StringEnum, T](implicit
    encoder: Encoder[E, String, T]
  ): Encoder[E, D, T] =
    enumeratumEncoder(encoder)

  @nowarn
  implicit def byteEnumeratumEncoder[E, D <: ByteEnumEntry: ByteEnum, T](implicit
    encoder: Encoder[E, Byte, T]
  ): Encoder[E, D, T] =
    enumeratumEncoder(encoder)

  @nowarn
  implicit def charEnumeratumEncoder[E, D <: CharEnumEntry: CharEnum, T](implicit
    encoder: Encoder[E, Char, T]
  ): Encoder[E, D, T] =
    enumeratumEncoder(encoder)

}
