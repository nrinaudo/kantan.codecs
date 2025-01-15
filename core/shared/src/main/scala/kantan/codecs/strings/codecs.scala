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

import java.util.UUID
import java.util.regex.Pattern
import scala.reflect.ClassTag
import scala.util.matching.Regex

/** Defines default instances for [[StringEncoder]] and [[StringDecoder]]. */
object codecs extends PlatformSpecificInstances {

  /** Defines a [[StringCodec]] instance for Java enumerations.
    *
    * @example
    *   {{{
    * scala> import java.nio.file.AccessMode
    *
    * // Decoding example
    * scala> StringDecoder[AccessMode].decode("READ")
    * res1: StringResult[AccessMode] = Right(READ)
    *
    * // Encoding example
    * scala> StringEncoder[AccessMode].encode(AccessMode.READ)
    * res2: String = READ
    *   }}}
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  implicit def javaEnumStringCodec[T <: Enum[T]](implicit tag: ClassTag[T]): StringCodec[T] =
    StringCodec.from(StringDecoder.makeSafe("Enum") { s =>
      val enumClass = tag.runtimeClass.asInstanceOf[Class[T]]
      Enum.valueOf(enumClass, s)
    })(_.name())

  /** Defines a [[StringCodec]] instance for `Pattern`.
    *
    * @example
    *   {{{
    * scala> import java.util.regex.Pattern
    *
    * // Decoding example
    * scala> StringDecoder[Pattern].decode("[a-z]")
    * res1: StringResult[Pattern] = Right([a-z])
    *
    * // Encoding example
    * scala> StringEncoder[Pattern].encode(Pattern.compile("[a-z]"))
    * res2: String = [a-z]
    *   }}}
    */
  implicit val patternStringCodec: StringCodec[Pattern] =
    StringCodec.from(StringDecoder.makeSafe("Pattern")(s => Pattern.compile(s.trim)))(_.pattern())

  /** Defines a [[StringCodec]] instance for `Regex`.
    *
    * @example
    *   {{{
    * scala> import scala.util.matching.Regex
    *
    * // Decoding example
    * scala> StringDecoder[Regex].decode("[a-z]")
    * res1: StringResult[Regex] = Right([a-z])
    *
    * // Encoding example
    * scala> StringEncoder[Regex].encode("[a-z]".r)
    * res2: String = [a-z]
    *   }}}
    */
  implicit val regexStringCodec: StringCodec[Regex] =
    StringCodec.from(StringDecoder.makeSafe("Regex")(s => s.trim.r))(_.pattern.pattern())

  /** Defines a [[StringCodec]] instance for `BigDecimal`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[BigDecimal].decode("2")
    * res1: StringResult[BigDecimal] = Right(2)
    *
    * // Encoding example
    * scala> StringEncoder[BigDecimal].encode(BigDecimal(2D))
    * res2: String = 2.0
    *   }}}
    */
  implicit val bigDecimalStringCodec: StringCodec[BigDecimal] =
    StringCodec.from(StringDecoder.makeSafe("BigDecimal")(s => BigDecimal(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `BigInt`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[BigInt].decode("2")
    * res1: StringResult[BigInt] = Right(2)
    *
    * // Encoding example
    * scala> StringEncoder[BigInt].encode(BigInt(2))
    * res2: String = 2
    *   }}}
    */
  implicit val bigIntStringCodec: StringCodec[BigInt] =
    StringCodec.from(StringDecoder.makeSafe("BigInt")(s => BigInt(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `Boolean`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[Boolean].decode("true")
    * res1: StringResult[Boolean] = Right(true)
    *
    * // Encoding example
    * scala> StringEncoder[Boolean].encode(true)
    * res2: String = true
    *   }}}
    */
  implicit val booleanStringCodec: StringCodec[Boolean] =
    StringCodec.from(StringDecoder.makeSafe("Boolean")(s => s.trim.toBoolean))(_.toString)

