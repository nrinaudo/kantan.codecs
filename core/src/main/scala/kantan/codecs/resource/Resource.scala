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
  implicit def inputStream[I <: InputStream]: InputResource[I] = Resource.from(Result.success)
  implicit def reader[R <: Reader]: ReaderResource[R] = Resource.from(Result.success)

  implicit def outputStream[O <: OutputStream]: OutputResource[O] = Resource.from(Result.success)
  implicit def writer[W <: Writer]: WriterResource[W] = Resource.from(Result.success)



  // - Byte to char ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def readerFromStream[A](implicit codec: Codec, res: InputResource[A]): ReaderResource[A]  =
    res.map(i ⇒ new InputStreamReader(i, codec.charSet))

  implicit def writerFromStream[A](implicit codec: Codec, res: OutputResource[A]): WriterResource[A] =
    res.map(o ⇒ new OutputStreamWriter(o, codec.charSet))



  // - Standard types --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val fileInput: InputResource[File] = inputStream.contramapResult(f ⇒ open(new FileInputStream(f)))
  implicit val fileOutput: OutputResource[File] = outputStream.contramapResult(f ⇒ open(new FileOutputStream(f)))
  implicit val pathInput: InputResource[Path] = Resource.from(p ⇒ open(Files.newInputStream(p)))
  implicit val pathOutput: OutputResource[Path] = Resource.from(p ⇒ open(Files.newOutputStream(p)))

  implicit def pathReader(implicit codec: Codec): ReaderResource[Path] =
    Resource.from(p ⇒ open(Files.newBufferedReader(p, codec.charSet)))
  implicit def pathWriter(implicit codec: Codec): WriterResource[Path] =
      Resource.from(p ⇒ open(Files.newBufferedWriter(p, codec.charSet)))

  implicit val bytes: InputResource[Array[Byte]] =
    inputStream.contramap(bs ⇒ new ByteArrayInputStream(bs))
  implicit val chars: ReaderResource[Array[Char]] = reader.contramap(cs ⇒ new CharArrayReader(cs))
  implicit val string: ReaderResource[String] = reader.contramap(s ⇒ new StringReader(s))

  implicit val url: InputResource[URL] =
    inputStream.contramapResult(u ⇒ open(u.openStream()))
  implicit val uri: InputResource[URI] = url.contramap(_.toURL)
}
