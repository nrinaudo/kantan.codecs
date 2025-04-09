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

package kantan.codecs.strings

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.text.DateFormat
import java.util.Date

/** JVM-specific codec instances. */
trait PlatformSpecificInstances {

  /** Defines a [[StringCodec]] instance for `java.net.URL`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> import java.net.URL
    *
    * scala> import java.net.URI
    *
    * scala> StringDecoder[URL].decode("http://localhost:8080")
    * res1: StringResult[URL] = Right(http://localhost:8080)
    *
    * // Encoding example
    * scala> StringEncoder[URL].encode(new URI("http://localhost:8080").toURL)
    * res2: String = http://localhost:8080
    *   }}}
    */
  implicit val urlStringCodec: StringCodec[URL] =
    StringCodec.from(StringDecoder.makeSafe("URL")(s => new URI(s.trim).toURL()))(_.toString)

  /** Defines a [[StringCodec]] instance for `java.net.URI`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> import java.net.URI
    *
    * scala> StringDecoder[URI].decode("http://localhost:8080")
    * res1: StringResult[URI] = Right(http://localhost:8080)
    *
    * // Encoding example
    * scala> StringEncoder[URI].encode(new URI("http://localhost:8080"))
    * res2: String = http://localhost:8080
    *   }}}
    */
  implicit val uriStringCodec: StringCodec[URI] =
    StringCodec.from(StringDecoder.makeSafe("URI")(s => new URI(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `java.io.File`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> import java.io.File
    *
    * scala> StringDecoder[File].decode("/home/nrinaudo")
    * res1: StringResult[File] = Right(/home/nrinaudo)
    *
    * // Encoding example
    * scala> StringEncoder[File].encode(new File("/home/nrinaudo"))
    * res2: String = /home/nrinaudo
    *   }}}
    */
  implicit val fileStringCodec: StringCodec[File] =
    StringCodec.from(StringDecoder.makeSafe("File")(s => new File(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `java.nio.file.Path`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> import java.nio.file.{Path, Paths}
    *
    * scala> StringDecoder[Path].decode("/home/nrinaudo")
    * res1: StringResult[Path] = Right(/home/nrinaudo)
    *
    * // Encoding example
    * scala> StringEncoder[Path].encode(Paths.get("/home/nrinaudo"))
    * res2: String = /home/nrinaudo
    *   }}}
    */
  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  implicit val pathStringCodec: StringCodec[Path] =
    StringCodec.from(StringDecoder.makeSafe("Path")(p => Paths.get(p.trim)))(_.toString)

}

/** JVM-specific [[StringDecoder decoders]]. */
trait PlatformSpecificDecoders {

  /** Creates a [[StringDecoder]] instance for `java.util.Date`. */
  def dateDecoder(format: DateFormat): StringDecoder[Date] =
    StringDecoder.from(StringDecoder.makeSafe("Date")(s => format.synchronized(format.parse(s))))

}

/** JVM-specific [[StringEncoder encoders]]. */
trait PlatformSpecificEncoders {

  def dateEncoder(format: DateFormat): StringEncoder[Date] =
    StringEncoder.from(d => format.synchronized(format.format(d)))

}

/** JVM-specific [[StringCodec codecs]]. */
trait PlatformSpecificCodecs {

  /** Creates a [[StringCodec]] instance for `java.util.Date`. */
  def dateCodec(format: DateFormat): StringCodec[Date] =
    StringCodec.from(StringDecoder.dateDecoder(format), StringEncoder.dateEncoder(format))

}
