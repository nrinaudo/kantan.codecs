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

import kantan.codecs.ResultCompanion

/** Type class for types that can be used as errors.
  *
  * This is mostly meant to work in conjunction with [[Error]], and lets code that deals with errors turn them into
  * values of the right error type.
  */
trait IsError[E] extends Serializable { self =>

  /** Creates a new instance of `E` from an exception. */
  def fromThrowable(t: Throwable): E

  /** Creates a new instance of `E` from an error message. */
  def fromMessage(msg: String): E

  /** Creates a new instance of `E` from an error message and exception. */
  def from(msg: String, t: Throwable): E

  /** Safely evaluates the specified argument, wrapping errors in a `E`. */
  def safe[A](a: => A): Either[E, A] =
    ResultCompanion.nonFatal(fromThrowable)(a)

  def map[EE](f: E => EE): IsError[EE] =
    new IsError[EE] {
      override def fromThrowable(t: Throwable) =
        f(self.fromThrowable(t))
      override def fromMessage(msg: String) =
        f(self.fromMessage(msg))
      override def from(msg: String, t: Throwable) =
        f(self.from(msg, t))
    }
}

object IsError {

  /** Summons an implicit instance of `IsError[A]` if one is found in scope, fails compilation otherwise. */
  def apply[A](implicit ev: IsError[A]): IsError[A] =
    ev

  /** Default instance for `Exception.` */
  implicit val exceptionIsError: IsError[Exception] = new IsError[Exception] {
    override def fromThrowable(t: Throwable) =
      new Exception(t)
    override def fromMessage(msg: String) =
      new Exception(msg)
    override def from(msg: String, t: Throwable) =
      new Exception(msg, t)
  }
}
