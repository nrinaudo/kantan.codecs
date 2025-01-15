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

import java.io.CharArrayReader
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

/** JVM specific instances of [[Resource]]. */
trait PlatformSpecificInstances {

  implicit val pathInputResource: InputResource[Path] =
    InputResource[InputStream].econtramap(p => OpenResult(Files.newInputStream(p)))

  implicit val pathOutputResource: OutputResource[Path] =
    OutputResource[OutputStream].econtramap(p => OpenResult(Files.newOutputStream(p)))

  implicit val fileInputResource: InputResource[File] = InputResource[Path].contramap(_.toPath)

  implicit val fileOutputResource: OutputResource[File] = OutputResource[Path].contramap(_.toPath)

  implicit val charsReaderResource: ReaderResource[Array[Char]] =
    ReaderResource[Reader].contramap(cs => new CharArrayReader(cs))

  implicit val urlInputResource: InputResource[URL] =
    InputResource[InputStream].econtramap(u => OpenResult(u.openStream()))

  implicit val uriInputResource: InputResource[URI] = InputResource[URL].contramap(_.toURL)

}
