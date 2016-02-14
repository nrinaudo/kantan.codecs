package kantan.codecs

import kantan.codecs.DecodeResult.{Failure, Success}
import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class DecodeResultTests extends FunSuite with GeneratorDrivenPropertyChecks {
  // - Success specific tests ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("Success should be a success") {
    forAll { s: Success[Int] ⇒
      assert(s.isSuccess)
      assert(!s.isFailure)
    }
  }

  test("Success.toOption should be a Some") {
    forAll { s: Success[Int] ⇒ assert(s.toOption.contains(s.value)) }
  }

  test("Success.toEither should be a Right") {
    forAll { s: Success[Int] ⇒ assert(s.toEither == Right(s.value)) }
  }

  test("Success.toList should not be empty") {
    forAll { s: Success[Int] ⇒ assert(s.toList == List(s.value)) }
  }

  test("Success.get should succeed") {
    forAll { s: Success[Int] ⇒ assert(s.get == s.value) }
  }

  test("Success.getOrElse should ignore the orElse") {
    forAll { (s: Success[Int], f: String) ⇒ assert(s.getOrElse(f) == s.value) }
  }



  // - Failure specific tests ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("Failure should be a failure") {
    forAll { f: Failure[Int] ⇒
      assert(!f.isSuccess)
      assert(f.isFailure)
    }
  }

  test("Failure.toOption should be a None") {
    forAll { f: Failure[Int] ⇒ assert(f.toOption.isEmpty) }
  }

  test("Failure.toEither should be a Left") {
    forAll { f: Failure[Int] ⇒ assert(f.toEither == Left(f.value)) }
  }

  test("Failure.toList should be Nil") {
    forAll { f: Failure[Int] ⇒ assert(f.toList == Nil) }
  }

  test("Failure.get should throw NoSuchElementException") {
    forAll { f: Failure[Int] ⇒
      intercept[NoSuchElementException](f.get)
      ()
    }
  }

  test("Failure.getOrElse should return the orElse part") {
    forAll { (f: Failure[Int], s: String) ⇒ assert(f.getOrElse(s) == s) }
  }
}