  /** Defines a [[StringCodec]] instance for `Char`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[Char].decode("a")
    * res1: StringResult[Char] = Right(a)
    *
    * // Encoding example
    * scala> StringEncoder[Char].encode('a')
    * res2: String = a
    *   }}}
    */
  implicit val charStringCodec: StringCodec[Char] = StringCodec.from { s =>
    // This is a bit dodgy, but necessary: if the string has a length greater than 1, it might be a legal character with
    // padding. The only issue is if the character is *whitespace* with whitespace padding. This is acknowledged and
    // willfully ignored, at least for the time being.
    val t = if(s.length > 1) s.trim else s
    if(t.length == 1) Right(t.charAt(0))
    else Left(DecodeError(s"Not a valid Char: '$s'"))
  }(_.toString)

  /** Defines a [[StringCodec]] instance for `Double`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[Double].decode("2")
    * res1: StringResult[Double] = Right(2.0)
    *
    * // Encoding example
    * scala> StringEncoder[Double].encode(2D)
    * res2: String = 2.0
    *   }}}
    */
  implicit val doubleStringCodec: StringCodec[Double] =
    StringCodec.from(StringDecoder.makeSafe("Double")(s => java.lang.Double.parseDouble(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `Byte`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[Byte].decode("2")
    * res1: StringResult[Byte] = Right(2)
    *
    * // Encoding example
    * scala> StringEncoder[Byte].encode(2)
    * res2: String = 2
    *   }}}
    */
  implicit val byteStringCodec: StringCodec[Byte] =
    StringCodec.from(StringDecoder.makeSafe("Byte")(s => java.lang.Byte.parseByte(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `Float`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[Float].decode("2")
    * res1: StringResult[Float] = Right(2.0)
    *
    * // Encoding example
    * scala> StringEncoder[Float].encode(2F)
    * res2: String = 2.0
    *   }}}
    */
  implicit val floatStringCodec: StringCodec[Float] =
    StringCodec.from(StringDecoder.makeSafe("Float")(s => java.lang.Float.parseFloat(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `Int`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[Int].decode("2")
    * res1: StringResult[Int] = Right(2)
    *
    * // Encoding example
    * scala> StringEncoder[Int].encode(2)
    * res2: String = 2
    *   }}}
    */
  implicit val intStringCodec: StringCodec[Int] =
    StringCodec.from(StringDecoder.makeSafe("Int")(s => Integer.parseInt(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `Long`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[Long].decode("2")
    * res1: StringResult[Long] = Right(2)
    *
    * // Encoding example
    * scala> StringEncoder[Long].encode(2L)
    * res2: String = 2
    *   }}}
    */
  implicit val longStringCodec: StringCodec[Long] =
    StringCodec.from(StringDecoder.makeSafe("Long")(s => java.lang.Long.parseLong(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `Short`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[Short].decode("2")
    * res1: StringResult[Short] = Right(2)
    *
    * // Encoding example
    * scala> StringEncoder[Short].encode(2.toShort)
    * res2: String = 2
    *   }}}
    */
  implicit val shortStringCodec: StringCodec[Short] =
    StringCodec.from(StringDecoder.makeSafe("Short")(s => java.lang.Short.parseShort(s.trim)))(_.toString)

  /** Defines a [[StringCodec]] instance for `String`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> StringDecoder[String].decode("foobar")
    * res1: StringResult[String] = Right(foobar)
    *
    * // Encoding example
    * scala> StringEncoder[String].encode("foobar")
    * res2: String = foobar
    *   }}}
    */
  implicit val stringStringCodec: StringCodec[String] =
    StringCodec.from(s => Right(s))(identity)

  /** Defines a [[StringCodec]] instance for `java.util.UUID`.
    *
    * @example
    *   {{{
    * // Decoding example
    * scala> import java.util.UUID
    *
    * scala> StringDecoder[UUID].decode("123e4567-e89b-12d3-a456-426655440000")
    * res1: StringResult[UUID] = Right(123e4567-e89b-12d3-a456-426655440000)
    *
    * // Encoding example
    * scala> StringEncoder[UUID].encode(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"))
    * res2: String = 123e4567-e89b-12d3-a456-426655440000
    *   }}}
    */
  implicit val uuidStringCodec: StringCodec[UUID] =
    StringCodec.from(StringDecoder.makeSafe("UUID")(s => UUID.fromString(s.trim)))(_.toString)

}
