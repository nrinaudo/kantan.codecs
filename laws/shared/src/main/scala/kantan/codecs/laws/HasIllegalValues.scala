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

package kantan.codecs.laws

import imp.imp
import java.net.URI
import java.util.UUID
import kantan.codecs.laws.CodecValue.IllegalValue
import scala.reflect.ClassTag

/** Type class that describes types for which values in the encoded type exist that cannot be decoded.
  *
  * The main purpose of this type class is to allow for automatic derivation of `Arbitrary` and `Shrink` instances
  * of [[CodecValue.IllegalValue]] for use in laws. It's possible to write these instances manually, but
  * `HasIllegalValues` simplifies the process for a lot of scenarios.
  *
  * Default instances are provided for standard types encoded as strings. In order to re-use them for codecs that are
  * essentially wrapper for string codecs, you can stick the following somewhere in the implicit context:
  * {{{
  * implicit def hasIllegalValues[D](implicit hiv: HasIllegalStringValues[D]): HasIllegalValues[String, D, mycodec.type] =
  *   hiv.tag[mycodec..type]
  * }}}
  */
trait HasIllegalValues[Encoded, Decoded, Tag] {

  /** Checks whether the corresponding encoded value can be decoded. */
  def isValid(e: Encoded): Boolean

  def isInvalid(e: Encoded): Boolean = !isValid(e)

  /** Makes the specified encoded value illegal.
    *
    * The specified encoded value is expected to be valid (in the sense of [[isValid]]).
    */
  def perturb(e: Encoded): Encoded

  def asIllegalValue(e: Encoded): IllegalValue[Encoded, Decoded, Tag] = IllegalValue(
    if(isValid(e)) perturb(e)
    else e
  )

  /** Changes the instance's tag type.
    *
    * This is meant to allow developers of codecs that rely on a pre-existing one (such as CSV relies on String codecs)
    * to re-use existing instances at 0 cost.
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def tag[T]: HasIllegalValues[Encoded, Decoded, T] = this.asInstanceOf[HasIllegalValues[Encoded, Decoded, T]]
}

/** Provides default instances, as well as instance creation helpers. */
object HasIllegalValues extends PlatformSpecificHasIllegalValues {

  /** Creates a new instance based on the specified (unsafe) decoding function.
    *
    * If the specified function throws, the corresponding value is considered invalid.
    */
  def fromUnsafeString[D, U](decode: String => U): HasIllegalStringValues[D] =
    fromString(e => scala.util.Try(decode(e)).isSuccess)

  /** Creates a new instance based on the specified validity checking function. */
  def fromString[D](valid: String => Boolean): HasIllegalStringValues[D] =
    new HasIllegalStringValues[D] {
      override def isValid(e: String) = valid(e)
      override def perturb(e: String) = s"perturbed-$e"

    }

  // - Default instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val int: HasIllegalStringValues[Int]               = fromUnsafeString(Integer.parseInt)
  implicit val short: HasIllegalStringValues[Short]           = fromUnsafeString(java.lang.Short.parseShort)
  implicit val uuid: HasIllegalStringValues[UUID]             = fromUnsafeString(UUID.fromString)
  implicit val long: HasIllegalStringValues[Long]             = fromUnsafeString(java.lang.Long.parseLong)
  implicit val float: HasIllegalStringValues[Float]           = fromUnsafeString(java.lang.Float.parseFloat)
  implicit val double: HasIllegalStringValues[Double]         = fromUnsafeString(java.lang.Double.parseDouble)
  implicit val byte: HasIllegalStringValues[Byte]             = fromUnsafeString(java.lang.Byte.parseByte)
  implicit val boolean: HasIllegalStringValues[Boolean]       = fromUnsafeString(java.lang.Boolean.parseBoolean)
  implicit val bigInt: HasIllegalStringValues[BigInt]         = fromUnsafeString(BigInt.apply)
  implicit val bigDecimal: HasIllegalStringValues[BigDecimal] = fromUnsafeString(BigDecimal.apply)
  implicit val uri: HasIllegalStringValues[URI]               = fromUnsafeString(e => new URI(e))
  implicit val char: HasIllegalStringValues[Char]             = fromString(_.length == 1)

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  implicit def javaEnum[T <: Enum[T]](implicit tag: ClassTag[T]): HasIllegalStringValues[T] =
    fromUnsafeString(str => Enum.valueOf(tag.runtimeClass.asInstanceOf[Class[T]], str))

  implicit def option[D: HasIllegalStringValues]: HasIllegalStringValues[Option[D]] =
    fromString(str => str.isEmpty || imp[HasIllegalStringValues[D]].isValid(str))

  implicit def either[L: HasIllegalStringValues, R: HasIllegalStringValues]: HasIllegalStringValues[Either[L, R]] =
    fromString { str =>
      imp[HasIllegalStringValues[L]].isValid(str) || imp[HasIllegalStringValues[R]].isValid(str)
    }
}
