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

package kantan.codecs.scalaz

import kantan.codecs.Decoder
import kantan.codecs.Encoder
import kantan.codecs.error.Error
import kantan.codecs.strings.DecodeError
import scalaz.-\/
import scalaz.Contravariant
import scalaz.Cord
import scalaz.Equal
import scalaz.Maybe
import scalaz.MonadError
import scalaz.Plus
import scalaz.Show
import scalaz.\/
import scalaz.\/-

trait DecoderInstances {

  implicit final def decoderInstances[E, F, T]
    : MonadError[({ type L[A] = Decoder[E, A, F, T] })#L, F] with Plus[({ type L[A] = Decoder[E, A, F, T] })#L] =
    new MonadError[({ type L[A] = Decoder[E, A, F, T] })#L, F] with Plus[({ type L[A] = Decoder[E, A, F, T] })#L] {

      override def point[A](a: => A) =
        Decoder.from(_ => Right(a))

      override def bind[A, B](fa: Decoder[E, A, F, T])(f: A => Decoder[E, B, F, T]) =
        fa.flatMap(f)

      override def handleError[A](fa: Decoder[E, A, F, T])(f: F => Decoder[E, A, F, T]) =
        fa.handleErrorWith(f)

      override def raiseError[A](e: F) =
        Decoder.from(_ => Left(e))

      override def plus[A](a: Decoder[E, A, F, T], b: => Decoder[E, A, F, T]) =
        a.orElse(b)

    }
}

trait EncoderInstances {

  implicit def encoderContravariant[E, T]: Contravariant[({ type L[A] = Encoder[E, A, T] })#L] =
    new Contravariant[({ type L[A] = Encoder[E, A, T] })#L] {
      override def contramap[D, DD](fa: Encoder[E, D, T])(f: DD => D) =
        fa.contramap(f)
    }

}

trait CommonInstances {

  implicit def isErrorShow[E <: Error]: Show[E] =
    Show.show(e => Cord(e.toString))

  implicit def disjunctionDecoder[E, DA, DB, F, T](implicit
    da: Decoder[E, DA, F, T],
    db: Decoder[E, DB, F, T]
  ): Decoder[E, DA \/ DB, F, T] =
    da.map(-\/.apply[DA, DB]).orElse(db.map(\/-.apply[DA, DB]))

  implicit def disjunctionEncoder[E, DA, DB, T](implicit
    ea: Encoder[E, DA, T],
    eb: Encoder[E, DB, T]
  ): Encoder[E, DA \/ DB, T] =
    Encoder.from {
      case -\/(a) => ea.encode(a)
      case \/-(b) => eb.encode(b)
    }

  implicit def maybeDecoder[E, D, F, T](implicit da: Decoder[E, Option[D], F, T]): Decoder[E, Maybe[D], F, T] =
    da.map(Maybe.fromOption)

  implicit def maybeEncoder[E, D, T](implicit ea: Encoder[E, Option[D], T]): Encoder[E, Maybe[D], T] =
    ea.contramap(_.toOption)

  implicit val stringDecodeErrorEqual: Equal[DecodeError] = Equal.equalA[DecodeError]

}
