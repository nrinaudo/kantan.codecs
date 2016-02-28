package kantan.codecs.scalaz

import kantan.codecs.laws.discipline.SimpleEncoder
import kantan.codecs.scalaz.laws.arbitrary._
import kantan.codecs.scalaz.laws.equality._

import scalaz._, Scalaz._
import _root_.scalaz.scalacheck.ScalazProperties.contravariant

class EncoderTests extends ScalazSuite {
  implicit val simpleEncoderEqual = encoderEqual[String, Int, SimpleEncoder]
  implicit val cv = encoderContravariant[String, SimpleEncoder]

  checkAll("Encoder", contravariant.laws[SimpleEncoder])
}
