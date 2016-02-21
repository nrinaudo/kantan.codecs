package kantan.codecs.laws.discipline

import kantan.codecs.Result
import kantan.codecs.Result.{Failure, Success}
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.laws._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.{Arbitrary, Gen}

import scala.util.Try

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances extends ArbitraryArities {
  // - Arbitrary results -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def success[S: Arbitrary]: Gen[Success[S]] =
    Arbitrary.arbitrary[S].map(Success.apply)
  def failure[F: Arbitrary]: Gen[Failure[F]] =
    Arbitrary.arbitrary[F].map(Failure.apply)

  implicit def arbSuccess[S: Arbitrary]: Arbitrary[Success[S]] = Arbitrary(success)
  implicit def arbFailure[F: Arbitrary]: Arbitrary[Failure[F]] = Arbitrary(failure)

  implicit def arbResult[F: Arbitrary, S: Arbitrary]: Arbitrary[Result[F, S]] =
    Arbitrary(Gen.oneOf(success[S], failure[F]))



  // - Arbitrary codec values ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def genLegal[E, D: Arbitrary](f: D ⇒ E): Gen[LegalValue[E, D]] = arb[D].map(d ⇒ LegalValue(f(d), d))
  def genIllegal[E: Arbitrary, D](f: E ⇒ Boolean): Gen[IllegalValue[E, D]] =
    arb[E].suchThat(e ⇒ f(e)).map(IllegalValue.apply)

  def genIllegalFromException[E: Arbitrary, D](f: E ⇒ D): Gen[IllegalValue[E, D]] =
    genIllegal(e ⇒ Try(f(e)).isFailure)

  def genLegalOption[E, D](f: E ⇒ Boolean)(empty: E)(implicit dl: Arbitrary[LegalValue[E, D]]): Gen[LegalValue[E, Option[D]]] =
    arb[Option[LegalValue[E, D]]]
      .suchThat(_.map(v ⇒ !f(v.encoded)).getOrElse(true))
      .map(_.map(l ⇒ l.copy(decoded = Option(l.decoded))).getOrElse(LegalValue(empty, None)))

  implicit def genIllegalOption[E, D](f: E ⇒ Boolean)(implicit dl: Arbitrary[IllegalValue[E, D]]): Gen[IllegalValue[E, Option[D]]] =
    arb[IllegalValue[E, D]].suchThat(v ⇒ !f(v.encoded)).map(d ⇒ IllegalValue(d.encoded))

}