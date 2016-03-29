package kantan.codecs.laws.discipline

import java.io.FileNotFoundException
import kantan.codecs.Result
import kantan.codecs.Result.{Failure, Success}
import kantan.codecs.strings.{StringDecoder, StringEncoder}
import org.scalacheck._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.Gen._
import scala.util.Try

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances extends ArbitraryArities {
  // - Arbitrary results -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def success[S: Arbitrary]: Gen[Success[S]] =
    Arbitrary.arbitrary[S].map(Success.apply)
  def failure[F: Arbitrary]: Gen[Failure[F]] =
    Arbitrary.arbitrary[F].map(Failure.apply)

  implicit def arbSuccess[S: Arbitrary]: Arbitrary[Success[S]] = Arbitrary(success)
  implicit def arbFailure[F: Arbitrary]: Arbitrary[Failure[F]] = Arbitrary(failure)

  implicit def arbResult[F: Arbitrary, S: Arbitrary]: Arbitrary[Result[F, S]] =
    Arbitrary(Gen.oneOf(success[S], failure[F]))



  // - Exceptions ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbException: Arbitrary[Exception] = Arbitrary(Gen.oneOf(
    new NullPointerException,
    new FileNotFoundException("file not found"),
    new IllegalArgumentException,
    new IllegalStateException("illegal state")
  ))

  implicit def arbTry[A](implicit aa: Arbitrary[A]): Arbitrary[Try[A]] =
    Arbitrary(Gen.oneOf(arb[Exception].map(scala.util.Failure.apply), aa.arbitrary.map(scala.util.Success.apply)))



  // - String codecs ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbStringEncoder[A: Arbitrary]: Arbitrary[StringEncoder[A]] =
    Arbitrary(Arbitrary.arbitrary[A ⇒ String].map(f ⇒ StringEncoder(f)))

  implicit def arbStringDecoder[A: Arbitrary]: Arbitrary[StringDecoder[A]] =
    Arbitrary(Arbitrary.arbitrary[String ⇒ Result[Throwable, A]].map(f ⇒ StringDecoder(f)))



  // This is necessary to prevent ScalaCheck from generating BigDecimal values that cannot be serialized because their
  // scale is higher than MAX_INT.
  // Note that this isn't actually an issue with ScalaCheck but with Scala itself, and is(?) fixed in Scala 2.12:
  // https://github.com/scala/scala/pull/4320
  implicit lazy val arbBigDecimal: Arbitrary[BigDecimal] = {
    import java.math.MathContext._
    val mcGen = oneOf(DECIMAL32, DECIMAL64, DECIMAL128)
    val bdGen = for {
      x ← Arbitrary.arbitrary[BigInt]
      mc ← mcGen
      limit ← const(math.max(x.abs.toString.length - mc.getPrecision, 0))
      scale ← Gen.choose(Int.MinValue + limit , Int.MaxValue)
    } yield {
      try {
        BigDecimal(x, scale, mc)
      } catch {
        // Handle the case where scale/precision conflict
        case ae: java.lang.ArithmeticException ⇒ BigDecimal(x, scale, UNLIMITED)
      }
    }
    Arbitrary(bdGen)
  }
}
