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
package scalatest

import org.scalatest.{FunSuite, Matchers, OptionValues}
import org.scalatest.exceptions.TestFailedException

class ResultValuesTests extends FunSuite with Matchers with ResultValues with OptionValues {
  test("it should be possible to use 'failure and 'success in tests") {
    Result.failure("foo") should be a 'failure
    Result.failure("foo") shouldNot be a 'success
    Result.success(1) should be a 'success
    Result.success(1) shouldNot be a 'failure
  }

  test("it should return the failure value inside a result if failure.value is defined") {
    val r: Result[String, Int] = Result.failure("foo")

    r.failure.value should be("foo")
  }

  test("it should throw TestFailedException if failure.value is empty") {
    val r: Result[String, Int] = Result.success(1)

    val caught = the[TestFailedException] thrownBy {
      r.failure.value should be("foo")
    }
    caught.failedCodeFileName.value should be("ResultValuesTests.scala")
    caught.message.value should be(Resources.resultFailureValueNotDefined)
  }

  test("it should return the success value inside a result if success.value is defined") {
    val r: Result[String, Int] = Result.success(1)

    r.success.value should be(1)
  }

  test("it should throw TestFailedException if success.value is empty") {
    val r: Result[String, Int] = Result.failure("foo")

    val caught = the[TestFailedException] thrownBy {
      r.success.value should be(1)
    }
    caught.failedCodeFileName.value should be("ResultValuesTests.scala")
    caught.message.value should be(Resources.resultSuccessValueNotDefined)
  }
}
