package kantan.codecs.scalaz

import laws.discipline._
import kantan.codecs.strings.StringEncoder

import _root_.scalaz.scalacheck.ScalazProperties.contravariant
import scalaz.Scalaz._

class EncoderTests extends ScalazSuite {
  checkAll("StringEncoder", contravariant.laws[StringEncoder])
}
