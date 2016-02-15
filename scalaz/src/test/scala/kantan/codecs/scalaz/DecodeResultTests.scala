package kantan.codecs.scalaz

import kantan.codecs.DecodeResult
import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary

import scalaz.scalacheck.ScalazArbitrary._
import scalaz.scalacheck.ScalazProperties._
import scalaz.std.anyVal._
import scalaz.std.string._
import scalaz.{Equal, NonEmptyList}

class DecodeResultTests extends ScalazSuite {
  case class NoOrder(value: Int)
  implicit val noOrderEqual = new Equal[NoOrder] {
    override def equal(a1: NoOrder, a2: NoOrder): Boolean = a1.value == a2.value
  }
  implicit val arbNoEqual = Arbitrary(Arbitrary.arbitrary[Int].map(NoOrder.apply))


  checkAll("DecodeResult[String, Int]", order.laws[DecodeResult[String, Int]])
  checkAll("DecodeResult[String, Int]", equal.laws[DecodeResult[String, NoOrder]])
  checkAll("DecodeResult[String, NonEmptyList[Int]]", semigroup.laws[DecodeResult[String, NonEmptyList[Int]]])
  checkAll("DecodeResult[String, Int]", monoid.laws[DecodeResult[String, Int]])
  checkAll("DecodeResult[String, ?]", monad.laws[DecodeResult[String, ?]])
  checkAll("DecodeResult[String, ?]", traverse.laws[DecodeResult[String, ?]])
  checkAll("DecodeResult[?, ?]", bitraverse.laws[DecodeResult[?, ?]])
}
