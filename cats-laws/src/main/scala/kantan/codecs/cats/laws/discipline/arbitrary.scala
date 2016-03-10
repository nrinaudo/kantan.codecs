package kantan.codecs.cats.laws.discipline

import cats.data.Xor
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalacheck.Arbitrary

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances {
  implicit def arbLegalXor[E, DL, DR](implicit a: Arbitrary[LegalValue[E, Either[DL, DR]]]): Arbitrary[LegalValue[E, DL Xor DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ Xor.fromEither(v))))

  implicit def arbIllegalXor[E, DL, DR](implicit a: Arbitrary[IllegalValue[E, Either[DL, DR]]]): Arbitrary[IllegalValue[E, DL Xor DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ Xor.fromEither(v))))
}

