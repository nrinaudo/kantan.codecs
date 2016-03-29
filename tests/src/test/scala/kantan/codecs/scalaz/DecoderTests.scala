package kantan.codecs.scalaz

import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.scalaz.laws.discipline.equality._
import kantan.codecs.strings.StringDecoder
import scalaz.scalacheck.ScalazProperties._
import scalaz.std.anyVal._

class DecoderTests extends ScalazSuite {
  checkAll("StringDecoder", functor.laws[StringDecoder])
}
