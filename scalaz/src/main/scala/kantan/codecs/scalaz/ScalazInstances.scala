package kantan.codecs.scalaz

import kantan.codecs.DecodeResult
import kantan.codecs.DecodeResult.{Success, Failure}

import scalaz._

trait ScalazInstances extends LowPriorityScalazInstances {
  implicit def decodeResultOrder[F, S](implicit of: Order[F], os: Order[S]): Order[DecodeResult[F, S]] =
    new Order[DecodeResult[F, S]] {
      override def order(x: DecodeResult[F, S], y: DecodeResult[F, S]): Ordering = x match {
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

  implicit def decodeResultShow[F, S](implicit sf: Show[F], ss: Show[S]): Show[DecodeResult[F, S]] =
    new Show[DecodeResult[F, S]] {
      override def shows(d: DecodeResult[F, S]) = d match {
        case Failure(f) ⇒ s"Failure(${sf.show(f)})"
        case Success(s) ⇒ s"Success(${ss.show(s)})"
      }
    }

  implicit def decodeResultMonoid[F, S](implicit sf: Semigroup[F], ms: Monoid[S]): Monoid[DecodeResult[F, S]] =
    new Monoid[DecodeResult[F, S]] {
      override def zero = DecodeResult.success(ms.zero)

      override def append(d1: DecodeResult[F, S], d2: ⇒ DecodeResult[F, S]) = d1 match {
        case Failure(f1) ⇒ d2 match {
          case Failure(f2) ⇒ DecodeResult.failure(sf.append(f1, f2))
          case Success(_)  ⇒ d1
        }
        case Success(s1) ⇒ d2 match {
          case Failure(_)  ⇒ d2
          case Success(s2) ⇒ DecodeResult.success(ms.append(s1, s2))
        }
      }
    }

  implicit def decodeResultInstances[F]: Monad[DecodeResult[F, ?]] with Traverse[DecodeResult[F, ?]] =
    new Monad[DecodeResult[F, ?]] with Traverse[DecodeResult[F, ?]] {
      override def traverseImpl[G[_], S, SS](d: DecodeResult[F, S])(f: S ⇒ G[SS])(implicit ag: Applicative[G]) = d match {
        case f@Failure(_) ⇒ ag.pure(f)
        case Success(s)   ⇒ ag.map(f(s))(DecodeResult.success)
      }
      override def bind[S, SS](d: DecodeResult[F, S])(f: S ⇒ DecodeResult[F, SS]) = d.flatMap(f)
      override def point[S](s: ⇒ S) = DecodeResult.success(s)
      override def map[S, SS](r: DecodeResult[F, S])(f: S ⇒ SS) = r.map(f)
      override def foldLeft[S, A](r: DecodeResult[F, S], z: A)(f: (A, S) ⇒ A) = r.foldLeft(z)(f)
    }

  implicit def decodeResultBitraverse = new Bitraverse[DecodeResult] {
    override def bitraverseImpl[G[_], A, B, C, D](fab: DecodeResult[A, B])(f: A => G[C], g: B => G[D])(implicit ag: Applicative[G])=
      fab match {
        case Failure(a) => Functor[G].map(f(a))(Failure.apply)
        case Success(b) => Functor[G].map(g(b))(Success.apply)
      }
    override def bimap[A, B, C, D](fab: DecodeResult[A, B])(f: A => C, g: B => D) = fab.bimap(f, g)
  }
}
