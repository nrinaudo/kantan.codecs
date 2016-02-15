package kantan.codecs.scalaz

import kantan.codecs.DecodeResult
import kantan.codecs.laws.discipline.arbitrary._

import scalaz.NonEmptyList
import scalaz.scalacheck.ScalazArbitrary._
import scalaz.scalacheck.ScalazProperties._
import scalaz.std.anyVal._
import scalaz.std.string._


class DecodeResultTests extends ScalazSuite {
  checkAll("DecodeResult[String, ?]", traverse.laws[DecodeResult[String, ?]])
  checkAll("DecodeResult[String, ?]", monad.laws[DecodeResult[String, ?]])
  checkAll("DecodeResult[String, Int]", equal.laws[DecodeResult[String, Int]])
  checkAll("DecodeResult[String, Int]", monoid.laws[DecodeResult[String, Int]])
  checkAll("DecodeResult[String, NonEmptyList[Int]]", semigroup.laws[DecodeResult[String, NonEmptyList[Int]]])
}
