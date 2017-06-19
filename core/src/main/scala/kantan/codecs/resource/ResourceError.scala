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

sealed abstract trait ResourceError extends Product with Serializable

object ResourceError {
  sealed case class OpenError(message: String) extends Exception with ResourceError {
    override val getMessage = message
  }

  object OpenError {
    def apply(msg: String, t: Throwable): OpenError = new OpenError(msg) {
      override val getCause = t
    }

    def apply(t: Throwable): OpenError = OpenError(Option(t.getMessage).getOrElse("Open error"), t)
  }

  sealed case class ProcessError(message: String) extends Exception with ResourceError {
      override val getMessage = message
    }

    object ProcessError {
      def apply(msg: String, t: Throwable): ProcessError = new ProcessError(msg) {
        override val getCause = t
      }

      def apply(t: Throwable): ProcessError = ProcessError(Option(t.getMessage).getOrElse("Process error"), t)
    }

  sealed case class CloseError(message: String) extends Exception with ResourceError {
    override val getMessage = message
  }

  object CloseError {
    def apply(msg: String, t: Throwable): CloseError = new CloseError(msg) {
      override val getCause = t
    }

    def apply(t: Throwable): CloseError = CloseError(Option(t.getMessage).getOrElse("Close error"), t)
  }
}
