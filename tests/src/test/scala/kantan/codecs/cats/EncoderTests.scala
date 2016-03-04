package kantan.codecs.cats

import laws.discipline._
import cats.std.all._
import cats.laws.discipline.ContravariantTests
import kantan.codecs.strings.StringEncoder
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EncoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringEncoder", ContravariantTests[StringEncoder].contravariant[Int, Int, Int])
}
