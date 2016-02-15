package kantan.codecs

import java.io.FileNotFoundException

import org.scalacheck.{Gen, Arbitrary}

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
}
