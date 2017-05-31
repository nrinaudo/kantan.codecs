/*
 * Copyright 2017 Nicolas Rinaudo
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
  * This is mostly meant for [[Error]] and its subtypes, as it allows one to abstract over failure types, such as
  * is done is [[DecoderCompanion.fromUnsafe]]
  */
trait ExceptionTransformer[E] { self ⇒
  def transform(t: Throwable): E
  def transform(msg: String, t: Throwable): E

  def map[EE](f: E ⇒ EE): ExceptionTransformer[EE] = new ExceptionTransformer[EE] {
    override def transform(t: Throwable) = f(self.transform(t))
    override def transform(msg: String, t: Throwable) = f(self.transform(msg, t))
  }
}

object ExceptionTransformer {
  def from[E <: Error](defaultMsg: String, f: String ⇒ E): ExceptionTransformer[E] = new ExceptionTransformer[E] {
    def transform(msg: String, cause: Throwable): E = {
      val error = f(msg)
      error.initCause(cause)
      error
    }

    override def transform(cause: Throwable): E = transform(Option(cause.getMessage).getOrElse(defaultMsg), cause)
  }
}
