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

import _root_.scalaz._
import imp.imp
import kantan.codecs._
import kantan.codecs.Result.{Failure, Success}

trait ScalazInstances extends LowPriorityScalazInstances {
  // - \/ instances ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def disjunctionDecoder[E, DA, DB, F, T]
  (implicit da: Decoder[E, Either[DA, DB], F, T]): Decoder[E, DA \/ DB, F, T] =
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



  // - Result instances ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def resultOrder[F: Order, S: Order]: Order[Result[F, S]] =
    new Order[Result[F, S]] {
      override def order(x: Result[F, S], y: Result[F, S]): Ordering = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ imp[Order[F]].order(f1, f2)
          case Success(_)  ⇒ Ordering.LT
        }
        case Success(s1) ⇒ y match {
          case Failure(_) ⇒ Ordering.GT
          case Success(s2) ⇒ imp[Order[S]].order(s1, s2)
        }
      }
    }

  implicit def resultShow[F: Show, S: Show]: Show[Result[F, S]] = Show.shows {
    case Failure(f) ⇒ s"Failure(${imp[Show[F]].show(f)})"
    case Success(s) ⇒ s"Success(${imp[Show[S]].show(s)})"
  }

  implicit def resultMonoid[F: Semigroup, S: Monoid]: Monoid[Result[F, S]] =
    new Monoid[Result[F, S]] {
      override def zero = Result.success(imp[Monoid[S]].zero)

      override def append(d1: Result[F, S], d2: ⇒ Result[F, S]) = d1 match {
        case Failure(f1) ⇒ d2 match {
          case Failure(f2) ⇒ Result.failure(imp[Semigroup[F]].append(f1, f2))
          case Success(_)  ⇒ d1
        }
        case Success(s1) ⇒ d2 match {
          case Failure(_)  ⇒ d2
          case Success(s2) ⇒ Result.success(imp[Monoid[S]].append(s1, s2))
        }
      }
    }

  implicit def resultInstances[F]: Monad[Result[F, ?]] with Traverse[Result[F, ?]] =
    new Monad[Result[F, ?]] with Traverse[Result[F, ?]] {
      override def traverseImpl[G[_], S, SS](d: Result[F, S])(f: S ⇒ G[SS])(implicit ag: Applicative[G]) = d match {
        case f@Failure(_) ⇒ ag.pure(f)
        case Success(s)   ⇒ ag.map(f(s))(Result.success)
      }
      override def bind[S, SS](d: Result[F, S])(f: S ⇒ Result[F, SS]) = d.flatMap(f)
      override def point[S](s: ⇒ S) = Result.success(s)
      override def map[S, SS](r: Result[F, S])(f: S ⇒ SS) = r.map(f)
      override def foldLeft[S, A](r: Result[F, S], z: A)(f: (A, S) ⇒ A) = r.foldLeft(z)(f)
    }

  implicit def resultBitraverse: Bitraverse[Result] = new Bitraverse[Result] {
    override def bitraverseImpl[G[_], A, B, C, D](fab: Result[A, B])(f: A => G[C], g: B => G[D])
                                                 (implicit ag: Applicative[G])=
      fab match {
        case Failure(a) => Functor[G].map(f(a))(Failure.apply)
        case Success(b) => Functor[G].map(g(b))(Success.apply)
      }
    override def bimap[A, B, C, D](fab: Result[A, B])(f: A => C, g: B => D) = fab.bimap(f, g)
  }
}
