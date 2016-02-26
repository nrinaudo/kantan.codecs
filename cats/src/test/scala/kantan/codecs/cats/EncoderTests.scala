package kantan.codecs.cats

import cats.Eq
import cats.laws.discipline.ContravariantTests
import kantan.codecs.laws.discipline.SimpleEncoder
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.laws.discipline.equality._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EncoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def simpleEncoderEq[A: Arbitrary]: Eq[SimpleEncoder[A]] = new Eq[SimpleEncoder[A]] {
    override def eqv(a1: SimpleEncoder[A], a2: SimpleEncoder[A]): Boolean =
      simpleEncoderEquals(a1, a2)
   }
  implicit val cv = encoderContravariant[String, SimpleEncoder]

  checkAll("Encoder[String, Int]", ContravariantTests[SimpleEncoder].contravariant[Int, Int, Int])
}
