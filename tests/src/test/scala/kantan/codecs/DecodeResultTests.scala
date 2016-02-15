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

  test("valueOr should apply the specified function to a failure's value, or do nothing for a success") {
    forAll { (d: DecodeResult[String, Int], or: String ⇒ Int) ⇒ d match {
      case Success(i) ⇒ assert(d.valueOr(or) == i)
      case Failure(s) ⇒ assert(d.valueOr(or) == or(s))
    }}
  }

  test("exists should return false for failures, true for successes where it applies") {
    forAll { (d: DecodeResult[String, Int], f: Int ⇒ Boolean) ⇒ d match {
      case Success(i) ⇒ assert(d.exists(f) == f(i))
      case Failure(s) ⇒ assert(!d.exists(f))
    }}
  }

  test("ensure should only modify successes for which the specified predicate returns false") {
    forAll { (d: DecodeResult[String, Int], failure: String, f: Int ⇒ Boolean) ⇒ d match {
      case Success(i) ⇒
        if(f(i)) assert(d.ensure(failure)(f) == d)
        else     assert(d.ensure(failure)(f) == DecodeResult.failure(failure))
      case Failure(s) ⇒ assert(d.ensure(failure)(f) == d)
    }}
  }

  // TODO: this requires Arbitrary[PartialFunction[String, Int]]. Write when time allows.
  /*
  test("recover should only modify failures for which the specified function is defined") {
    forAll { (d: DecodeResult[String, Int], f: PartialFunction[String, Int]) ⇒ d match {
      case Success(i) ⇒ assert(d.recover(f) == DecodeResult.success(i))
      case Failure(s) ⇒
        if(f.isDefinedAt(s)) assert(d.recover(f) == DecodeResult.success(f(s)))
        else                 assert(d.recover(f) == d)
    }}
  }
  */


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
