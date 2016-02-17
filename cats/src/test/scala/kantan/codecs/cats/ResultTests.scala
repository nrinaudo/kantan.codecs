package kantan.codecs.cats

import algebra.laws.{GroupLaws, OrderLaws}
import cats.Eq
import cats.data.NonEmptyList
import kantan.codecs.Result
import kantan.codecs.laws.discipline.arbitrary._
import cats.laws.discipline.{TraverseTests, BifunctorTests, CartesianTests, MonadTests}
import cats.laws.discipline.arbitrary._
import cats.std.all._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ResultTests  extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // TODO: surely this instance is available by default in cats? Couldn't find it though.
  implicit def eqTuple3[A1: Eq, A2: Eq, A3: Eq] = new Eq[(A1, A2, A3)] {
    override def eqv(x: (A1, A2, A3), y: (A1, A2, A3)): Boolean =
    x._1 === y._1 && x._2 === y._2 && x._3 === y._3
  }

  // TODO: not sure why this isn't resolved automatically. Investigate.
  implicit val test = CartesianTests.Isomorphisms.invariant[Result[String, ?]]

  checkAll("Result[String, ?]", MonadTests[Result[String, ?]].monad[Int, Int, Int])
  checkAll("Result[?, ?]", BifunctorTests[Result].bifunctor[Int, Int, Int, Int, Int, Int])
  checkAll("Result[String, ?]", TraverseTests[Result[String, ?]].traverse[Int, Int, Int, Int, Option, Option])
  checkAll("Result[Int, String]", OrderLaws[Result[Int, String]].order)
  checkAll("Result[String, Int]", GroupLaws[Result[String, Int]].monoid)
  checkAll("Result[String, NonEmptyList[Int]]", GroupLaws[Result[String, NonEmptyList[Int]]].semigroup)
}
