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
import java.net.{URI, URL}
import java.nio.file.{Path, Paths}
import java.text.DateFormat
import java.util.{Date, UUID}
import kantan.codecs.{Codec, Result}

object StringCodec {
  @deprecated("use from instead (see https://github.com/nrinaudo/kantan.codecs/issues/22)", "0.1.8")
  def apply[D](f: String ⇒ Result[DecodeError, D])(g: D ⇒ String): StringCodec[D] = from(f)(g)
  def from[D](f: String ⇒ Result[DecodeError, D])(g: D ⇒ String): StringCodec[D] = Codec.from(f)(g)
}

/** Defines default instances of [[StringCodec]] for all primitive types. */
trait StringCodecInstances extends StringEncoderInstances with StringDecoderInstances {
  implicit val bigDecimal: StringCodec[BigDecimal] =
    StringCodec.from(StringDecoder.decoder("BigDecimal")(s ⇒ BigDecimal(s.trim)))(_.toString)

  implicit val bigInt: StringCodec[BigInt] =
    StringCodec.from(StringDecoder.decoder("BigInt")(s ⇒ BigInt(s.trim)))(_.toString)

  implicit val boolean: StringCodec[Boolean] =
    StringCodec.from(StringDecoder.decoder("Boolean")(s ⇒ s.trim.toBoolean))(_.toString)

  implicit val char: StringCodec[Char] = StringCodec.from { s ⇒
    // This is a bit dodgy, but necessary: if the string has a length greater than 1, it might be a legal character with
    // padding. The only issue is if the character is *whitespace* with whitespace padding. This is acknowledged and
    // willfully ignored, at least for the time being.
    val t = if(s.length > 1) s.trim else s
    if(t.length == 1) Result.success(t.charAt(0))
    else              Result.failure(DecodeError(s"Not a valid Char: '$s'"))
  }(_.toString)

  implicit val double: StringCodec[Double] =
    StringCodec.from(StringDecoder.decoder("Double")(s ⇒ s.trim.toDouble))(_.toString)

  implicit val byte: StringCodec[Byte] =
    StringCodec.from(StringDecoder.decoder("Byte")(s ⇒ s.trim.toByte))(_.toString)

  implicit val float: StringCodec[Float] =
    StringCodec.from(StringDecoder.decoder("Float")(s ⇒ s.trim.toFloat))(_.toString)

  implicit val int: StringCodec[Int] =
    StringCodec.from(StringDecoder.decoder("Int")(s ⇒ s.trim.toInt))(_.toString)

  implicit val long: StringCodec[Long] =
    StringCodec.from(StringDecoder.decoder("Long")(s ⇒ s.trim.toLong))(_.toString)

  implicit val short: StringCodec[Short] =
    StringCodec.from(StringDecoder.decoder("Short")(s ⇒ s.trim.toShort))(_.toString)

  implicit val string: StringCodec[String] =
    StringCodec.from(s ⇒ Result.success(s))(_.toString)

  implicit val uuid: StringCodec[UUID] =
    StringCodec.from(StringDecoder.decoder("UUID")(s ⇒ UUID.fromString(s.trim)))(_.toString)

  implicit val url: StringCodec[URL] =
    StringCodec.from(StringDecoder.decoder("URL")(s ⇒ new URL(s.trim)))(_.toString)

  implicit val uri: StringCodec[URI] =
    StringCodec.from(StringDecoder.decoder("URI")(s ⇒ new URI(s.trim)))(_.toString)

  implicit val file: StringCodec[File] =
    StringCodec.from(StringDecoder.decoder("File")(s ⇒ new File(s.trim)))(_.toString)

  implicit val path: StringCodec[Path] =
    StringCodec.from(StringDecoder.decoder("Path")(p ⇒ Paths.get(p.trim)))(_.toString)


  implicit def date(implicit ft: DateFormat): StringCodec[Date] =
    StringCodec.from(StringDecoder.decoder("Date")(s ⇒ ft.synchronized(ft.parse(s))))(d ⇒ ft.synchronized(ft.format(d)))
}
