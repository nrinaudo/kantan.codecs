package kantan.codecs.scalaz

import kantan.codecs.laws.discipline.SimpleDecoder
import kantan.codecs.laws.discipline.equality._
import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.functor

class DecoderTests extends ScalazSuite {
  implicit def simpleDecoderEqual[A: Arbitrary]: Equal[SimpleDecoder[A]] = new Equal[SimpleDecoder[A]] {
    override def equal(a1: SimpleDecoder[A], a2: SimpleDecoder[A]): Boolean =
      simpleDecoderEquals(a1, a2)
  }
  implicit val fct = decoderFunctor[String, Boolean, SimpleDecoder]

  checkAll("SimpleDecoder", functor.laws[SimpleDecoder])
}
