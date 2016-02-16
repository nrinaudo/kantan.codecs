package kantan.codecs.cats

import cats._
import kantan.codecs.DecodeResult
import kantan.codecs.DecodeResult.{Success, Failure}

trait LowPriorityCatsInstances {
  implicit def decodeResultEq[F, S](implicit ef: Eq[F], es: Eq[S]): Eq[DecodeResult[F, S]] = new Eq[DecodeResult[F, S]] {
    override def eqv(x: DecodeResult[F, S], y: DecodeResult[F, S]): Boolean = x match {
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
