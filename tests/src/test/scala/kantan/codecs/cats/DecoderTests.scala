package kantan.codecs.cats

import cats.Eq
import cats.std.all._
import cats.laws.discipline.FunctorTests
import kantan.codecs.laws.discipline.SimpleDecoder
import kantan.codecs.cats.laws.arbitrary._
import kantan.codecs.cats.laws.equality._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def simpleDecoderEq[A: Arbitrary: Eq] = decoderEq[String, A, Boolean, SimpleDecoder]
  implicit val functor = decoderFunctor[String, Boolean, SimpleDecoder]

  checkAll("Decoder[String, Int, Boolean]", FunctorTests[SimpleDecoder].functor[Int, Int, Int])
}
