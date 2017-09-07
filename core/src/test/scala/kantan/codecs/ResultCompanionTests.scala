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

import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.util.Try

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class ResultCompanionTests extends FunSuite with GeneratorDrivenPropertyChecks with Matchers {
  object simple extends ResultCompanion.Simple[String]

  object withDefault extends ResultCompanion.WithDefault[String] {
    override protected def fromThrowable(t: Throwable) = t.getMessage
  }

  test("Simple.success should behave like Result.success") {
    forAll { i: Int ⇒
      simple.success(i) should be(Result.success(i))
    }
  }

  test("Simple.failure should behave like Result.failure") {
    forAll { s: String ⇒
      simple.failure(s) should be(Result.failure(s))
    }
  }

  test("Simple.fromEither should behave like Result.fromEither") {
    forAll { (e: Either[String, Int]) ⇒
      simple.fromEither(e) should be(Result.fromEither(e))
    }
  }

  test("Simple.fromOption should behave like Result.fromOption") {
    forAll { (o: Option[Int], s: String) ⇒
      simple.fromOption(o, s) should be(Result.fromOption(o, s))
    }
  }

  test("Simple.sequence should behave like Result.sequence") {
    forAll { (l: List[Result[String, Int]]) ⇒
      simple.sequence(l) should be(Result.sequence(l))
    }
  }

  test("WithDefault.apply should yield the expected result on success") {
    forAll { i: Int ⇒
      withDefault(i) should be(Result.success(i))
    }
  }

  test("WithDefault.apply should yield the expected result on failure") {
    forAll { e: Exception ⇒
      withDefault(throw e) should be(Result.failure(e.getMessage))
    }
  }

  test("WithDefault.fromTry should yield the expected result") {
    forAll { ti: Try[Int] ⇒
      val res = withDefault.fromTry(ti)

      ti match {
        case scala.util.Success(i) ⇒ res should be(Result.Success(i))
        case scala.util.Failure(t) ⇒ res should be(Result.Failure(t.getMessage))
      }
    }
  }
}
