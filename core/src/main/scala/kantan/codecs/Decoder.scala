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

import kantan.codecs.export.DerivedDecoder

/** Type class for types that can be decoded from other types.
  *
  * @tparam E encoded type - what to decode from.
  * @tparam D decoded type - what to decode to.
  * @tparam F failure type - how to represent errors.
  * @tparam T tag type - used to specialise decoder instances, and usually where default implementations are declared.
  */
trait Decoder[E, D, F, T] extends Any with Serializable {
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
  def decode(e: E): Result[F, D]

  /** Decodes encoded data unsafely.
    *
    * The main difference between this and [[decode]] is that the former throws exceptions when errors occur where the
    * later safely encodes error conditions in its return type.
    *
    * [[decode]] should almost always be preferred, but this can be useful for code where crashing is an acceptable
    * reaction to failure.
    */
  def unsafeDecode(e: E): D = decode(e).get



  // - Composition -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Creates a new [[Decoder]] instance by transforming raw results with the specified function.
    *
    * Most of the time, other combinators such as [[map]] should be preferred. [[andThen]] is mostly useful when
    * one needs to turn failures into successes, and even then, [[recover]] or [[recoverWith]] are probably more
    * directly useful.
    */
  def andThen[FF, DD](f: Result[F, D] ⇒ Result[FF, DD]): Decoder[E, DD, FF, T] = Decoder.from(e ⇒ f(decode(e)))

  /** Creates a new [[Decoder]] instance by transforming some failures into successes with the specified function. */
  def recover[DD >: D](pf: PartialFunction[F, DD]): Decoder[E, DD, F, T] = andThen(_.recover(pf))

  /** Creates a new [[Decoder]] instance by transforming some failures with the specified function. */
  def recoverWith[DD >: D, FF >: F](pf: PartialFunction[F, Result[FF, DD]]): Decoder[E, DD, FF, T] =
    andThen(_.recoverWith(pf))

  /** Creates a new [[Decoder]] instance by transforming successful results with the specified function.
    *
    * This differs from [[mapResult]] in that the transformation function cannot fail.
    */
  def map[DD](f: D ⇒ DD): Decoder[E, DD, F, T] = andThen(_.map(f))

  /** Creates a new [[Decoder]] instance by transforming successful results with the specified function.
    *
    * This differs from [[map]] in that it allows the transformation function to fail.
    */
  def mapResult[DD](f: D ⇒ Result[F, DD]): Decoder[E, DD, F, T] = andThen(_.flatMap(f))

  /** Creates a new [[Decoder]] instance by transforming errors with the specified function. */
  def mapError[FF](f: F ⇒ FF): Decoder[E, D, FF, T] = andThen(_.leftMap(f))

  /** Creates a new [[Decoder]] instance by transforming encoded values with the specified function. */
  def contramapEncoded[EE](f: EE ⇒ E): Decoder[EE, D, F, T] = Decoder.from(f andThen decode)

  /** Changes the type with which the decoder is tagged.
    *
    * This makes it possible to share similar decoders across various libraries. Extracting values from strings, for
    * example, is a common task for which the default implementation can be shared rather than copy / pasted.
    */
  def tag[TT]: Decoder[E, D, F, TT] = this.asInstanceOf[Decoder[E, D, F, TT]]
}

object Decoder {
  @deprecated("use from instead (see https://github.com/nrinaudo/kantan.codecs/issues/22)", "0.1.8")
  def apply[E, D, F, T](f: E ⇒ Result[F, D]): Decoder[E, D, F, T] = Decoder.from(f)

  /** Creates a new [[Decoder]] instance that applies the specified function when decoding. */
  def from[E, D, F, T](f: E ⇒ Result[F, D]): Decoder[E, D, F, T] = new Decoder[E, D, F, T] {
    override def decode(e: E) = f(e)
  }

  implicit def decoderFromExported[E, D, F, T](implicit da: DerivedDecoder[E, D, F, T]): Decoder[E, D, F, T] =
    da.value

  implicit def optionalDecoder[E: Optional, D, F, T]
  (implicit da: Decoder[E, D, F, T]): Decoder[E, Option[D], F, T] =
    Decoder.from { e ⇒
      if(Optional[E].isEmpty(e)) Result.success(None)
      else                       da.decode(e).map(Some.apply)
    }

  /** Provides a [[Decoder]] instance for `Either[A, B]`, provided both `A` and `B` have a [[Decoder]] instance. */
  implicit def eitherDecoder[E, D, B, F, T](implicit da: Decoder[E, D, F, T], db: Decoder[E, B, F, T])
  : Decoder[E, Either[D, B], F, T] =
    Decoder.from { s ⇒
      da.decode(s).map(a ⇒ Left(a): Either[D, B]).orElse(db.decode(s).map(b ⇒ Right(b): Either[D, B]))
    }
}
