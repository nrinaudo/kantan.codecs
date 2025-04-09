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

import kantan.codecs.error.IsError
import kantan.codecs.`export`.DerivedDecoder

/** Type class for types that can be decoded from other types.
  *
  * @tparam E
  *   encoded type - what to decode from.
  * @tparam D
  *   decoded type - what to decode to.
  * @tparam F
  *   failure type - how to represent errors.
  * @tparam T
  *   tag type - used to specialise decoder instances, and usually where default implementations are declared.
  */
trait Decoder[E, D, F, T] extends Serializable {
  // - Decoding --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Decodes encoded data.
    *
    * This method is safe, in that it won't throw for run-of-the-mill errors. Unrecoverable errors such as out of memory
    * exceptions are still thrown, but that's considered valid exceptional cases, where incorrectly encoded data is
    * just... normal.
    *
    * Callers that wish to fail fast and fail hard can use the [[unsafeDecode]] method instead.
    */
  def decode(e: E): Either[F, D]

  /** Decodes encoded data unsafely.
    *
    * The main difference between this and [[decode]] is that the former throws exceptions when errors occur where the
    * later safely encodes error conditions in its return type.
    *
    * [[decode]] should almost always be preferred, but this can be useful for code where crashing is an acceptable
    * reaction to failure.
    */
  @SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
  def unsafeDecode(e: E): D =
    decode(e).fold(
      error => sys.error(s"Failed to decode value $e: $error"),
      d => d
    )

  // - Composition -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Creates a new [[Decoder]] instance with a fallback decoder if the current one fails. */
  def orElse[DD >: D](d: Decoder[E, DD, F, T]): Decoder[E, DD, F, T] =
    Decoder.from(e => decode(e).left.flatMap(_ => d.decode(e)))

  /** Creates a new [[Decoder]] instance by transforming raw results with the specified function.
    *
    * Most of the time, other combinators such as [[map]] should be preferred. [[andThen]] is mostly useful when one
    * needs to turn failures into successes, and even then, [[recover]] or [[recoverWith]] are probably more directly
    * useful.
    */
  def andThen[FF, DD](f: Either[F, D] => Either[FF, DD]): Decoder[E, DD, FF, T] =
    Decoder.from(e => f(decode(e)))

  /** Creates a new [[Decoder]] instance by transforming some failures into successes with the specified function. */
  def recover[DD >: D](pf: PartialFunction[F, DD]): Decoder[E, DD, F, T] =
    andThen(_.left.flatMap { f =>
      if(pf.isDefinedAt(f)) Right(pf(f))
      else Left(f)
    })

  /** Creates a new [[Decoder]] instance by transforming some failures with the specified function. */
  def recoverWith[DD >: D, FF >: F](pf: PartialFunction[F, Either[FF, DD]]): Decoder[E, DD, FF, T] =
    andThen(_.left.flatMap { f =>
      if(pf.isDefinedAt(f)) pf(f)
      else Left(f)
    })

  def handleErrorWith(f: F => Decoder[E, D, F, T]): Decoder[E, D, F, T] =
    Decoder.from { s =>
      decode(s).left.flatMap(error => f(error).decode(s))
    }

  /** Creates a new [[Decoder]] instance by transforming successful results with the specified function.
    *
    * This differs from [[emap]] in that the transformation function cannot fail.
    */
  def map[DD](f: D => DD): Decoder[E, DD, F, T] =
    andThen(_.map(f))

  @deprecated("Use emap instead", "0.2.0")
  def mapResult[DD](f: D => Either[F, DD]): Decoder[E, DD, F, T] =
    emap(f)

  /** Creates a new [[Decoder]] instance by transforming successful results with the specified function.
    *
    * This differs from [[map]] in that it allows the transformation function to fail.
    */
  def emap[DD](f: D => Either[F, DD]): Decoder[E, DD, F, T] =
    andThen(_.flatMap(f))

  /** Applies the specified partial function, turning all non-matches to failures.
    *
    * You can think as [[collect]] as a bit like a [[filter]] and a [[map]] merged into one.
    */
  @SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
  def collect[DD](f: PartialFunction[D, DD])(implicit t: IsError[F]): Decoder[E, DD, F, T] =
    emap { d =>
      if(f.isDefinedAt(d)) Right(f(d))
      else Left(t.fromMessage(s"Not acceptable: '$d'"))
    }

  /** Turns all values that don't match the specified predicates into failures.
    *
    * See [[collect]] if you wish to transform the values in the same call.
    */
  def filter(f: D => Boolean)(implicit t: IsError[F]): Decoder[E, D, F, T] =
    collect {
      case d if f(d) => d
    }

  @deprecated("Use leftMap instead", "0.2.0")
  def mapError[FF](f: F => FF): Decoder[E, D, FF, T] =
    leftMap(f)

  /** Creates a new [[Decoder]] instance by transforming errors with the specified function. */
  def leftMap[FF](f: F => FF): Decoder[E, D, FF, T] =
    andThen(_.left.map(f))

