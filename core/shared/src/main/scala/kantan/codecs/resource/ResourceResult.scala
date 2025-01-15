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

package kantan.codecs.resource

import kantan.codecs.ResultCompanion

/** Provides convenience methods for [[ResourceResult]]. */
object ResourceResult extends ResultCompanion.Simple[ResourceError] {

  /** Evaluates the specified value, returning an [[OpenResult]] if an error occurs.
    *
    * @example
    *   {{{
    * scala> def f: Int = sys.error("something went wrong")
    *
    * scala> ResourceResult.open(f)
    * res0: ResourceResult[Int] = Left(OpenError: something went wrong)
    *   }}}
    *
    * @see
    *   OpenResult.apply
    */
  def open[A](a: => A): ResourceResult[A] =
    OpenResult(a)

  /** Evaluates the specified value, returning a [[ProcessResult]] if an error occurs.
    *
    * @example
    *   {{{
    * scala> def f: Int = sys.error("something went wrong")
    *
    * scala> ResourceResult.process(f)
    * res0: ResourceResult[Int] = Left(ProcessError: something went wrong)
    *   }}}
    *
    * @see
    *   ProcessResult.apply
    */
  def process[A](a: => A): ResourceResult[A] =
    ProcessResult(a)

  /** Evaluates the specified value, returning a [[CloseResult]] if an error occurs.
    *
    * @example
    *   {{{
    * scala> def f: Int = sys.error("something went wrong")
    *
    * scala> ResourceResult.close(f)
    * res0: ResourceResult[Unit] = Left(CloseError: something went wrong)
    *   }}}
    *
    * @see
    *   ResourceResult.apply
    */
  def close[U](u: => U): ResourceResult[Unit] =
    CloseResult(u)
}
