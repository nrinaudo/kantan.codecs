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

package kantan.codecs.strings

import java.io.File
import java.net.{URI, URL}
import java.nio.file.{Path, Paths}
import java.text.DateFormat
import java.util.{Date, UUID}
import kantan.codecs.{Codec, Result}

object StringCodec {
  def from[D](f: String ⇒ Result[DecodeError, D])(g: D ⇒ String): StringCodec[D] = Codec.from(f)(g)
  def from[D](d: StringDecoder[D], e: StringEncoder[D]): StringCodec[D] = Codec.from(d, e)

  def dateCodec(format: DateFormat): StringCodec[Date] =
    StringCodec.from(StringDecoder.dateDecoder(format), StringEncoder.dateEncoder(format))
}

/** Defines default instances of [[StringCodec]] for all primitive types. */
trait StringCodecInstances extends StringEncoderInstances with StringDecoderInstances {
  implicit val bigDecimalCodec: StringCodec[BigDecimal] =
    StringCodec.from(StringDecoder.decoder("BigDecimal")(s ⇒ BigDecimal(s.trim)))(_.toString)

  implicit val bigIntCodec: StringCodec[BigInt] =
    StringCodec.from(StringDecoder.decoder("BigInt")(s ⇒ BigInt(s.trim)))(_.toString)

  implicit val booleanCodec: StringCodec[Boolean] =
    StringCodec.from(StringDecoder.decoder("Boolean")(s ⇒ s.trim.toBoolean))(_.toString)

  implicit val charCodec: StringCodec[Char] = StringCodec.from { s ⇒
    // This is a bit dodgy, but necessary: if the string has a length greater than 1, it might be a legal character with
    // padding. The only issue is if the character is *whitespace* with whitespace padding. This is acknowledged and
    // willfully ignored, at least for the time being.
    val t = if(s.length > 1) s.trim else s
    if(t.length == 1) Result.success(t.charAt(0))
    else              Result.failure(DecodeError(s"Not a valid Char: '$s'"))
  }(_.toString)

  implicit val doubleCodec: StringCodec[Double] =
    StringCodec.from(StringDecoder.decoder("Double")(s ⇒ s.trim.toDouble))(_.toString)

  implicit val byteCodec: StringCodec[Byte] =
    StringCodec.from(StringDecoder.decoder("Byte")(s ⇒ s.trim.toByte))(_.toString)

  implicit val floatCodec: StringCodec[Float] =
    StringCodec.from(StringDecoder.decoder("Float")(s ⇒ s.trim.toFloat))(_.toString)

  implicit val intCodec: StringCodec[Int] =
    StringCodec.from(StringDecoder.decoder("Int")(s ⇒ s.trim.toInt))(_.toString)

  implicit val longCodec: StringCodec[Long] =
    StringCodec.from(StringDecoder.decoder("Long")(s ⇒ s.trim.toLong))(_.toString)

  implicit val shortCodec: StringCodec[Short] =
    StringCodec.from(StringDecoder.decoder("Short")(s ⇒ s.trim.toShort))(_.toString)

  implicit val stringCodec: StringCodec[String] =
    StringCodec.from(s ⇒ Result.success(s))(_.toString)

  implicit val uuidCodec: StringCodec[UUID] =
    StringCodec.from(StringDecoder.decoder("UUID")(s ⇒ UUID.fromString(s.trim)))(_.toString)

  implicit val urlCodec: StringCodec[URL] =
    StringCodec.from(StringDecoder.decoder("URL")(s ⇒ new URL(s.trim)))(_.toString)

  implicit val uriCodec: StringCodec[URI] =
    StringCodec.from(StringDecoder.decoder("URI")(s ⇒ new URI(s.trim)))(_.toString)

  implicit val fileCodec: StringCodec[File] =
    StringCodec.from(StringDecoder.decoder("File")(s ⇒ new File(s.trim)))(_.toString)

  implicit val pathCodec: StringCodec[Path] =
    StringCodec.from(StringDecoder.decoder("Path")(p ⇒ Paths.get(p.trim)))(_.toString)
}
