package kantan.codecs

import _root_.cats._
import _root_.cats.functor.Bifunctor
import kantan.codecs.DecodeResult.{Failure, Success}

trait LowPriorityInstances {
  implicit def decodeResultEq[A, B](implicit ea: Eq[A], eb: Eq[B]): Eq[DecodeResult[A, B]] = new Eq[DecodeResult[A, B]] {
    override def eqv(x: DecodeResult[A, B], y: DecodeResult[A, B]): Boolean = x match {
      case Failure(a1) ⇒ y match {
        case Failure(a2) ⇒ ea.eqv(a1, a2)
        case Success(_)  ⇒ false
      }
      case Success(b1) ⇒ y match {
        case Failure(_) ⇒ false
        case Success(b2) ⇒ eb.eqv(b1, b2)
      }
    }
  }

  implicit def decodeResultSemigroup[F, S](implicit sf: Semigroup[F], ss: Semigroup[S]): Semigroup[DecodeResult[F, S]] =
    new Semigroup[DecodeResult[F, S]] {
      override def combine(x: DecodeResult[F, S], y: DecodeResult[F, S]) = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ DecodeResult.Failure(sf.combine(f1, f2))
          case Success(_)  ⇒ x
        }
        case Success(s1) ⇒ y match {
          case Failure(f)  ⇒ y
          case Success(s2) ⇒ DecodeResult.Success(ss.combine(s1, s2))
        }
      }
    }
}

package object cats extends LowPriorityInstances {
  implicit def decodeResultOrder[A, B](implicit oa: Order[A], ob: Order[B]): Order[DecodeResult[A, B]] = new Order[DecodeResult[A, B]] {
    override def compare(x: DecodeResult[A, B], y: DecodeResult[A, B]): Int = x match {
      case Failure(a1) ⇒ y match {
        case Failure(a2) ⇒ oa.compare(a1, a2)
        case Success(_)  ⇒ -1
      }
      case Success(b1) ⇒ y match {
        case Failure(_) ⇒ 1
        case Success(b2) ⇒ ob.compare(b1, b2)
      }
    }
  }

  implicit def decodeResultShow[A, B](implicit sa: Show[A], sb: Show[B]): Show[DecodeResult[A, B]] =
    new Show[DecodeResult[A, B]] {
      override def show(f: DecodeResult[A, B]) = f match {
        case Failure(a) ⇒ s"Failure(${sa.show(a)})"
        case Success(b) ⇒ s"Success(${sb.show(b)})"
      }
    }

  implicit def decodeResultMonoid[A, B](implicit sa: Semigroup[A], mb: Monoid[B]) = new Monoid[DecodeResult[A, B]] {
    override def empty = DecodeResult.success(mb.empty)
    override def combine(x: DecodeResult[A, B], y: DecodeResult[A, B]) = x match {
      case Failure(a1) ⇒ y match {
        case Failure(a2) ⇒ Failure(sa.combine(a1, a2))
        case Success(_)  ⇒ x
      }
      case Success(b1) ⇒ y match {
        case Success(b2) ⇒ Success(mb.combine(b1, b2))
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
        case Success(b)   ⇒ ag.map(f(b))(DecodeResult.success)
      }

      override def flatMap[A, B](fa: DecodeResult[F, A])(f: A => DecodeResult[F, B])= fa.flatMap(f)
      override def pure[A](a: A) = DecodeResult.success(a)
    }

  implicit def decodeResultBiFunctor = new Bifunctor[DecodeResult] {
    override def bimap[A, B, C, D](fab: DecodeResult[A, B])(f: A => C, g: B => D): DecodeResult[C, D] =
      fab.bimap(f, g)
  }
}
