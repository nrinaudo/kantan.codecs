package kantan.codecs.scalaz

import kantan.codecs.laws.discipline.{equality, SimpleEncoder}
import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.contravariant

object EncoderTests extends ScalazSuite {
  implicit def simpleEncoderEq[A: Arbitrary]: Equal[SimpleEncoder[A]] = new Equal[SimpleEncoder[A]] {
    override def equal(a1: SimpleEncoder[A], a2: SimpleEncoder[A]): Boolean =
      equality.simpleEncoderEquals(a1, a2)
  }

  implicit val cv = encoderContravariant[String, SimpleEncoder]

  checkAll("Encoder", contravariant.laws[SimpleEncoder])
}
