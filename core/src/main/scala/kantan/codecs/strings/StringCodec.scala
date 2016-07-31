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
import java.text.DateFormat
import java.util.{Date, UUID}
import kantan.codecs.{Codec, Result}

object StringCodec {
  def apply[D](f: String ⇒ Result[DecodeError, D])(g: D ⇒ String): StringCodec[D] = Codec(f)(g)
}

/** Defines default instances of [[StringCodec]] for all primitive types. */
trait StringCodecInstances extends StringEncoderInstances with StringDecoderInstances {
  private def failure(input: String, tpe: String): DecodeError =
    DecodeError(s"Not a valid $tpe: '$input'")

  implicit val bigDecimal: StringCodec[BigDecimal] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "BigDecimal"))(BigDecimal(s.trim)))(_.toString)

  implicit val bigInt: StringCodec[BigInt] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "BigInt"))(BigInt(s.trim)))(_.toString)

  implicit val boolean: StringCodec[Boolean] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "Boolean"))(s.trim.toBoolean))(_.toString)

  implicit val char: StringCodec[Char] = StringCodec { s ⇒
    // This is a bit dodgy, but necessary: if the string has a length greater than 1, it might be a legal character with
    // padding. The only issue is if the character is *whitespace* with whitespace padding. This is acknowledged and
    // willfully ignored, at least for the time being.
    val t = if(s.length > 1) s.trim else s
    if(t.length == 1) Result.success(t.charAt(0))
    else              Result.failure(failure(s, "Char"))
  }(_.toString)

  implicit val double: StringCodec[Double] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "Double"))(s.trim.toDouble))(_.toString)

  implicit val byte: StringCodec[Byte] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "Byte"))(s.trim.toByte))(_.toString)

  implicit val float: StringCodec[Float] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "Float"))(s.trim.toFloat))(_.toString)

  implicit val int: StringCodec[Int] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "Int"))(s.trim.toInt))(_.toString)

  implicit val long: StringCodec[Long] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "Long"))(s.trim.toLong))(_.toString)

  implicit val short: StringCodec[Short] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "Short"))(s.trim.toShort))(_.toString)

  implicit val string: StringCodec[String] =
    StringCodec(s ⇒ Result.success(s))(_.toString)

  implicit val uuid: StringCodec[UUID] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "UUID"))(UUID.fromString(s.trim)))(_.toString)

  implicit val url: StringCodec[URL] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "URL"))(new URL(s.trim)))(_.toString)

  implicit val uri: StringCodec[URI] = url.imap(_.toURI)(_.toURL)

  implicit val file: StringCodec[File] =
    StringCodec(s ⇒ Result.nonFatalOr(failure(s, "File"))(new File(s)))(_.toString)

  implicit def date(implicit ft: DateFormat): StringCodec[Date] =
    StringCodec { s ⇒
      Result.nonFatalOr(failure(s, "Date"))(ft.synchronized(ft.parse(s)))
    }(d ⇒ ft.synchronized(ft.format(d)))
}
