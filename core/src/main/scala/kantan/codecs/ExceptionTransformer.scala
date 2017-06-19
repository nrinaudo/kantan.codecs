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

/** Type class for types that can be extracted from exceptions.
  *
  * The purpose of this type class is to abstract over exception handling. A typical example is when one deals with
  * unsafe code, such as while doing java interop, but doesn't want to hard-code the failure type.
  */
trait ExceptionTransformer[E] { self ⇒
  /** Transforms an exception into a usable error. */
  def transform(t: Throwable): E

  /** Transforms an exception and error message into a usable error. */
  def transform(msg: String, t: Throwable): E

  def map[EE](f: E ⇒ EE): ExceptionTransformer[EE] = new ExceptionTransformer[EE] {
    override def transform(t: Throwable) = f(self.transform(t))
    override def transform(msg: String, t: Throwable) = f(self.transform(msg, t))
  }
}

object ExceptionTransformer {
  /** Summons an instance of [[ExceptionTransformer]] for `A` if one is found, fails the build otherwise.
    *
    * This is equivalent to, but faster than, `implicitly`.
    */
  def apply[A](implicit ev: ExceptionTransformer[A]): ExceptionTransformer[A] =
    macro imp.summon[ExceptionTransformer[A]]

  /** Evaluates its arguments and wraps it in a [[Result]].
    *
    * Any error is transformed and wrapped in a [[Result.Failure]].
    */
  def result[E: ExceptionTransformer, S](s: ⇒ S): Result[E, S] =
    Result.nonFatal(s).leftMap(ExceptionTransformer[E].transform)

  /** Default instances for `Exception`. */
  implicit val exceptionTransformer: ExceptionTransformer[Exception] = new ExceptionTransformer[Exception] {
    override def transform(msg: String, cause: Throwable) = new Exception(msg, cause)
    override def transform(cause: Throwable) = new Exception(cause)
  }
}
