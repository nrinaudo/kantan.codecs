package kantan.codecs.cats

import cats.laws.discipline.ContravariantTests
import cats.std.all._
import kantan.codecs.cats.laws.discipline.equality._
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.strings.StringEncoder
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EncoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringEncoder", ContravariantTests[StringEncoder].contravariant[Int, Int, Int])
}
