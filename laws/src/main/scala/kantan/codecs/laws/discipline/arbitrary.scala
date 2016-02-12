package kantan.codecs.laws.discipline

import kantan.codecs.DecodeResult
import org.scalacheck.{Gen, Arbitrary}

object arbitrary {
  def success[S: Arbitrary]: Gen[DecodeResult[Nothing, S]] =
    Arbitrary.arbitrary[S].map(DecodeResult.success)
  def failure[F: Arbitrary]: Gen[DecodeResult[F, Nothing]] =
    Arbitrary.arbitrary[F].map(DecodeResult.failure)
  implicit def arbDecodeResult[F: Arbitrary, S: Arbitrary]: Arbitrary[DecodeResult[F, S]] =
    Arbitrary(Gen.oneOf(success[S], failure[F]))
}
