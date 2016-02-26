package kantan.codecs.scalaz

import kantan.codecs.{Encoder, Decoder, Result}
import kantan.codecs.Result.{Success, Failure}

import scalaz._

trait ScalazInstances extends LowPriorityScalazInstances {
  implicit def decoderFunctor[E, F, R[D] <: Decoder[E, D, F, R]]: Functor[R] = new Functor[R] {
    override def map[A, B](fa: R[A])(f: A ⇒ B): R[B] = fa.map(f)
  }

  implicit def encoderContravariant[E, R[D] <: Encoder[E, D, R]]: Contravariant[R] = new Contravariant[R] {
    override def contramap[A, B](fa: R[A])(f: B ⇒ A) = fa.contramap(f)
  }

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

  implicit def resultShow[F, S](implicit sf: Show[F], ss: Show[S]): Show[Result[F, S]] =
    new Show[Result[F, S]] {
      override def shows(d: Result[F, S]) = d match {
        case Failure(f) ⇒ s"Failure(${sf.show(f)})"
        case Success(s) ⇒ s"Success(${ss.show(s)})"
      }
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

  implicit def resultBitraverse = new Bitraverse[Result] {
    override def bitraverseImpl[G[_], A, B, C, D](fab: Result[A, B])(f: A => G[C], g: B => G[D])(implicit ag: Applicative[G])=
      fab match {
        case Failure(a) => Functor[G].map(f(a))(Failure.apply)
        case Success(b) => Functor[G].map(g(b))(Success.apply)
      }
    override def bimap[A, B, C, D](fab: Result[A, B])(f: A => C, g: B => D) = fab.bimap(f, g)
  }
}
