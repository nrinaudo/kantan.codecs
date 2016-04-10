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
import kantan.codecs._
import kantan.codecs.Result.{Failure, Success}
import kantan.codecs.strings._

trait ScalazInstances extends LowPriorityScalazInstances {
  // - \/ instances ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def disjunctionDecoder[A, B](implicit da: StringDecoder[A], db: StringDecoder[B]): StringDecoder[A \/ B] =
    StringDecoder { s ⇒ da.decode(s).map(\/.left).orElse(db.decode(s).map(\/.right)) }

  implicit def disjunctionEncoder[A, B](implicit ea: StringEncoder[A], eb: StringEncoder[B]): StringEncoder[A \/ B] =
    StringEncoder(xab ⇒ xab.fold(ea.encode, eb.encode))



  // - Maybe instances -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def maybeDecoder[A](implicit da: StringDecoder[A]): StringDecoder[Maybe[A]] = StringDecoder { s ⇒
    if(s.isEmpty) Result.success(Maybe.empty)
    else          da.decode(s).map(Maybe.just)
  }

  implicit def maybeEncoder[A](implicit ea: StringEncoder[A]): StringEncoder[Maybe[A]] =
    StringEncoder(_.map(ea.encode).getOrElse(""))


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
  implicit def resultOrder[F, S](implicit of: Order[F], os: Order[S]): Order[Result[F, S]] =
    new Order[Result[F, S]] {
      override def order(x: Result[F, S], y: Result[F, S]): Ordering = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ of.order(f1, f2)
          case Success(_)  ⇒ Ordering.LT
        }
        case Success(s1) ⇒ y match {
          case Failure(_) ⇒ Ordering.GT
          case Success(s2) ⇒ os.order(s1, s2)
        }
      }
    }

  implicit def resultShow[F, S](implicit sf: Show[F], ss: Show[S]): Show[Result[F, S]] = Show.shows {
    case Failure(f) ⇒ s"Failure(${sf.show(f)})"
    case Success(s) ⇒ s"Success(${ss.show(s)})"
  }

  implicit def resultMonoid[F, S](implicit sf: Semigroup[F], ms: Monoid[S]): Monoid[Result[F, S]] =
    new Monoid[Result[F, S]] {
      override def zero = Result.success(ms.zero)

      override def append(d1: Result[F, S], d2: ⇒ Result[F, S]) = d1 match {
        case Failure(f1) ⇒ d2 match {
          case Failure(f2) ⇒ Result.failure(sf.append(f1, f2))
          case Success(_)  ⇒ d1
        }
        case Success(s1) ⇒ d2 match {
          case Failure(_)  ⇒ d2
          case Success(s2) ⇒ Result.success(ms.append(s1, s2))
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
