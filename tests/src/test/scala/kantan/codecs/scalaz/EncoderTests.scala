package kantan.codecs.scalaz

import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.scalaz.laws.discipline.equality._
import kantan.codecs.strings.StringEncoder

import scalaz.Scalaz._
import scalaz.scalacheck.ScalazProperties.contravariant

class EncoderTests extends ScalazSuite {
  checkAll("StringEncoder", contravariant.laws[StringEncoder])
}
