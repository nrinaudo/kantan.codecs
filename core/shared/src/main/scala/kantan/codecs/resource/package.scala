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

import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer

package object resource {
  type OpenResult[A]     = Either[ResourceError.OpenError, A]
  type CloseResult       = Either[ResourceError.CloseError, Unit]
  type ProcessResult[A]  = Either[ResourceError.ProcessError, A]
  type ResourceResult[A] = Either[ResourceError, A]

  /** [[Resource]] specialised for `java.io.InputStream`. */
  type InputResource[A] = Resource[A, InputStream]

  /** [[Resource]] specialised for `java.io.Reader`.
    *
    * Note that it's good practice not to declare explicit instances of [[ReaderResource]] for types that have an
    * instance of [[InputResource]]. It's better to let the implicit resolution mechanism work out how to best turn an
    * `InputStream` into a `Reader` - the JVM-specific `kantan.codecs.resource.bom` package, in particular, relies on
    * this.
    */
  type ReaderResource[A] = Resource[A, Reader]

  /** [[Resource]] specialised for `java.io.OutputStream`. */
  type OutputResource[A] = Resource[A, OutputStream]

  /** [[Resource]] specialised for `java.io.Writer`.
    *
    * Note that it's good practice not to declare explicit instances of [[WriterResource]] for types that have an
    * instance of [[OutputResource]]. It's better to let the implicit resolution mechanism work out how to best turn an
    * `OutputStream` into a `Writer` - the JVM-specific `kantan.codecs.resource.bom` package, in particular, relies on
    * this.
    */
  type WriterResource[A] = Resource[A, Writer]
}
