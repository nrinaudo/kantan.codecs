package kantan.codecs.scalaz

import kantan.codecs.DecodeResult
import kantan.codecs.DecodeResult.{Success, Failure}

import scalaz.{Semigroup, Equal}

trait LowPriorityInstances {
  implicit def decodeResultEqual[F, S](implicit ef: Equal[F], es: Equal[S]): Equal[DecodeResult[F, S]] =
    new Equal[DecodeResult[F, S]] {
      override def equal(x: DecodeResult[F, S], y: DecodeResult[F, S]) = x match {
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

  implicit def decodeResultSemigroup[F, S](implicit sf: Semigroup[F], ss: Semigroup[S]): Semigroup[DecodeResult[F, S]] =
    new Semigroup[DecodeResult[F, S]] {
      override def append(x: DecodeResult[F, S], y: ⇒ DecodeResult[F, S]) = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ DecodeResult.Failure(sf.append(f1, f2))
          case Success(_)  ⇒ x
        }
        case Success(s1) ⇒ y match {
          case Failure(f)  ⇒ y
          case Success(s2) ⇒ DecodeResult.Success(ss.append(s1, s2))
        }
      }
    }
}
