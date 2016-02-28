package kantan.codecs.cats.laws

import cats.data.Xor
import kantan.codecs.laws.CodecValue.{LegalValue, IllegalValue}
import org.scalacheck.Arbitrary

trait ArbitraryInstances extends kantan.codecs.laws.discipline.ArbitraryInstances {
  implicit def arbLegalXor[E, DL, DR](implicit a: Arbitrary[LegalValue[E, Either[DL, DR]]]): Arbitrary[LegalValue[E, DL Xor DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ Xor.fromEither(v))))

  implicit def arbIllegalXor[E, DL, DR](implicit a: Arbitrary[IllegalValue[E, Either[DL, DR]]]): Arbitrary[IllegalValue[E, DL Xor DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ Xor.fromEither(v))))
}
