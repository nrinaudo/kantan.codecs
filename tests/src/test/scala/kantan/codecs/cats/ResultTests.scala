package kantan.codecs.cats

import algebra.laws.{GroupLaws, OrderLaws}
import cats.laws.discipline.eq._
import cats.data.NonEmptyList
import kantan.codecs.Result
import kantan.codecs.cats.laws._
import cats.laws.discipline.{TraverseTests, BifunctorTests, CartesianTests, MonadTests}
import cats.laws.discipline.arbitrary._
import cats.std.all._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ResultTests  extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // TODO: not sure why this isn't resolved automatically. Investigate.
  implicit val test = CartesianTests.Isomorphisms.invariant[Result[String, ?]]

  checkAll("Result[String, ?]", MonadTests[Result[String, ?]].monad[Int, Int, Int])
  checkAll("Result[?, ?]", BifunctorTests[Result].bifunctor[Int, Int, Int, Int, Int, Int])
  checkAll("Result[String, ?]", TraverseTests[Result[String, ?]].traverse[Int, Int, Int, Int, Option, Option])
  checkAll("Result[Int, String]", OrderLaws[Result[Int, String]].order)
  checkAll("Result[String, Int]", GroupLaws[Result[String, Int]].monoid)
  checkAll("Result[String, NonEmptyList[Int]]", GroupLaws[Result[String, NonEmptyList[Int]]].semigroup)
}
