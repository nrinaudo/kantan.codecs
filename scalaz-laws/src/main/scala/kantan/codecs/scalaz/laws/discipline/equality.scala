package kantan.codecs.scalaz.laws.discipline

import _root_.scalaz.Equal
import kantan.codecs._
import kantan.codecs.scalaz._
import org.scalacheck.Arbitrary

object equality extends EqualInstances

trait EqualInstances {
  implicit def throwableEqual: Equal[Throwable] = new Equal[Throwable] {
    override def equal(a1: Throwable, a2: Throwable): Boolean = a1.getClass == a2.getClass
  }

  implicit def decoderEqual[E: Arbitrary, D: Equal, F: Equal, T]: Equal[Decoder[E, D, F, T]] =
    new Equal[Decoder[E, D, F, T]] {
      override def equal(a1: Decoder[E, D, F, T], a2: Decoder[E, D, F, T]) =
        kantan.codecs.laws.discipline.equality.eq(a1.decode, a2.decode) { (d1, d2) ⇒
          implicitly[Equal[Result[F, D]]].equal(d1, d2)
        }
    }

  implicit def encoderEqual[E: Equal, D: Arbitrary, T]: Equal[Encoder[E, D, T]] = new Equal[Encoder[E, D, T]] {
    override def equal(a1: Encoder[E, D, T], a2: Encoder[E, D, T]) =
      kantan.codecs.laws.discipline.equality.eq(a1.encode, a2.encode) { (e1, e2) ⇒
        implicitly[Equal[E]].equal(e1, e2)
      }
  }
}
