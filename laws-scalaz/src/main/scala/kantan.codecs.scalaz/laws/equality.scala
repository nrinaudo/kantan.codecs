package kantan.codecs.scalaz.laws

import kantan.codecs.scalaz._
import kantan.codecs.{Decoder, Encoder, Result}
import org.scalacheck.Arbitrary

import _root_.scalaz.Equal

object equality {
  implicit def decoderEqual[E: Arbitrary, D: Equal, F: Equal, R[DD] <: Decoder[E, DD, F, R]]: Equal[R[D]] = new Equal[R[D]] {
    override def equal(a1: R[D], a2: R[D]) =
      kantan.codecs.laws.discipline.equality.eq(a1.decode, a2.decode) { (d1, d2) ⇒
        implicitly[Equal[Result[F, D]]].equal(d1, d2)
      }
  }

  implicit def encoderEqual[E: Equal, D: Arbitrary, R[DD] <: Encoder[E, DD, R]]: Equal[R[D]] = new Equal[R[D]] {
    override def equal(a1: R[D], a2: R[D]) =
      kantan.codecs.laws.discipline.equality.eq(a1.encode, a2.encode) { (e1, e2) ⇒
        implicitly[Equal[E]].equal(e1, e2)
      }
  }
}
