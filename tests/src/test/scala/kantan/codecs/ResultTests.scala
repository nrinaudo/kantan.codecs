/*
 * Copyright 2016 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.codecs

import _root_.cats.std.list._
import _root_.cats.syntax.traverse._
import cats._
import java.util.NoSuchElementException
import kantan.codecs.Result.{Failure, Success}
import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.util.Try

class ResultTests extends FunSuite with GeneratorDrivenPropertyChecks {
  // - Generic tests ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("foreach should not be executed for failures and pass the correct value for successes") {
    forAll { (d: Result[String, Int]) ⇒ d match {
      case Success(i) ⇒ d.foreach(v ⇒ assert(i == v))
      case Failure(_) ⇒ d.foreach(_ ⇒ fail())
    }}
  }

  test("forall should return true for failures, or the expected value for successes") {
    forAll { (d: Result[String, Int], f: Int ⇒ Boolean) ⇒ d match {
      case Success(i) ⇒ assert(d.forall(f) == f(i))
      case Failure(_) ⇒ assert(d.forall(f))
    }}
  }

  test("get should throw for failures and return the expected value for successes") {
    forAll { (d: Result[String, Int]) ⇒ d match {
      case Success(i) ⇒ assert(d.get == i)
      case Failure(_) ⇒
        intercept[NoSuchElementException](d.get)
        ()
    }}
  }

  test("getOrElse should return the default value for failures and the expected one for successes") {
    forAll { (d: Result[String, Int], v: Int) ⇒ d match {
      case Success(i) ⇒ assert(d.getOrElse(v) == i)
      case Failure(_) ⇒ assert(d.getOrElse(v) == v)
    }}
  }

  test("valueOr should apply the specified function to a failure's value, or do nothing for a success") {
    forAll { (d: Result[String, Int], or: String ⇒ Int) ⇒ d match {
      case Success(i) ⇒ assert(d.valueOr(or) == i)
      case Failure(s) ⇒ assert(d.valueOr(or) == or(s))
    }}
  }

  test("exists should return false for failures, true for successes where it applies") {
    forAll { (d: Result[String, Int], f: Int ⇒ Boolean) ⇒ d match {
      case Success(i) ⇒ assert(d.exists(f) == f(i))
      case Failure(s) ⇒ assert(!d.exists(f))
    }}
  }

  test("ensure should only modify successes for which the specified predicate returns false") {
    forAll { (d: Result[String, Int], failure: String, f: Int ⇒ Boolean) ⇒ d match {
      case Success(i) ⇒
        if(f(i)) assert(d.ensure(failure)(f) == d)
        else     assert(d.ensure(failure)(f) == Result.failure(failure))
      case Failure(s) ⇒ assert(d.ensure(failure)(f) == d)
    }}
  }

  test("recover should only modify failures for which the specified function is defined") {
    forAll { (d: Result[String, Int], f: String ⇒ Option[Int]) ⇒ {
      val partial = Function.unlift(f)
      d match {
        case Success(i) ⇒ assert(d.recover(partial) == Result.success(i))
        case Failure(s) ⇒
          if(partial.isDefinedAt(s)) assert(d.recover(partial) == Result.success(partial(s)))
          else                       assert(d.recover(partial) == d)
      }
    }}
  }

  test("fromTry should return a Failure from a Failure and a Success from a Success") {
    forAll { (t: Try[Int]) ⇒ t match {
      case scala.util.Failure(e) ⇒ assert(Result.fromTry(t) == Failure(e))
      case scala.util.Success(i) ⇒ assert(Result.fromTry(t) == Success(i))
    }}
  }

  test("fromEither should return a Failure from a Left and a Success from a Right") {
    forAll { (e: Either[String, Int]) ⇒ e match {
      case Left(s) ⇒ assert(Result.fromEither(e) == Failure(s))
      case Right(i) ⇒ assert(Result.fromEither(e) == Success(i))
    }}
  }

  test("fromOption should return a Failure from a None and a Success from a Some") {
    forAll { (o: Option[Int], s: String) ⇒ o match {
      case None ⇒ assert(Result.fromOption(o, s) == Failure(s))
      case Some(i) ⇒ assert(Result.fromOption(o, s) == Success(i))
    }}
  }

  test("sequence should behave the same as known implementations") {
    forAll { (l: List[Result[String, Int]]) =>
      assert(Result.sequence(l) == l.sequenceU)
    }
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
    forAll { s: Success[Int] ⇒ assert(s.toOption == Some(s.value)) }
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
