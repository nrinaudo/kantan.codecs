package kantan.codecs.scalaz.laws.discipline

import kantan.codecs.laws.CodecValue.{LegalValue, IllegalValue}
import org.scalacheck.Arbitrary

import scalaz.{\/, Maybe}

class ArbitraryInstances extends kantan.codecs.laws.discipline.ArbitraryInstances {
  implicit def arbLegalMaybe[E, D](implicit al: Arbitrary[LegalValue[E, Option[D]]]): Arbitrary[LegalValue[E, Maybe[D]]] =
    Arbitrary(al.arbitrary.map(_.mapDecoded(v ⇒ Maybe.fromOption(v))))

  implicit def arbIllegalMaybe[E, D](implicit al: Arbitrary[IllegalValue[E, Option[D]]]): Arbitrary[IllegalValue[E, Maybe[D]]] =
      Arbitrary(al.arbitrary.map(_.mapDecoded(v ⇒ Maybe.fromOption(v))))

  implicit def arbLegalDisjunction[E, DL, DR](implicit a: Arbitrary[LegalValue[E, Either[DL, DR]]]): Arbitrary[LegalValue[E, DL \/ DR]] =
    Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ \/.fromEither(v))))

  implicit def arbIllegalDisjunction[E, DL, DR](implicit a: Arbitrary[IllegalValue[E, Either[DL, DR]]]): Arbitrary[IllegalValue[E, DL \/ DR]] =
      Arbitrary(a.arbitrary.map(_.mapDecoded(v ⇒ \/.fromEither(v))))
}
