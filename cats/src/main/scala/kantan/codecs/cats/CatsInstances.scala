package kantan.codecs.cats

import cats._
import cats.functor.{Contravariant, Bifunctor}
import kantan.codecs.{Encoder, Decoder, Result}
import kantan.codecs.Result.{Failure, Success}

trait CatsInstances extends LowPriorityCatsInstances {
  implicit def decoderFunctor[E, F, T]: Functor[Decoder[E, ?, F, T]] = new Functor[Decoder[E, ?, F, T]] {
    override def map[A, B](fa: Decoder[E, A, F, T])(f: A ⇒ B) = fa.map(f)
  }

  implicit def encoderContravariant[E, T]: Contravariant[Encoder[E, ?, T]] = new Contravariant[Encoder[E, ?, T]] {
    override def contramap[A, B](fa: Encoder[E, A, T])(f: B ⇒ A) = fa.contramap(f)
  }

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

  implicit def resultShow[F, S](implicit sf: Show[F], ss: Show[S]): Show[Result[F, S]] =
    new Show[Result[F, S]] {
      override def show(f: Result[F, S]) = f match {
        case Failure(f) ⇒ s"Failure(${sf.show(f)})"
        case Success(s) ⇒ s"Success(${ss.show(s)})"
      }
    }

  implicit def resultMonoid[F, S](implicit sf: Semigroup[F], ms: Monoid[S]) = new Monoid[Result[F, S]] {
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
      def traverse[G[_], S1, S2](fa: Result[F, S1])(f: S1 => G[S2])(implicit ag: Applicative[G]): G[Result[F, S2]] = fa match {
        case f@Failure(_) ⇒ ag.pure(f)
        case Success(s)   ⇒ ag.map(f(s))(Result.success)
      }

      override def flatMap[A, B](fa: Result[F, A])(f: A => Result[F, B])= fa.flatMap(f)
      override def pure[A](a: A) = Result.success(a)
    }

  implicit def resultBiFunctor = new Bifunctor[Result] {
    override def bimap[A, B, C, D](fab: Result[A, B])(f: A => C, g: B => D): Result[C, D] =
      fab.bimap(f, g)
  }
}
