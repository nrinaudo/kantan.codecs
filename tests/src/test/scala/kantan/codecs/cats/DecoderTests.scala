package kantan.codecs.cats

import cats.laws.discipline.FunctorTests
import cats.std.all._
import kantan.codecs.cats.laws.discipline.equality._
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.strings.StringDecoder
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder", FunctorTests[StringDecoder].functor[Int, Int, Int])
}
