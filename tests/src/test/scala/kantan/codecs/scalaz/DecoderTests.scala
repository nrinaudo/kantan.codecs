package kantan.codecs.scalaz

import kantan.codecs.SimpleDecoder
import kantan.codecs.scalaz.laws.equality._
import kantan.codecs.scalaz.laws.arbitrary._
import kantan.codecs.arbitrary._
import org.scalacheck.Arbitrary

import scalaz.Equal
import scalaz.std.anyVal._
import _root_.scalaz.scalacheck.ScalazProperties._

class DecoderTests extends ScalazSuite {
  implicit def simpleDecoderEqual[A: Arbitrary: Equal] = decoderEqual[String, A, Boolean, SimpleDecoder]
  implicit val fct = decoderFunctor[String, Boolean, SimpleDecoder]

  checkAll("SimpleDecoder", functor.laws[SimpleDecoder])
}
