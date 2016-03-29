package kantan.codecs.cats

import cats._
import kantan.codecs.Result
import kantan.codecs.Result.{Failure, Success}

trait LowPriorityCatsInstances {
  implicit def resultEq[F, S](implicit ef: Eq[F], es: Eq[S]): Eq[Result[F, S]] = new Eq[Result[F, S]] {
    override def eqv(x: Result[F, S], y: Result[F, S]): Boolean = x match {
      case Failure(f1) ⇒ y match {
        case Failure(f2) ⇒ ef.eqv(f1, f2)
        case Success(_)  ⇒ false
      }
      case Success(s1) ⇒ y match {
        case Failure(_) ⇒ false
        case Success(s2) ⇒ es.eqv(s1, s2)
      }
    }
  }

  implicit def resultSemigroup[F, S](implicit sf: Semigroup[F], ss: Semigroup[S]): Semigroup[Result[F, S]] =
    new Semigroup[Result[F, S]] {
      override def combine(x: Result[F, S], y: Result[F, S]) = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ Result.Failure(sf.combine(f1, f2))
          case Success(_)  ⇒ x
        }
        case Success(s1) ⇒ y match {
          case Failure(f)  ⇒ y
          case Success(s2) ⇒ Result.Success(ss.combine(s1, s2))
        }
      }
    }
}
