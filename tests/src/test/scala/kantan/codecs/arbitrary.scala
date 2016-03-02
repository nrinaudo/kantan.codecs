package kantan.codecs

import java.io.FileNotFoundException

import kantan.codecs.simple.{SimpleEncoder, SimpleDecoder}
import org.scalacheck.{Gen, Arbitrary}
import kantan.codecs.laws.discipline.arbitrary._

import scala.util.{Success, Failure, Try}

/** Various useful arbitrary instances. We might want to split them into a different project at some point. */
object arbitrary {
  implicit val arbException: Arbitrary[Exception] = Arbitrary(Gen.oneOf(
    new NullPointerException,
    new FileNotFoundException("file not found"),
    new IllegalArgumentException,
    new IllegalStateException("illegal state")
  ))

  implicit def arbTry[A](implicit aa: Arbitrary[A]): Arbitrary[Try[A]] =
    Arbitrary(Gen.oneOf(Arbitrary.arbitrary[Exception].map(Failure.apply), aa.arbitrary.map(Success.apply)))

  implicit def arbSimpleEncoder[A: Arbitrary]: Arbitrary[SimpleEncoder[A]] =
    Arbitrary(Arbitrary.arbitrary[A ⇒ String].map(f ⇒ SimpleEncoder(f)))

  implicit def arbSimpleDecoder[A: Arbitrary]: Arbitrary[SimpleDecoder[A]] =
    Arbitrary(Arbitrary.arbitrary[String ⇒ Result[Boolean, A]].map(f ⇒ SimpleDecoder(f)))
}
