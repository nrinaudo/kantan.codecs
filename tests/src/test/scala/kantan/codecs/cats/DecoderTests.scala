package kantan.codecs.cats

import cats.Eq
import cats.std.all._
import cats.laws.discipline.FunctorTests
import kantan.codecs.simple._
import kantan.codecs.arbitrary._
import kantan.codecs.cats.laws._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def simpleDecoderEq[A: Arbitrary: Eq] = decoderEq[String, A, Boolean, Simple]
  implicit val functor = decoderFunctor[String, Boolean, Simple]

  checkAll("Decoder[String, Int, Boolean]", FunctorTests[SimpleDecoder].functor[Int, Int, Int])
}