  /** Creates a new [[Decoder]] instance by transforming encoded values with the specified function. */
  def contramapEncoded[EE](f: EE => E): Decoder[EE, D, F, T] =
    Decoder.from(f.andThen(decode))

  /** Changes the type with which the decoder is tagged.
    *
    * This makes it possible to share similar decoders across various libraries. Extracting values from strings, for
    * example, is a common task for which the default implementation can be shared rather than copy / pasted.
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def tag[TT]: Decoder[E, D, F, TT] =
    this.asInstanceOf[Decoder[E, D, F, TT]]

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def flatMap[DD](f: D => Decoder[E, DD, F, T]): Decoder[E, DD, F, T] =
    Decoder.from { s =>
      decode(s) match {
        case Right(d)    => f(d).decode(s)
        case l @ Left(_) => l.asInstanceOf[Either[F, DD]]
      }
    }

  def product[DD](decoder: Decoder[E, DD, F, T]): Decoder[E, (D, DD), F, T] =
    Decoder.from { s =>
      for {
        d  <- decode(s)
        dd <- decoder.decode(s)
      } yield (d, dd)
    }
}

/** Provides methods commonly declared by companion objects for specialised decoder types.
  *
  * Most libraries that use kantan.codecs will declare type aliases for decoders - `CellDecoder` in kantan.csv, for
  * example. [[DecoderCompanion]] lets such types have a useful companion object without a lot of code duplication.
  */
trait DecoderCompanion[E, F, T] extends Serializable {

  /** Summons an implicit instance of [[Decoder]] if one is found, fails compilation otherwise.
    *
    * This is a slightly faster, less verbose version of `implicitly`.
    */
  def apply[D](implicit ev: Decoder[E, D, F, T]): Decoder[E, D, F, T] =
    ev

  /** Creates a new [[Decoder]] instance from the specified function. */
  @inline def from[D](f: E => Either[F, D]): Decoder[E, D, F, T] =
    Decoder.from(f)

  /** Creates a new [[Decoder]] instance from the specified function.
    *
    * This method turns the specified function safe. The error message might end up being a bit generic though - use
    * [[from]] if you want to deal with errors explicitly.
    */
  @inline def fromUnsafe[D](f: E => D)(implicit t: IsError[F]): Decoder[E, D, F, T] =
    Decoder.fromUnsafe(f)

  @SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
  def fromPartial[D](f: PartialFunction[E, Either[F, D]])(implicit t: IsError[F]): Decoder[E, D, F, T] =
    Decoder.from { e =>
      if(f.isDefinedAt(e)) f(e)
      else Left(t.fromMessage(s"Not acceptable: '$e'"))
    }

  /** Creates a new [[Decoder]] instance from the specified alternatives.
    *
    * When decoding, each of the specified decoders will be attempted. The result will be the first success if found, or
    * the last failure otherwise.
    */
  @inline def oneOf[D](ds: Decoder[E, D, F, T]*)(implicit i: IsError[F]): Decoder[E, D, F, T] =
    Decoder.oneOf(ds: _*)
}

object Decoder {

  /** Creates a new [[Decoder]] instance that applies the specified function when decoding. */
  def from[E, D, F, T](f: E => Either[F, D]): Decoder[E, D, F, T] =
    new Decoder[E, D, F, T] {
      override def decode(e: E) =
        f(e)
    }

  /** Turns an unsafe function into a [[Decoder]].
    *
    * The specified function is assumed to throw, and errors will be dealt with properly.
    */
  def fromUnsafe[E, D, F: IsError, T](f: E => D): Decoder[E, D, F, T] =
    Decoder.from(e => IsError[F].safe(f(e)))

  implicit def decoderFromExported[E, D, F, T](implicit da: DerivedDecoder[E, D, F, T]): Decoder[E, D, F, T] =
    da.value

  implicit def optionalDecoder[E: Optional, D, F, T](implicit da: Decoder[E, D, F, T]): Decoder[E, Option[D], F, T] =
    Decoder.from { e =>
      if(Optional[E].isEmpty(e)) Right(None)
      else da.decode(e).map(Some.apply)
    }

  /** Provides a [[Decoder]] instance for `Either[A, B]`, provided both `A` and `B` have a [[Decoder]] instance. */
  implicit def eitherDecoder[E, D1, D2, F, T](implicit
    d1: Decoder[E, D1, F, T],
    d2: Decoder[E, D2, F, T]
  ): Decoder[E, Either[D1, D2], F, T] =
    d1.map(Left.apply).orElse(d2.map(Right.apply))

  /** Creates a new decoder using all specified values.
    *
    * The generated decoder will try each of the specified decoders in turn, and return either the first success or, if
    * none is found, the last failure.
    */
  @SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
  def oneOf[E, D, F: IsError, T](ds: Decoder[E, D, F, T]*): Decoder[E, D, F, T] =
    ds.reduceOption(_ orElse _)
      .getOrElse(Decoder.from(d => Left(IsError[F].fromMessage(s"Not acceptable: '$d'"))))
}
