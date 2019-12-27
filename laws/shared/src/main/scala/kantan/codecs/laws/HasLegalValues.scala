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

import java.net.URI
import java.util.UUID
import kantan.codecs.Optional
import kantan.codecs.laws.CodecValue.LegalValue

/** Type class that describes types for which legal values encoded / decoded couples exist.
  *
  * The main purpose of this type class is to allow for automatic derivation of `Arbitrary` and `Shrink` instances
  * of [[CodecValue.LegalValue]] for use in laws. It's possible to write these instances manually, but `HasLegalValues`
  * simplifies the process for a lot of scenarios.
  *
  * Default instances are provided for standard types encoded as strings. In order to re-use them for codecs that are
  * essentially wrapper for string codecs, you can stick the following somewhere in the implicit context:
  * {{{
  * implicit def hasLegalValues[D](implicit hlv: HasLegalStringValues[D]): HasLegalValues[String, D, mycodec.type] =
  *   hlv.tag[mycodec..type]
  * }}}
  */
trait HasLegalValues[Encoded, Decoded, Tag] {

  /** Encodes the specified value as a legal `Encoded`.
    *
    * Note that this might seem like a re-implementation of something that is already available in `Encoder`, but this
    * is done on purpose. `HasLegalValues` are typically used to test the behaviour of `Encoder`, and testing an
    * implementation against itself is counter-productive.
    *
    * Note that this might seem to contradict the entire "don't re-implement the system under test" rule of property
    * based testing. This is true, *but* in the vast majority of the time, you'll have a default, trivial encoding
    * method (`toString`, most of the time), which might be refined by the encoder themselves. We're not exactly
    * re-implementing the system under test, but providing a framework for testing against a pre-existing oracle.
    */
  def encode(d: Decoded): Encoded

  def asLegalValue(d: Decoded): LegalValue[Encoded, Decoded, Tag] = LegalValue(encode(d), d)

  /** Changes the instance's tag type.
    *
    * This is meant to allow developers of codecs that rely on a pre-existing one (such as CSV relies on String codecs)
    * to re-use existing instances at 0 cost.
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def tag[T]: HasLegalValues[Encoded, Decoded, T] = this.asInstanceOf[HasLegalValues[Encoded, Decoded, T]]
}

/** Provides default instances, as well as instance creation helpers. */
object HasLegalValues extends PlatformSpecificHasLegalValues {

  /** Creates a new instance from the specified encoding function. */
  def from[E, D, T](f: D => E): HasLegalValues[E, D, T] = new HasLegalValues[E, D, T] {
    override def encode(d: D) = f(d)
  }

  /** Creates an instance who considers `toString` to be the encoding method for the specified type. */
  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def fromToString[D, U]: HasLegalStringValues[D] = from(_.toString)

  // - Default instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val string: HasLegalStringValues[String]            = fromToString
  implicit val uuid: HasLegalStringValues[UUID]                = fromToString
  implicit val int: HasLegalStringValues[Int]                  = fromToString
  implicit val short: HasLegalStringValues[Short]              = fromToString
  implicit val long: HasLegalStringValues[Long]                = fromToString
  implicit val float: HasLegalStringValues[Float]              = fromToString
  implicit val double: HasLegalStringValues[Double]            = fromToString
  implicit val byte: HasLegalStringValues[Byte]                = fromToString
  implicit val boolean: HasLegalStringValues[Boolean]          = fromToString
  implicit val bigInt: HasLegalStringValues[BigInt]            = fromToString
  implicit val bigDecimal: HasLegalStringValues[BigDecimal]    = fromToString
  implicit val uri: HasLegalStringValues[URI]                  = fromToString
  implicit val char: HasLegalStringValues[Char]                = fromToString
  implicit def javaEnum[T <: Enum[T]]: HasLegalStringValues[T] = from(_.name())

  implicit def option[E: Optional, D, T](implicit hl: HasLegalValues[E, D, T]): HasLegalValues[E, Option[D], T] = from {
    _.map(hl.encode)
      .getOrElse(Optional[E].empty)
  }

  implicit def either[E, L, R, T](
    implicit hll: HasLegalValues[E, L, T],
    hlr: HasLegalValues[E, R, T]
  ): HasLegalValues[E, Either[L, R], T] = from {
    case Left(l)  => hll.encode(l)
    case Right(r) => hlr.encode(r)
  }

}
