package kantan.codecs.scalaz

import kantan.codecs.simple._
import kantan.codecs.arbitrary._
import kantan.codecs.scalaz.laws._

import _root_.scalaz.scalacheck.ScalazProperties.contravariant
import scalaz.Scalaz._
import scalaz._

class EncoderTests extends ScalazSuite {
  implicit val simpleEncoderEqual = encoderEqual[String, Int, Simple]
  implicit val cv = encoderContravariant[String, Simple]

  checkAll("Encoder", contravariant.laws[SimpleEncoder])
}
