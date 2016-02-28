package kantan.codecs.scalaz

import kantan.codecs.Result
import kantan.codecs.scalaz.laws._
import org.scalacheck.Arbitrary

import scalaz.scalacheck.ScalazArbitrary._
import scalaz.scalacheck.ScalazProperties._
import scalaz.std.anyVal._
import scalaz.std.string._
import scalaz.{Equal, NonEmptyList}

class ResultTests extends ScalazSuite {
  case class NoOrder(value: Int)
  implicit val noOrderEqual = new Equal[NoOrder] {
    override def equal(a1: NoOrder, a2: NoOrder): Boolean = a1.value == a2.value
  }
  implicit val arbNoEqual = Arbitrary(Arbitrary.arbitrary[Int].map(NoOrder.apply))


  checkAll("Result[String, Int]", order.laws[Result[String, Int]])
  checkAll("Result[String, Int]", equal.laws[Result[String, NoOrder]])
  checkAll("Result[String, NonEmptyList[Int]]", semigroup.laws[Result[String, NonEmptyList[Int]]])
  checkAll("Result[String, Int]", monoid.laws[Result[String, Int]])
  checkAll("Result[String, ?]", monad.laws[Result[String, ?]])
  checkAll("Result[String, ?]", traverse.laws[Result[String, ?]])
  checkAll("Result[?, ?]", bitraverse.laws[Result[?, ?]])
}
