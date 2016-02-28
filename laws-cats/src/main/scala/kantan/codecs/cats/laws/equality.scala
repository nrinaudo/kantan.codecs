package kantan.codecs.cats.laws

import cats.Eq
import kantan.codecs.{Encoder, Decoder, Result}
import org.scalacheck.Arbitrary
import kantan.codecs.cats._

object equality {
  implicit def decoderEq[E: Arbitrary, D: Eq, F: Eq, R[DD] <: Decoder[E, DD, F, R]]: Eq[R[D]] = new Eq[R[D]] {
    override def eqv(a1: R[D], a2: R[D]) =
      kantan.codecs.laws.discipline.equality.eq(a1.decode, a2.decode) { (d1, d2) ⇒
        implicitly[Eq[Result[F, D]]].eqv(d1, d2)
      }
  }

  implicit def encoderEq[E: Eq, D: Arbitrary, R[DD] <: Encoder[E, DD, R]]: Eq[R[D]] = new Eq[R[D]] {
    override def eqv(a1: R[D], a2: R[D]) =
      kantan.codecs.laws.discipline.equality.eq(a1.encode, a2.encode) { (e1, e2) ⇒
        implicitly[Eq[E]].eqv(e1, e2)
      }
  }
}
