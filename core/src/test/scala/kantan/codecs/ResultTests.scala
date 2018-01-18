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

import Result.{Failure, Success}
import java.util.NoSuchElementException
import laws.discipline.arbitrary._
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.util.Try

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class ResultTests extends FunSuite with GeneratorDrivenPropertyChecks with Matchers {
  // - Generic tests ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("foreach should not be executed for failures and pass the correct value for successes") {
    forAll { (d: Result[String, Int]) ⇒
      d match {
        case Success(i) ⇒
          d.foreach { v ⇒
            i should be(v)
            ()
          }
        case Failure(_) ⇒ d.foreach(_ ⇒ fail())
      }
    }
  }

  test("forall should return true for failures, or the expected value for successes") {
    forAll { (d: Result[String, Int], f: Int ⇒ Boolean) ⇒
      d match {
        case Success(i) ⇒ d.forall(f) should be(f(i))
        case Failure(_) ⇒ d.forall(f) should be(true)
      }
    }
  }

  test("get should throw for failures and return the expected value for successes") {
    forAll { (d: Result[String, Int]) ⇒
      d match {
        case Success(i) ⇒ d.get should be(i)
        case Failure(_) ⇒
          intercept[NoSuchElementException](d.get)
          ()
      }
    }
  }

  test("getOrElse should return the default value for failures and the expected one for successes") {
    forAll { (d: Result[String, Int], v: Int) ⇒
      d match {
        case Success(i) ⇒ d.getOrElse(v) should be(i)
        case Failure(_) ⇒ d.getOrElse(v) should be(v)
      }
    }
  }

  test("valueOr should apply the specified function to a failure's value, or do nothing for a success") {
    forAll { (d: Result[String, Int], or: String ⇒ Int) ⇒
      d match {
        case Success(i) ⇒ d.valueOr(or) should be(i)
        case Failure(s) ⇒ d.valueOr(or) should be(or(s))
      }
    }
  }

  test("exists should return false for failures, true for successes where it applies") {
    forAll { (d: Result[String, Int], f: Int ⇒ Boolean) ⇒
      d match {
        case Success(i) ⇒ d.exists(f) should be(f(i))
        case Failure(_) ⇒ d.exists(f) should be(false)
      }
    }
  }

  test("ensure should only modify successes for which the specified predicate returns false") {
    forAll { (d: Result[String, Int], failure: String, f: Int ⇒ Boolean) ⇒
      d match {
        case Success(i) ⇒
          if(f(i)) d.ensure(failure)(f) should be(d)
          else d.ensure(failure)(f) should be(Failure(failure))
        case Failure(_) ⇒ d.ensure(failure)(f) should be(d)
      }
    }
  }

  test("recover should only modify failures for which the specified function is defined") {
    forAll { (d: Result[String, Int], f: String ⇒ Option[Int]) ⇒
      {
        val partial = Function.unlift(f)
        d match {
          case Success(i) ⇒ d.recover(partial) should be(Success(i))
          case Failure(s) ⇒
            if(partial.isDefinedAt(s)) d.recover(partial) should be(Success(partial(s)))
            else d.recover(partial) should be(d)
        }
      }
    }
  }

  test("recoverWith should only modify failures for which the specified function is defined") {
    forAll { (d: Result[String, Int], f: String ⇒ Option[Result[String, Int]]) ⇒
      val partial = Function.unlift(f)
      d match {
        case Success(i) ⇒ d.recoverWith(partial) should be(Result.success(i))
        case Failure(s) ⇒
          if(partial.isDefinedAt(s)) d.recoverWith(partial) should be(partial(s))
          else d.recoverWith(partial) should be(d)
      }
    }
  }

  test("fromTry should return a Failure from a Failure and a Success from a Success") {
    forAll { (t: Try[Int]) ⇒
      t match {
        case scala.util.Failure(e) ⇒ Result.fromTry(t) should be(Failure(e))
        case scala.util.Success(i) ⇒ Result.fromTry(t) should be(Success(i))
      }
    }
  }

  test("fromEither should return a Failure from a Left and a Success from a Right") {
    forAll { (e: Either[String, Int]) ⇒
      e match {
        case Left(s)  ⇒ Result.fromEither(e) should be(Failure(s))
        case Right(i) ⇒ Result.fromEither(e) should be(Success(i))
      }
    }
  }

  test("fromOption should return a Failure from a None and a Success from a Some") {
    forAll { (o: Option[Int], s: String) ⇒
      o match {
        case None    ⇒ Result.fromOption(o, s) should be(Failure(s))
        case Some(i) ⇒ Result.fromOption(o, s) should be(Success(i))
      }
    }
  }

  // TODO: work out how to re-enable this
  /*
  test("sequence should behave the same as known implementations") {
    forAll { (l: List[Result[String, Int]]) ⇒
      assert(Result.sequence(l) == l.sequenceU)
    }
  }
   */

  test("flatMap should apply to successes and leave failures untouched") {
    forAll { (d: Result[Int, String], f: String ⇒ Result[Int, Float]) ⇒
      d match {
        case Success(s)     ⇒ d.flatMap(f) should be(f(s))
        case e @ Failure(_) ⇒ d.flatMap(f) should be(e)
      }
    }
  }

  test("leftFlatMap should apply to failures and leave successes untouched") {
    forAll { (d: Result[Int, String], f: Int ⇒ Result[Float, String]) ⇒
      d match {
        case s @ Success(_) ⇒ d.leftFlatMap(f) should be(s)
        case Failure(e)     ⇒ d.leftFlatMap(f) should be(f(e))
      }
    }
  }

  test("biFlatMap should behave as expected") {
    forAll { (d: Result[Int, String], f: Int ⇒ Result[Int, String], g: String ⇒ Result[Int, String]) ⇒
      d match {
        case Failure(e) ⇒ d.biFlatMap(f, g) should be(f(e))
        case Success(s) ⇒ d.biFlatMap(f, g) should be(g(s))
      }
    }
  }

  // - Instance creation tests -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("nonFatal should return the expected value when no error occurs") {
    forAll { (i: Int) ⇒
      Result.nonFatal(i) should be(Success(i))
    }
  }

  test("nonFatal should return the expected failure when an error occurs") {
    forAll { (e: Exception) ⇒
      Result.nonFatal(throw e) should be(Failure(e))
    }
  }

  test("nonFatalOr should return the expected value when no error occurs") {
    forAll { (s: String, i: Int) ⇒
      Result.nonFatalOr(s)(i) should be(Success(i))
    }
  }

  test("nonFatalOr should return the expected failure when an error occurs") {
    forAll { (s: String, e: Exception) ⇒
      Result.nonFatalOr(s)(throw e) should be(Failure(s))
    }
  }

  // - Success specific tests ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("Success should be a success") {
    forAll { s: Success[Int] ⇒
      s.isSuccess should be(true)
      s.isFailure should be(false)
    }
  }

  test("Success.toOption should be a Some") {
    forAll { s: Success[Int] ⇒
      s.toOption should be(Some(s.value))
    }
  }

  test("Success.toEither should be a Right") {
    forAll { s: Success[Int] ⇒
      s.toEither should be(Right(s.value))
    }
  }

  test("Success.toList should not be empty") {
    forAll { s: Success[Int] ⇒
      s.toList should be(List(s.value))
    }
  }

  // - Failure specific tests ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("Failure should be a failure") {
    forAll { f: Failure[Int] ⇒
      f.isSuccess should be(false)
      f.isFailure should be(true)
    }
  }

  test("Failure.toOption should be a None") {
    forAll { f: Failure[Int] ⇒
      f.toOption should be('empty)
    }
  }

  test("Failure.toEither should be a Left") {
    forAll { f: Failure[Int] ⇒
      f.toEither should be(Left(f.value))
    }
  }

  test("Failure.toList should be Nil") {
    forAll { f: Failure[Int] ⇒
      f.toList should be('empty)
    }
  }
}
