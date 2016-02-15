package kantan.codecs.scalaz

import kantan.codecs.DecodeResult
import kantan.codecs.laws.discipline.arbitrary._

import scalaz.scalacheck.ScalazProperties.{traverse, equal, monad}
import scalaz.std.anyVal._
import scalaz.std.string._

class DecodeResultTests extends ScalazSuite {
  checkAll("DecodeResult[String, ?]", traverse.laws[DecodeResult[String, ?]])
  checkAll("DecodeResult[String, ?]", monad.laws[DecodeResult[String, ?]])
  checkAll("DecodeResult[String, Int]", equal.laws[DecodeResult[String, Int]])
}
