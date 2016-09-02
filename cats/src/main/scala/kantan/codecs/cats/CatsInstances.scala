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

package kantan.codecs.cats

import _root_.cats._
import cats.data.Xor
import cats.functor.{Bifunctor, Contravariant}
import kantan.codecs._
import kantan.codecs.Result.{Failure, Success}

trait CatsInstances extends LowPriorityCatsInstances {
  // - Xor instances ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def xorDecoder[E, DA, DB, F, T](implicit da: Decoder[E, Either[DA, DB], F, T]): Decoder[E, DA Xor DB, F, T] =
    da.map(Xor.fromEither)

  implicit def xorEncoder[E, DA, DB, T](implicit ea: Encoder[E, Either[DA, DB], T]): Encoder[E, DA Xor DB, T] =
    ea.contramap(_.toEither)


  // - Decoder instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def decoderFunctor[E, F, T]: Functor[Decoder[E, ?, F, T]] = new Functor[Decoder[E, ?, F, T]] {
    override def map[A, B](fa: Decoder[E, A, F, T])(f: A ⇒ B) = fa.map(f)
  }



  // - Encoder instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def encoderContravariant[E, T]: Contravariant[Encoder[E, ?, T]] = new Contravariant[Encoder[E, ?, T]] {
    override def contramap[A, B](fa: Encoder[E, A, T])(f: B ⇒ A) = fa.contramap(f)
  }



  // - Result instances ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def resultOrder[F, S](implicit of: Order[F], os: Order[S]): Order[Result[F, S]] = new Order[Result[F, S]] {
    override def compare(x: Result[F, S], y: Result[F, S]): Int = x match {
      case Failure(f1) ⇒ y match {
        case Failure(f2) ⇒ of.compare(f1, f2)
        case Success(_)  ⇒ -1
      }
      case Success(s1) ⇒ y match {
        case Failure(_) ⇒ 1
        case Success(s2) ⇒ os.compare(s1, s2)
      }
    }
  }

  implicit def resultShow[F, S](implicit sf: Show[F], ss: Show[S]): Show[Result[F, S]] = Show.show {
    case Failure(f) ⇒ s"Failure(${sf.show(f)})"
    case Success(s) ⇒ s"Success(${ss.show(s)})"
  }

  implicit def resultMonoid[F, S](implicit sf: Semigroup[F], ms: Monoid[S]): Monoid[Result[F, S]] =
    new Monoid[Result[F, S]] {
      override def empty = Result.success(ms.empty)
      override def combine(x: Result[F, S], y: Result[F, S]) = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ Failure(sf.combine(f1, f2))
          case Success(_)  ⇒ x
        }
        case Success(s1) ⇒ y match {
          case Success(s2) ⇒ Success(ms.combine(s1, s2))
          case Failure(_)  ⇒ y
        }
      }
    }

  implicit def resultInstances[F]: Traverse[Result[F, ?]] with Monad[Result[F, ?]] =
    new Traverse[Result[F, ?]] with Monad[Result[F, ?]] {
      def foldLeft[A, B](fa: Result[F, A], b: B)(f: (B, A) => B): B = fa.foldLeft(b)(f)
      def foldRight[A, B](fa: Result[F, A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        fa.foldRight(lb)(f)
      def traverse[G[_], S1, S2](fa: Result[F, S1])(f: S1 => G[S2])(implicit ag: Applicative[G]): G[Result[F, S2]] =
        fa match {
          case f@Failure(_) ⇒ ag.pure(f)
          case Success(s)   ⇒ ag.map(f(s))(Result.success)
        }

      override def flatMap[A, B](fa: Result[F, A])(f: A => Result[F, B]) = fa.flatMap(f)
      override def tailRecM[A, B](a: A)(f: A ⇒ Result[F, Either[A, B]]) = defaultTailRecM(a)(f)

      override def pure[A](a: A) = Result.success(a)
    }

  implicit def resultBiFunctor: Bifunctor[Result] = new Bifunctor[Result] {
    override def bimap[A, B, C, D](fab: Result[A, B])(f: A => C, g: B => D): Result[C, D] =
      fab.bimap(f, g)
  }
}
