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
package cats

import _root_.cats._
import scala.annotation.tailrec
import strings.DecodeError

trait DecoderInstances {

  implicit final def decoderInstances[E, F, T]
    : SemigroupK[Decoder[E, ?, F, T]] with MonadError[Decoder[E, ?, F, T], F] =
    new SemigroupK[Decoder[E, ?, F, T]] with MonadError[Decoder[E, ?, F, T], F] {

      final def combineK[D](x: Decoder[E, D, F, T], y: Decoder[E, D, F, T]): Decoder[E, D, F, T] = x.orElse(y)

      final def pure[D](d: D): Decoder[E, D, F, T] = Decoder.from(_ ⇒ Right(d))

      override final def map[A, B](fa: Decoder[E, A, F, T])(f: A ⇒ B): Decoder[E, B, F, T] = fa.map(f)

      override final def product[A, B](fa: Decoder[E, A, F, T], fb: Decoder[E, B, F, T]): Decoder[E, (A, B), F, T] =
        fa.product(fb)

      final def flatMap[A, B](fa: Decoder[E, A, F, T])(f: A ⇒ Decoder[E, B, F, T]): Decoder[E, B, F, T] = fa.flatMap(f)

      final def raiseError[D](f: F): Decoder[E, D, F, T] = Decoder.from(_ ⇒ Left(f))

      final def handleErrorWith[D](fd: Decoder[E, D, F, T])(f: F ⇒ Decoder[E, D, F, T]): Decoder[E, D, F, T] =
        fd.handleErrorWith(f)

      @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
      final def tailRecM[A, B](a: A)(f: A ⇒ Decoder[E, Either[A, B], F, T]): Decoder[E, B, F, T] = Decoder.from {

        @tailrec
        def loop(e: E, a1: A): Either[F, B] = f(a1).decode(e) match {
          case l @ Left(_)         ⇒ l.asInstanceOf[Either[F, B]]
          case Right(Left(a2))     ⇒ loop(e, a2)
          case Right(r @ Right(_)) ⇒ r.asInstanceOf[Either[F, B]]
        }

        s ⇒
          loop(s, a)
      }
    }
}

trait EncoderInstances {

  implicit def encoderContravariant[E, T]: Contravariant[Encoder[E, ?, T]] = new Contravariant[Encoder[E, ?, T]] {
    override def contramap[A, B](e: Encoder[E, A, T])(f: B ⇒ A) = e.contramap(f)
  }

}

trait CommonInstances {

  implicit def isErrorShow[E <: error.Error]: Show[E] = Show.show(_.toString)

  implicit val stringDecodeErrorEq: Eq[DecodeError] = Eq.fromUniversalEquals

}
