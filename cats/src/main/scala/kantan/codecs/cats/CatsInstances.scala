package kantan.codecs.cats

import cats._
import cats.functor.Bifunctor
import kantan.codecs.DecodeResult
import kantan.codecs.DecodeResult.{Success, Failure}

trait CatsInstances extends LowPriorityCatsInstances {
  implicit def decodeResultOrder[F, S](implicit of: Order[F], os: Order[S]): Order[DecodeResult[F, S]] = new Order[DecodeResult[F, S]] {
    override def compare(x: DecodeResult[F, S], y: DecodeResult[F, S]): Int = x match {
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

  implicit def decodeResultShow[F, S](implicit sf: Show[F], ss: Show[S]): Show[DecodeResult[F, S]] =
    new Show[DecodeResult[F, S]] {
      override def show(f: DecodeResult[F, S]) = f match {
        case Failure(f) ⇒ s"Failure(${sf.show(f)})"
        case Success(s) ⇒ s"Success(${ss.show(s)})"
      }
    }

  implicit def decodeResultMonoid[F, S](implicit sf: Semigroup[F], ms: Monoid[S]) = new Monoid[DecodeResult[F, S]] {
    override def empty = DecodeResult.success(ms.empty)
    override def combine(x: DecodeResult[F, S], y: DecodeResult[F, S]) = x match {
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

  implicit def decodeResultInstances[F]: Traverse[DecodeResult[F, ?]] with Monad[DecodeResult[F, ?]] =
    new Traverse[DecodeResult[F, ?]] with Monad[DecodeResult[F, ?]] {
      def foldLeft[A, B](fa: DecodeResult[F, A], b: B)(f: (B, A) => B): B = fa.foldLeft(b)(f)
      def foldRight[A, B](fa: DecodeResult[F, A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        fa.foldRight(lb)(f)
      def traverse[G[_], S1, S2](fa: DecodeResult[F, S1])(f: S1 => G[S2])(implicit ag: Applicative[G]): G[DecodeResult[F, S2]] = fa match {
        case f@Failure(_) ⇒ ag.pure(f)
        case Success(s)   ⇒ ag.map(f(s))(DecodeResult.success)
      }

      override def flatMap[A, B](fa: DecodeResult[F, A])(f: A => DecodeResult[F, B])= fa.flatMap(f)
      override def pure[A](a: A) = DecodeResult.success(a)
    }

  implicit def decodeResultBiFunctor = new Bifunctor[DecodeResult] {
    override def bimap[A, B, C, D](fab: DecodeResult[A, B])(f: A => C, g: B => D): DecodeResult[C, D] =
      fab.bimap(f, g)
  }
}
