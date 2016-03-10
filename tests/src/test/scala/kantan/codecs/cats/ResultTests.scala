package kantan.codecs.cats

import kantan.codecs.Result
import kantan.codecs.laws.discipline.arbitrary._

import algebra.laws.{GroupLaws, OrderLaws}
import cats._
import cats.laws.discipline.eq._
import cats.data.NonEmptyList
import cats.laws.discipline.{TraverseTests, BifunctorTests, CartesianTests, MonadTests}
import cats.laws.discipline.arbitrary._
import cats.std.all._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ResultTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case class NoOrder(value: Int)
  implicit val noOrderEq= new Eq[NoOrder] {
    override def eqv(a1: NoOrder, a2: NoOrder): Boolean = a1.value == a2.value
  }
  implicit val arbNoEqual = Arbitrary(Arbitrary.arbitrary[Int].map(NoOrder.apply))

  // TODO: not sure why this isn't resolved automatically. Investigate.
  implicit val test = CartesianTests.Isomorphisms.invariant[Result[String, ?]]

  checkAll("Result[String, ?]", MonadTests[Result[String, ?]].monad[Int, Int, Int])
  checkAll("Result[?, ?]", BifunctorTests[Result].bifunctor[Int, Int, Int, Int, Int, Int])
  checkAll("Result[String, ?]", TraverseTests[Result[String, ?]].traverse[Int, Int, Int, Int, Option, Option])
  checkAll("Result[Int, String]", OrderLaws[Result[Int, String]].order)
  checkAll("Result[Int, String]", OrderLaws[Result[NoOrder, String]].eqv)
  checkAll("Result[String, Int]", GroupLaws[Result[String, Int]].monoid)
  checkAll("Result[String, NonEmptyList[Int]]", GroupLaws[Result[String, NonEmptyList[Int]]].semigroup)

  test("Show should yield the expected result for successes") {
    forAll { i: Int ⇒ assert(Show[Result[String, Int]].show(Result.success(i)) == s"Success($i)") }
  }

  test("Show should yield the expected result for failures") {
      forAll { i: Int ⇒ assert(Show[Result[Int, String]].show(Result.failure(i)) == s"Failure($i)") }
    }
}
