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

package kantan.codecs

import kantan.codecs.export.DerivedDecoder

/** Type class for types that can be decoded from other types.
  *
  * @tparam E encoded type - what to decode from.
  * @tparam D decoded type - what to decode to.
  * @tparam F failure type - how to represent errors.
  * @tparam T tag type - used to specialise decoder instances, and usually where default implementations are declared.
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
    * This differs from [[emap]] in that the transformation function cannot fail.
    */
  def map[DD](f: D ⇒ DD): Decoder[E, DD, F, T] = andThen(_.map(f))

  @deprecated("Use emap instead", "0.2.0")
  def mapResult[DD](f: D ⇒ Result[F, DD]): Decoder[E, DD, F, T] = emap(f)

  /** Creates a new [[Decoder]] instance by transforming successful results with the specified function.
    *
    * This differs from [[map]] in that it allows the transformation function to fail.
    */
  def emap[DD](f: D ⇒ Result[F, DD]): Decoder[E, DD, F, T] = andThen(_.flatMap(f))

  @deprecated("Use leftMap instead", "0.2.0")
  def mapError[FF](f: F ⇒ FF): Decoder[E, D, FF, T] = leftMap(f)

  /** Creates a new [[Decoder]] instance by transforming errors with the specified function. */
  def leftMap[FF](f: F ⇒ FF): Decoder[E, D, FF, T] = andThen(_.leftMap(f))

  /** Creates a new [[Decoder]] instance by transforming encoded values with the specified function. */
  def contramapEncoded[EE](f: EE ⇒ E): Decoder[EE, D, F, T] = Decoder.from(f andThen decode)

  /** Changes the type with which the decoder is tagged.
    *
    * This makes it possible to share similar decoders across various libraries. Extracting values from strings, for
    * example, is a common task for which the default implementation can be shared rather than copy / pasted.
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  def tag[TT]: Decoder[E, D, F, TT] = this.asInstanceOf[Decoder[E, D, F, TT]]
}

/** Provides methods commonly declared by companion objects for specialised decoder types.
  *
  * Most libraries that use kantan.codecs will declare type aliases for decoders - `CellDecoder` in kantan.csv, for
  * example. [[DecoderCompanion]] lets such types have a useful companion object without a lot of code duplication.
  */
trait DecoderCompanion[E, F, T] {
  /** Creates a new [[Decoder]] instance from the specified function. */
  @inline def from[D](f: E ⇒ Result[F, D]): Decoder[E, D, F, T] = Decoder.from(f)

  /** Creates a new [[Decoder]] instance from the specified alternatives.
    *
    * When decoding, each of the specified decoders will be attempted. The result will be the first success if found,
    * or the last failure otherwise.
    */
  @inline def oneOf[D](h: Decoder[E, D, F, T], t: Decoder[E, D, F, T]*): Decoder[E, D, F, T] = Decoder.oneOf(h, t:_*)

  /** Creates a new [[Decoder]] instance from the specified alternatives.
    *
    * When decoding, each of the specified decoders will be attempted. The result will be the first success if found,
    * or the last failure otherwise.
    *
    * Note that an exception will be thrown if the specified list is empty.
    */
  @inline def oneOf[D](ds: Seq[Decoder[E, D, F, T]]): Decoder[E, D, F, T] = Decoder.oneOf(ds)
}

object Decoder {
  /** Creates a new [[Decoder]] instance that applies the specified function when decoding. */
  def from[E, D, F, T](f: E ⇒ Result[F, D]): Decoder[E, D, F, T] = new Decoder[E, D, F, T] {
    override def decode(e: E) = f(e)
  }

  implicit def decoderFromExported[E, D, F, T](implicit da: DerivedDecoder[E, D, F, T]): Decoder[E, D, F, T] =
    da.value

  implicit def optionalDecoder[E: Optional, D, F, T](implicit da: Decoder[E, D, F, T]): Decoder[E, Option[D], F, T] =
    Decoder.from { e ⇒
      if(Optional[E].isEmpty(e)) Result.success(None)
      else                       da.decode(e).map(Some.apply)
    }

  /** Provides a [[Decoder]] instance for `Either[A, B]`, provided both `A` and `B` have a [[Decoder]] instance. */
  implicit def eitherDecoder[E, D1, D2, F, T](implicit d1: Decoder[E, D1, F, T], d2: Decoder[E, D2, F, T])
  : Decoder[E, Either[D1, D2], F, T] =
    Decoder.from { s ⇒
      d1.decode(s).map(a ⇒ Left(a): Either[D1, D2]).orElse(d2.decode(s).map(b ⇒ Right(b): Either[D1, D2]))
    }

  def oneOf[E, D, F, T](ds: Seq[Decoder[E, D, F, T]]): Decoder[E, D, F, T] =
    ds.headOption.map(head ⇒ oneOf(head, ds.drop(1):_*)).getOrElse(sys.error("oneOf on an empty parameter list"))

  /** Creates a new decoder using all specified values.
    *
    * The generated decoder will try each of the specified decoders in turn, and return either the first success or,
    * if none is found, the last failure.
    */
  def oneOf[E, D, F, T](head: Decoder[E, D, F, T], tail: Decoder[E, D, F, T]*): Decoder[E, D, F, T] = {
    def decode(input: E, h: Decoder[E, D, F, T], t: Seq[Decoder[E, D, F, T]]): Result[F, D] = {
      val res = h.decode(input)

      if(res.isSuccess) res
      else              t.headOption.map(head ⇒ decode(input, head, t.drop(1))).getOrElse(res)
    }
    Decoder.from(input ⇒ decode(input, head, tail))
  }
}
