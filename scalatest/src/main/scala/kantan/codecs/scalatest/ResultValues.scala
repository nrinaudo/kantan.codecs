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

import org.scalactic._
import org.scalatest.exceptions.StackDepthException
import org.scalatest.exceptions.TestFailedException

/** Provides syntax for testing [[kantan.codecs.Result]] values.
  *
  * This adds two methods to [[kantan.codecs.Result]]:
  *  - [[ResultValuable.success success]]
  *  - [[ResultValuable.failure failure]]
  *
  * This lets you write code such as:
  * {{{
  * // Fails with a useful error message if `result` is a Failure
  * result.success.value should be (1)
  *
  * // Fails with a useful error message if `result` is a Success
  * result.failure.value should be (1)
  * }}}
  *
  * You can either mix the trait in your test suite, or bring the new syntax in scope with
  * `import kantan.codecs.scalatest.ResultValues._`.
  */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
trait ResultValues {

  /** Adds [[success]] and [[failure]] to values of type [[kantan.codecs.Result]]. */
  implicit class ResultValuable[L, R](result: Result[L, R])(implicit pos: source.Position) {
    def success: SuccessValuable[L, R] = new SuccessValuable(result, pos)
    def failure: FailureValuable[L, R] = new FailureValuable(result, pos)
  }

  /** Provides a [[value]] method that fails with a useful error if the underlying [[kantan.codecs.Result]]
    * is a Success.
    */
  class FailureValuable[L, R](result: Result[L, R], pos: source.Position) {
    def value: L =
      result match {
        case Result.Failure(l) ⇒ l
        case Result.Success(_) ⇒
          throw new TestFailedException(
            (_: StackDepthException) ⇒ Some(Resources.resultFailureValueNotDefined),
            None,
            pos
          )
      }
  }

  /** Provides a [[value]] method that fails with a useful error if the underlying [[kantan.codecs.Result]]
    * is a Failure.
    */
  class SuccessValuable[L, R](result: Result[L, R], pos: source.Position) {
    def value: R =
      result match {
        case Result.Success(r) ⇒ r
        case Result.Failure(_) ⇒
          throw new TestFailedException(
            (_: StackDepthException) ⇒ Some(Resources.resultSuccessValueNotDefined),
            None,
            pos
          )
      }
  }
}

object ResultValues extends ResultValues
