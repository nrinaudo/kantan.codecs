package kantan.codecs.cats

import cats.Eq
import kantan.codecs.{Encoder, Decoder, Result}
import org.scalacheck.Arbitrary

package object laws extends ArbitraryInstances {
  implicit def decoderEq[E: Arbitrary, D: Eq, F: Eq, T]: Eq[Decoder[E, D, F, T]]= new Eq[Decoder[E, D, F, T]] {
    override def eqv(a1: Decoder[E, D, F, T], a2: Decoder[E, D, F, T]) =
      kantan.codecs.laws.discipline.equality.eq(a1.decode, a2.decode) { (d1, d2) ⇒
        implicitly[Eq[Result[F, D]]].eqv(d1, d2)
      }
  }

  implicit def encoderEq[E: Eq, D: Arbitrary, T]: Eq[Encoder[E, D, T]] = new Eq[Encoder[E, D, T]] {
    override def eqv(a1: Encoder[E, D, T], a2: Encoder[E, D, T]) =
      kantan.codecs.laws.discipline.equality.eq(a1.encode, a2.encode) { (e1, e2) ⇒
        implicitly[Eq[E]].eqv(e1, e2)
      }
  }
}
