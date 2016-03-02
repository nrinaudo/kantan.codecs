package kantan.codecs.cats

import cats.laws.discipline.ContravariantTests
import cats.std.all._
import kantan.codecs.arbitrary._
import kantan.codecs.cats.laws._
import kantan.codecs.simple._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EncoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Encoder[String, Int]", ContravariantTests[SimpleEncoder].contravariant[Int, Int, Int])
}
