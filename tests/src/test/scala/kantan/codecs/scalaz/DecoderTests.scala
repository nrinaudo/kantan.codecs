package kantan.codecs.scalaz

import kantan.codecs.arbitrary._
import kantan.codecs.scalaz.laws._
import kantan.codecs.simple._

import _root_.scalaz.scalacheck.ScalazProperties._
import scalaz.std.anyVal._

class DecoderTests extends ScalazSuite {
  checkAll("SimpleDecoder", functor.laws[SimpleDecoder])
}
