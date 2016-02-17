package kantan.codecs.laws.discipline

import kantan.codecs.Result
import kantan.codecs.Result.{Failure, Success}
import org.scalacheck.{Gen, Arbitrary}

object arbitrary {
  def success[S: Arbitrary]: Gen[Success[S]] =
    Arbitrary.arbitrary[S].map(Success.apply)
  def failure[F: Arbitrary]: Gen[Failure[F]] =
    Arbitrary.arbitrary[F].map(Failure.apply)

  implicit def arbSuccess[S: Arbitrary]: Arbitrary[Success[S]] = Arbitrary(success)
  implicit def arbFailure[F: Arbitrary]: Arbitrary[Failure[F]] = Arbitrary(failure)

  implicit def arbResult[F: Arbitrary, S: Arbitrary]: Arbitrary[Result[F, S]] =
    Arbitrary(Gen.oneOf(success[S], failure[F]))
}
