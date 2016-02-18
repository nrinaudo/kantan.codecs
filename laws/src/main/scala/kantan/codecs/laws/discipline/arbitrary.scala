package kantan.codecs.laws.discipline

import kantan.codecs.Result
import kantan.codecs.Result.{Failure, Success}
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.{Arbitrary, Gen}

import scala.util.Try

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances {
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
  def genIllegal[E: Arbitrary, D](f: E ⇒ D): Gen[IllegalValue[E, D]] =
    arb[E].suchThat(e ⇒ Try(f(e)).isFailure).map(IllegalValue.apply)


  def arbLegal[E, D: Arbitrary](f: D ⇒ E): Arbitrary[LegalValue[E, D]] = Arbitrary(genLegal(f))
  def arbIllegal[E: Arbitrary, D](f: E ⇒ D): Arbitrary[IllegalValue[E, D]] = Arbitrary(genIllegal(f))
}