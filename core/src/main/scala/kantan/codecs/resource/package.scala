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

import java.io.{InputStream, OutputStream, Reader, Writer}

package object resource {
  type OpenResult[A] = Result[ResourceError.OpenError, A]
  type CloseResult = Result[ResourceError.CloseError, Unit]
  type ProcessResult[A] = Result[ResourceError.ProcessError, A]
  type ResourceResult[A] = Result[ResourceError, A]

  /** [[Resource]] specialised for `java.io.InputStream`. */
  type InputResource[A] = Resource[A, InputStream]

  /** [[Resource]] specialised for `java.io.Reader`.
    *
    * Note that it's good practice not to declare explicit instances of [[ReaderResource]] for types that have an
    * instance of [[InputResource]]. It's better to let the implicit resolution mechanism work out how to best turn
    * an `InputStream` into a `Reader` - the [[kantan.codecs.resource.bom]] package, in particular, relies on this.
    */
  type ReaderResource[A] = Resource[A, Reader]

  /** [[Resource]] specialised for `java.io.OutputStream`. */
  type OutputResource[A] = Resource[A, OutputStream]

  /** [[Resource]] specialised for `java.io.Writer`.
    *
    * Note that it's good practice not to declare explicit instances of [[WriterResource]] for types that have an
    * instance of [[OutputResource]]. It's better to let the implicit resolution mechanism work out how to best turn
    * an `OutputStream` into a `Writer` - the [[kantan.codecs.resource.bom]] package, in particular, relies on this.
    */
  type WriterResource[A] = Resource[A, Writer]
}
