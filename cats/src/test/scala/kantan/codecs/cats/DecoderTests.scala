package kantan.codecs.cats

import cats._
import cats.laws.discipline.FunctorTests
import kantan.codecs.laws.discipline.equality._
import kantan.codecs.laws.discipline.{SimpleEncoder, SimpleDecoder}
import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def simpleDecoderEq[A: Arbitrary]: Eq[SimpleDecoder[A]] = new Eq[SimpleDecoder[A]] {
    override def eqv(a1: SimpleDecoder[A], a2: SimpleDecoder[A]): Boolean =
      simpleDecoderEquals(a1, a2)
  }
  implicit val functor = decoderFunctor[String, Boolean, SimpleDecoder]

  checkAll("Decoder[String, Int, Boolean]", FunctorTests[SimpleDecoder].functor[Int, Int, Int])
}
