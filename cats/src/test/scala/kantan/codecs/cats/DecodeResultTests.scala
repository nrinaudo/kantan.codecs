package kantan.codecs.cats

import algebra.laws.{GroupLaws, OrderLaws}
import cats.Eq
import cats.data.NonEmptyList
import kantan.codecs.DecodeResult
import kantan.codecs.laws.discipline.arbitrary._
import cats.laws.discipline.{TraverseTests, BifunctorTests, CartesianTests, MonadTests}
import cats.laws.discipline.arbitrary._
import cats.std.all._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DecodeResultTests  extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // TODO: surely this instance is available by default in cats? Couldn't find it though.
  implicit def eqTuple3[A1: Eq, A2: Eq, A3: Eq] = new Eq[(A1, A2, A3)] {
    override def eqv(x: (A1, A2, A3), y: (A1, A2, A3)): Boolean =
    x._1 === y._1 && x._2 === y._2 && x._3 === y._3
  }

  // TODO: not sure why this isn't resolved automatically. Investigate.
  implicit val test = CartesianTests.Isomorphisms.invariant[DecodeResult[String, ?]]

  checkAll("DecodeResult[String, ?]", MonadTests[DecodeResult[String, ?]].monad[Int, Int, Int])
  checkAll("DecodeResult[?, ?]", BifunctorTests[DecodeResult].bifunctor[Int, Int, Int, Int, Int, Int])
  checkAll("DecodeResult[String, ?]", TraverseTests[DecodeResult[String, ?]].traverse[Int, Int, Int, Int, Option, Option])
  checkAll("DecodeResult[Int, String]", OrderLaws[DecodeResult[Int, String]].order)
  checkAll("DecodeResult[String, Int]", GroupLaws[DecodeResult[String, Int]].monoid)
  checkAll("DecodeResult[String, NonEmptyList[Int]]", GroupLaws[DecodeResult[String, NonEmptyList[Int]]].semigroup)
}
