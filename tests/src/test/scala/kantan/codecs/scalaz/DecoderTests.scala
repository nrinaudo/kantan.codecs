package kantan.codecs.scalaz

import kantan.codecs.strings.StringDecoder

import laws.discipline._
import _root_.scalaz.scalacheck.ScalazProperties._
import scalaz.std.anyVal._

class DecoderTests extends ScalazSuite {
  checkAll("StringDecoder", functor.laws[StringDecoder])
}
