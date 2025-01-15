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

package kantan.codecs.error

/** Base class for errors
  *
  * This is specifically meant for errors represented as ADTs. The fact that it extends `Exception` is an unfortunate
  * side effect of the Scala stdlib's reliance on exceptions, as seen with `scala.concurrent.Future` and
  * `scala.util.Try`, for example.
  */
abstract class Error(message: String) extends Exception(message) with Product with Serializable {
  override final def toString: String =
    productPrefix + ": " + getMessage
}

/** Provides useful instance creation methods for errors that might be created as a result of Java exceptions. */
abstract class ErrorCompanion[T <: Error](defaultMsg: String)(f: String => T) extends Serializable {
  implicit val isError: IsError[T] = new IsError[T] {
    override def from(msg: String, cause: Throwable) = {
      val error = f(msg)
      error.initCause(cause)
      error
    }

    override def fromMessage(msg: String) =
      from(msg, new Exception(msg))

    override def fromThrowable(cause: Throwable): T =
      from(Option(cause.getMessage).getOrElse(defaultMsg), cause)
  }

  /** Attempts to evaluate the specified argument, wrapping errors in a `T`. */
  def safe[A](a: => A): Either[T, A] =
    isError.safe(a)

  def apply(msg: String, cause: Throwable): T =
    isError.from(msg, cause)
  def apply(cause: Throwable): T =
    isError.fromThrowable(cause)
  def apply(msg: String): T =
    isError.fromMessage(msg)
}
