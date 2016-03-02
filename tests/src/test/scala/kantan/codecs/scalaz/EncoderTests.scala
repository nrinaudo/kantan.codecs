package kantan.codecs.scalaz

import kantan.codecs.arbitrary._
import kantan.codecs.scalaz.laws._
import kantan.codecs.simple._

import _root_.scalaz.scalacheck.ScalazProperties.contravariant
import scalaz.Scalaz._

class EncoderTests extends ScalazSuite {
  checkAll("Encoder", contravariant.laws[SimpleEncoder])
}
