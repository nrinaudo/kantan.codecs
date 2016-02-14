package kantan.codecs.cats

import cats.Eq
import kantan.codecs.DecodeResult
import kantan.codecs.laws.discipline.arbitrary._
import cats.laws.discipline.{BifunctorTests, CartesianTests, MonadTests}
import cats.std.int._
import cats.std.boolean._
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
  implicit val test = CartesianTests.Isomorphisms.invariant[DecodeResult[Boolean, ?]]

  checkAll("DecodeResult[Boolean, ?]", MonadTests[DecodeResult[Boolean, ?]].monad[Int, Int, Int])
  checkAll("DecodeResult", BifunctorTests[DecodeResult].bifunctor[Int, Int, Int, Int, Int, Int])
}
