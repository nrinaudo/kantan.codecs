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

import kantan.codecs.error.Error
import kantan.codecs.error.ErrorCompanion

/** Errors that can occur while working with a [[Resource]]. */
sealed abstract class ResourceError(message: String) extends Error(message)

object ResourceError {

  /** Errors that occur specifically while opening resources. */
  final case class OpenError(message: String) extends ResourceError(message)

  object OpenError extends ErrorCompanion("an unspecified error occurred while opening a resource")(new OpenError(_))

  /** Errors that occur specifically while processing resources. */
  final case class ProcessError(message: String) extends ResourceError(message)

  object ProcessError
      extends ErrorCompanion("an unspecified error occurred while processing a resource")(new ProcessError(_))

  /** Errors that occur specifically while closing resources. */
  final case class CloseError(message: String) extends ResourceError(message)

  object CloseError extends ErrorCompanion("an unspecified error occurred while closing a resource")(new CloseError(_))
}
