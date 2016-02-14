package kantan.codecs

import kantan.codecs.DecodeResult.{Failure, Success}
import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class DecodeResultTests extends FunSuite with GeneratorDrivenPropertyChecks {
  // - Generic tests ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("foreach should not be executed for failures and pass the correct value for successes") {
    forAll { (d: DecodeResult[String, Int]) ⇒ d match {
      case Success(i) ⇒ d.foreach(v ⇒ assert(i == v))
      case Failure(_) ⇒ d.foreach(_ ⇒ fail())
    }}
  }

  test("forall should return true for failures, or the expected value for successes") {
    forAll { (d: DecodeResult[String, Int], f: Int ⇒ Boolean) ⇒ d match {
      case Success(i) ⇒ assert(d.forall(f) == f(i))
      case Failure(_) ⇒ assert(d.forall(f))
    }}
  }

  test("get should throw for failures and return the expected value for successes") {
    forAll { (d: DecodeResult[String, Int]) ⇒ d match {
      case Success(i) ⇒ assert(d.get == i)
      case Failure(_) ⇒
        intercept[NoSuchElementException](d.get)
        ()
    }}
  }

  test("getOrElse should return the default value for failures and the expected one for successes") {
      forAll { (d: DecodeResult[String, Int], v: Int) ⇒ d match {
        case Success(i) ⇒ assert(d.getOrElse(v) == i)
        case Failure(_) ⇒ assert(d.getOrElse(v) == v)
      }}
    }


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
}
