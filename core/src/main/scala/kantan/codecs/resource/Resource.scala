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

package kantan.codecs.resource

import java.io._
import java.net.{URI, URL}
import java.nio.file.{Files, Path}
import kantan.codecs.Result
import scala.io.Codec

trait Resource[I, R, E] { self ⇒
  def open(a: I): Result[E, R]

  def contramap[AA](f: AA ⇒ I): Resource[AA, R, E] = Resource.from(f andThen self.open)
  def contramapResult[AA](f: AA ⇒ Result[E, I]): Resource[AA, R, E] =
    Resource.from(aa ⇒ f(aa).flatMap(self.open))

  def map[BB](f: R ⇒ BB): Resource[I, BB, E] = Resource.from(a ⇒ open(a).map(f))
  def mapResult[BB](f: R ⇒ Result[E, BB]): Resource[I, BB, E] =
    Resource.from(a ⇒ open(a).flatMap(f))
}

object Resource {
  def from[A, B, E](f: A ⇒ Result[E, B]): Resource[A, B, E] = new Resource[A, B, E] {
    override def open(a: A) = f(a)
  }

  private def open[A](a: ⇒ A): Result[ResourceError, A] = Result.nonFatal(a).leftMap(ResourceError.apply)

  // - Raw streams -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def streamInputResource[I <: InputStream]: InputResource[I] = Resource.from(Result.success)
  implicit def readerReaderResource[R <: Reader]: ReaderResource[R] = Resource.from(Result.success)

  implicit def streamOutputResource[O <: OutputStream]: OutputResource[O] = Resource.from(Result.success)
  implicit def writerWriterResource[W <: Writer]: WriterResource[W] = Resource.from(Result.success)



  // - Byte to char ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def readerFromStream[A: InputResource](implicit codec: Codec): ReaderResource[A]  =
    InputResource[A].map(i ⇒ new InputStreamReader(i, codec.charSet))

  implicit def writerFromStream[A: OutputResource](implicit codec: Codec): WriterResource[A] =
    OutputResource[A].map(o ⇒ new OutputStreamWriter(o, codec.charSet))



  // - Standard types --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val fileInputResource: InputResource[File] =
    streamInputResource.contramapResult(f ⇒ open(new FileInputStream(f)))
  implicit val fileOutputResource: OutputResource[File] =
    streamOutputResource.contramapResult(f ⇒ open(new FileOutputStream(f)))
  implicit val pathInputResource: InputResource[Path] = Resource.from(p ⇒ open(Files.newInputStream(p)))
  implicit val pathOutputResource: OutputResource[Path] = Resource.from(p ⇒ open(Files.newOutputStream(p)))

  implicit def pathReaderResource(implicit codec: Codec): ReaderResource[Path] =
    Resource.from(p ⇒ open(Files.newBufferedReader(p, codec.charSet)))
  implicit def pathWriterResource(implicit codec: Codec): WriterResource[Path] =
      Resource.from(p ⇒ open(Files.newBufferedWriter(p, codec.charSet)))

  implicit val bytesInputResource: InputResource[Array[Byte]] =
    streamInputResource.contramap(bs ⇒ new ByteArrayInputStream(bs))
  implicit val charsReaderResource: ReaderResource[Array[Char]] =
    readerReaderResource.contramap(cs ⇒ new CharArrayReader(cs))
  implicit val stringReaderResource: ReaderResource[String] = readerReaderResource.contramap(s ⇒ new StringReader(s))

  implicit val urlInputResource: InputResource[URL] =
    streamInputResource.contramapResult(u ⇒ open(u.openStream()))
  implicit val uriInputResource: InputResource[URI] = urlInputResource.contramap(_.toURL)
}
