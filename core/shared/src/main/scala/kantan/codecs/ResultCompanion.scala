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

import scala.util.Failure
import scala.util.Success
import scala.util.Try

/** Provides trait that result companion object can extend.
  *
  * The idea is that libraries that rely on kantan.codecs are likely to provide type-constrained versions of result,
  * such as `DecodeResult` in kantan.csv. Users are likely to expect goodies such as `fromTry` or `sequence` on
  * `DecodeResult`'s companion object, which can be achieved by extending [[kantan.codecs.ResultCompanion.WithDefault]].
  */
object ResultCompanion {

  /** Evaluates the specified expression, catching non-fatal errors and sticking them in a `Left`. */
  def nonFatal[E, S](f: Throwable => E)(s: => S): Either[E, S] =
    Try(s).toEither.left.map(f)

  /** Provides companion object methods for result types that do not have a sane default error type.
    *
    * If your specialised result type has a sane default (such as `TypeError` for `DecodeResult` in kantan.csv), use
    * [[WithDefault]] instead.
    */
  trait Simple[F] extends VersionSpecificResultCompanion.Simple[F] {

    /** Turns the specified value into a success. */
    @inline def success[S](s: S): Either[F, S] =
      Right(s)

    /** Turns the specified value into a failure. */
    @inline def failure(f: F): Either[F, Nothing] =
      Left(f)

  }

  /** Provides companion object methods for result types that have a sane default error type.
    *
    * This default error type is materialised by [[fromThrowable]].
    */
  trait WithDefault[F] extends Simple[F] {

    /** Turns an exception into an error. */
    protected def fromThrowable(t: Throwable): F

    /** Attempts to evaluate the specified expression. */
    @inline def apply[S](s: => S): Either[F, S] =
      nonFatal(fromThrowable _)(s)

    /** Turns the specified `Try` into a result. */
    @inline def fromTry[S](t: Try[S]): Either[F, S] =
      t match {
        case Success(s) => Right(s)
        case Failure(e) => Left(fromThrowable(e))
      }

  }

  /** Similar to [[WithDefault]], but uses [[error.IsError IsError]] to deal with error cases. */
  abstract class WithError[F: IsError] extends WithDefault[F] {

    override def fromThrowable(t: Throwable): F =
      IsError[F].fromThrowable(t)

  }

}
