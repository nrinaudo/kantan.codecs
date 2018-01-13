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

/** Aggregates error messages. */
object Resources {

  /** Error when `failure.value` is called on a [[kantan.codecs.Result.Success]]. */
  val resultFailureValueNotDefined = "The Result on which failure.value was invoked was not defined as a Failure"

  /** Error when `success.value` is called on a [[kantan.codecs.Result.Failure]]. */
  val resultSuccessValueNotDefined = "The Result on which success.value was invoked was not defined as a Success"
}
