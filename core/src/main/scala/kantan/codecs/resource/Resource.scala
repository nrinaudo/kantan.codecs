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
package resource

import java.io._
import java.net.{URI, URL}
import java.nio.file.{Files, Path}
import scala.io.Codec

/** Represents a resource that can be opened and worked on.
  *
  * The purpose of this trait is to abstract over the notion of "things that can be opened", such as files, URLs...
  * Default instances are provided for `java.io` types - `java.io.File`, for example, has instances for both
  * opening it for reading and for writing.
  *
  * @tparam I type of the resource itself (eg `java.io.File`).
  * @tparam R type of the opened resource (eg `java.io.InputStream`)
  */
trait Resource[I, R] { self ⇒

  /** Opens the specified resource. */
  def open(input: I): OpenResult[R]

  /** Opens the specified resource and applies the specified function to its content.
    *
    * The resource will be closed regardless of whether `f` fails. Note that `f` is expected to be safe - it cannot
    * throw but should instead wrap all errors in a [[ProcessResult]].
    */
  def withResource[O](input: I)(f: R ⇒ ProcessResult[O])(implicit c: Closeable[R]): ResourceResult[O] =
    open(input).flatMap { r ⇒
      val res = f(r)

      Closeable[R].close(r).flatMap(_ ⇒ res)
    }

  def contramap[II](f: II ⇒ I): Resource[II, R] = Resource.from(f andThen self.open)
  def contramapResult[II](f: II ⇒ OpenResult[I]): Resource[II, R] =
    Resource.from(aa ⇒ f(aa).flatMap(self.open))

  def map[RR](f: R ⇒ RR): Resource[I, RR] = Resource.from(a ⇒ open(a).map(f))
  def mapResult[RR](f: R ⇒ OpenResult[RR]): Resource[I, RR] =
    Resource.from(a ⇒ open(a).flatMap(f))
}

object Resource {
  def from[I, R](f: I ⇒ OpenResult[R]): Resource[I, R] = new Resource[I, R] {
    override def open(a: I) = f(a)
  }

  private def open[A](a: ⇒ A): OpenResult[A] = Result.nonFatal(a).leftMap(ResourceError.OpenError.apply)

  // - Raw streams -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def streamInputResource[I <: InputStream]: InputResource[I] = Resource.from(Result.success)
  implicit def readerReaderResource[R <: Reader]: ReaderResource[R]    = Resource.from(Result.success)

  implicit def streamOutputResource[O <: OutputStream]: OutputResource[O] = Resource.from(Result.success)
  implicit def writerWriterResource[W <: Writer]: WriterResource[W]       = Resource.from(Result.success)

  // - Byte to char ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Turns any [[InputResource]] into a [[ReaderResource]] using whatever implicit `Codec` is found in scope. */
  implicit def readerFromStream[A: InputResource](implicit codec: Codec): ReaderResource[A] =
    InputResource[A].map(i ⇒ new InputStreamReader(i, codec.charSet))

  /** Turns any [[OutputResource]] into a [[WriterResource]] using whatever implicit `Codec` is found in scope. */
  implicit def writerFromStream[A: OutputResource](implicit codec: Codec): WriterResource[A] =
    OutputResource[A].map(o ⇒ new OutputStreamWriter(o, codec.charSet))

  // - Standard types --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val pathInputResource: InputResource[Path] =
    InputResource[InputStream].contramapResult(p ⇒ open(Files.newInputStream(p)))
  implicit val pathOutputResource: OutputResource[Path] =
    OutputResource[OutputStream].contramapResult(p ⇒ open(Files.newOutputStream(p)))

  implicit val fileInputResource: InputResource[File]   = InputResource[Path].contramap(_.toPath)
  implicit val fileOutputResource: OutputResource[File] = OutputResource[Path].contramap(_.toPath)

  implicit val bytesInputResource: InputResource[Array[Byte]] =
    InputResource[InputStream].contramap(bs ⇒ new ByteArrayInputStream(bs))
  implicit val charsReaderResource: ReaderResource[Array[Char]] =
    ReaderResource[Reader].contramap(cs ⇒ new CharArrayReader(cs))
  implicit val stringReaderResource: ReaderResource[String] =
    ReaderResource[Reader].contramap(s ⇒ new StringReader(s))

  implicit val urlInputResource: InputResource[URL] =
    InputResource[InputStream].contramapResult(u ⇒ open(u.openStream()))
  implicit val uriInputResource: InputResource[URI] = InputResource[URL].contramap(_.toURL)
}
