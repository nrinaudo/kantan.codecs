package kantan.codecs.laws

import kantan.codecs.Result
import kantan.codecs.laws.discipline.{EncoderTests, DecoderTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EitherCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val decoder: SimpleDecoder[Either[Int, Boolean]] =
    SimpleDecoder(s ⇒ Result.nonFatal(Left(s.toInt)).leftFlatMap(_ ⇒ Result.nonFatal(Right(s.toBoolean): Either[Int, Boolean])).leftMap(_ ⇒ true))
  implicit val encoder = SimpleEncoder[Either[Int, Boolean]] { _ match {
    case Left(l) ⇒ l.toString
    case Right(r) ⇒ r.toString
  }}

  checkAll("Decoder[String, Int, Boolean]", DecoderTests[String, Either[Int, Boolean], Boolean, SimpleDecoder].decoder[Int, Int])
  checkAll("Encoder[String, Int]", EncoderTests[String, Either[Int, Boolean], SimpleEncoder].encoder[Int, Int])
}
