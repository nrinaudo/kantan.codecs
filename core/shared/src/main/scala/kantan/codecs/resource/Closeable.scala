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

/** Type class for all types that can be closed. */
trait Closeable[A] {

  /** Closes the specified value. */
  def close(a: A): CloseResult
}

object Closeable {
  def apply[A](implicit ev: Closeable[A]): Closeable[A] =
    ev

  def from[A](f: A => CloseResult): Closeable[A] =
    new Closeable[A] {
      override def close(a: A) =
        f(a)
    }

  /** Instance for any type that extends `AutoCloseable`. */
  implicit def autoCloseable[A <: AutoCloseable]: Closeable[A] =
    Closeable.from { c =>
      CloseResult(c.close())
    }
}
