package kantan.codecs.scalaz

import kantan.codecs.Result
import kantan.codecs.Result.{Success, Failure}

import scalaz.{Semigroup, Equal}

trait LowPriorityScalazInstances {
  implicit def resultEqual[F, S](implicit ef: Equal[F], es: Equal[S]): Equal[Result[F, S]] =
    new Equal[Result[F, S]] {
      override def equal(x: Result[F, S], y: Result[F, S]) = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ ef.equal(f1, f2)
          case Success(_)  ⇒ false
        }
        case Success(s1) ⇒ y match {
          case Failure(_) ⇒ false
          case Success(s2) ⇒ es.equal(s1, s2)
        }
      }
    }

  implicit def resultSemigroup[F, S](implicit sf: Semigroup[F], ss: Semigroup[S]): Semigroup[Result[F, S]] =
    new Semigroup[Result[F, S]] {
      override def append(x: Result[F, S], y: ⇒ Result[F, S]) = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ Result.Failure(sf.append(f1, f2))
          case Success(_)  ⇒ x
        }
        case Success(s1) ⇒ y match {
          case Failure(f)  ⇒ y
          case Success(s2) ⇒ Result.Success(ss.append(s1, s2))
        }
      }
    }
}
