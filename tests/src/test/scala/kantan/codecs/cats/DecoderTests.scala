package kantan.codecs.cats

import laws.discipline._
import cats.std.all._
import cats.laws.discipline.FunctorTests
import kantan.codecs.strings.StringDecoder
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder", FunctorTests[StringDecoder].functor[Int, Int, Int])
}
