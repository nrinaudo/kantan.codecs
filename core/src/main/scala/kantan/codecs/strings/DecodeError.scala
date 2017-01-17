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

package kantan.codecs.strings

sealed case class DecodeError(message: String) extends Exception {
  override final val getMessage = message
}

object DecodeError {
  def apply(msg: String, t: Throwable): DecodeError = new DecodeError(msg) {
    override val getCause = t
    override def toString: String = s"DecodeError($msg)"
  }

  def apply(t: Throwable): DecodeError = DecodeError(Option(t.getMessage).getOrElse("Decode error"), t)
}
