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

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.util.Try

/** Represents the result of a decode operation
  *
  * This is very similar to `Either`, with a few more bells and whistles and a more specific type. It's also much
  * more convenient to use in for-comprehensions, as it has proper [[map]] and [[flatMap]] methods.
  *
  * @define success [[kantan.codecs.Result.Success success]]
  * @define failure [[kantan.codecs.Result.Failure failure]]
  * @tparam F failure type: how to describe errors.
  * @tparam S success type: result of a successful operation.
  */
sealed abstract class Result[+F, +S] extends Product with Serializable {
  // - Result status ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Returns `true` if the result is a success. */
  def isSuccess: Boolean
  /** Returns `true` if the result is a failure. */
  def isFailure: Boolean = !isSuccess


  // - Value retrieval -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Returns the specified default value if a $failure, does nothing otherwise. */
  def orElse[FF >: F, SS >: S](default: ⇒ Result[FF, SS]): Result[FF, SS]

  /** Returns the underlying value if a $success, the result of applying the specified function to the failure value
    * otherwise.
    *
    * This is typically useful when one needs to provide a default value that depends on how an operation failed.
    *
    * @see [[recover]], [[recoverWith]]
    */
  def valueOr[SS >: S](f: F ⇒ SS): SS

  /** Returns the underlying value if a $success, the specified default value otherwise.
    *
    * This is useful when one needs to provide a default value in case of a $failure.
    */
  def getOrElse[SS >: S](default: ⇒ SS): SS

  /** Returns the underlying value if a $success, throws an exception otherwise.
    *
    * Other value retrieval methods, such as [[getOrElse]] or [[valueOr]], should almost always be preferred.
    */
  def get: S



  // - Standard Scala operations ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Applies the specified procedure on the underlying value if a $success, does nothing otherwise.
    *
    * This is useful when one wants to have side-effects that depend on the success' value - print it, for example.
    */
  def foreach(f: S ⇒ Unit): Unit

  /** Turns any $failure whose value is defined for specified partial function into a $success with the corresponding
    * value.
    *
    * This is useful when some error conditions are actually valid answers - one could imagine, for instance, that
    * failure to parse a boolean could be turned into a successful parsing of `false`.
    */
  def recover[SS >: S](pf: PartialFunction[F, SS]): Result[F, SS]

  /** Turns any $failure whose value is defined for specified partial function into the corresponding value.
    *
    * This is useful when some error conditions have fallback solutions - one could imagine, for instance, that failure
    * to parse a file because it does not exist can be retried on a secondary possible location for the file.
    */
  def recoverWith[SS >: S, FF >: F](pf: PartialFunction[F, Result[FF, SS]]): Result[FF, SS]

  /** Returns the result of applying the specified predicate to the underlying value if a $success, `false` otherwise.
    */
  def forall(f: S ⇒ Boolean): Boolean

  /** Returns the result of applying the specified predicate to the underlying value if a $success, `false` otherwise.
    */
  def exists(f: S ⇒ Boolean): Boolean

  /** Turns any $success whose underlying value doesn't validate the specified predicate into a $failure.
    *
    * This is useful when not all successes are interesting. One might receive, for example, a $success containing an
    * int, but need to further reduce its domain to positive ints and turn any other into a $failure.
    */
  def ensure[FF >: F](fail: ⇒ FF)(f: S ⇒ Boolean): Result[FF, S]



  // - Foldable operations ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def fold[A](ff: F => A, fs: S => A): A
  def foldLeft[A](acc: A)(f: (A, S) => A): A = fold(_ => acc, f(acc, _))
  def foldRight[A](acc: A)(f: (S, A) ⇒ A): A =
    fold(_ ⇒ acc, s ⇒ f(s, acc))


  // - Monad operations ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def map[SS](f: S ⇒ SS): Result[F, SS]
  def flatMap[FF >: F, SS](f: S ⇒ Result[FF, SS]): Result[FF, SS]


  // - Comonad operations ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def leftMap[FF](f: F ⇒ FF): Result[FF, S]
  def leftFlatMap[FF, SS >: S](f: F ⇒ Result[FF, SS]): Result[FF, SS]


  // - BiMonad operations ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def bimap[FF, SS](f: F ⇒ FF, s: S ⇒ SS): Result[FF, SS] = leftMap(f).map(s)
  def biFlatMap[FF >: F, SS >: S](f: FF ⇒ Result[FF, SS], s: SS ⇒ Result[FF, SS]): Result[FF, SS] =
    leftFlatMap(f).flatMap(s)



  // - Conversion to standard types ------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Turns a $success into a `Some` and a $failure into a `None`. */
  def toOption: Option[S]
  /** Turns a $success into a `Right` and a $failure into a `Left`. */
  def toEither: Either[F, S]
  /** Turns a $success into a singleton list and a $failure into an empty list. */
  def toList: List[S]
}

object Result {
  // - Helper methods --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Turns a collection of results into a result of a collection. */
  def sequence[F, S, M[X] <: TraversableOnce[X]](rs: M[Result[F, S]])
                                                (implicit cbf: CanBuildFrom[M[Result[F, S]], S, M[S]])
  : Result[F, M[S]] =
    rs.foldLeft(Result.success[F, mutable.Builder[S, M[S]]](cbf(rs))) { (builder, res) ⇒
      for(b ← builder; r ← res) yield b += r
    }.map(_.result())



  // - Instance construction -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def nonFatal[S](s: ⇒ S): Result[Throwable, S] =
    try {success(s)}
    catch { case scala.util.control.NonFatal(t) ⇒ failure(t) }

  def nonFatalOr[F, S](f: ⇒ F)(s: ⇒ S): Result[F, S] = nonFatal(s).leftMap(_ ⇒ f)

  def fromTry[S](t: Try[S]): Result[Throwable, S] = t match {
    case scala.util.Success(s) ⇒ success(s)
    case scala.util.Failure(f) ⇒ failure(f)
  }

  def fromEither[F, S](e: Either[F, S]): Result[F, S] = e.fold(failure, success)
  def fromOption[F, S](o: Option[S], f: ⇒ F): Result[F, S] = o.fold(failure[F, S](f))(success)

  def success[F, S](s: S): Result[F, S] = Success(s)
  def failure[F, S](f: F): Result[F, S] = Failure(f)



  // - Successes -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  final case class Success[S](value: S) extends Result[Nothing, S] {
    override def isSuccess= true

    override def orElse[FF >: Nothing, SS >: S](default: ⇒ Result[FF, SS]) = this
    override def getOrElse[SS >: S](default: => SS) = value
    override def get = value

    override def fold[C](ff: Nothing => C, fs: S => C): C = fs(value)

    override def foreach(f: S => Unit) = f(value)
    override def recover[SS >: S](pf: PartialFunction[Nothing, SS]) = this
    override def recoverWith[SS >: S, FF >: Nothing](pf: PartialFunction[Nothing, Result[FF, SS]]) = this
    override def valueOr[SS >: S](f: Nothing => SS) = value
    override def forall(f: S => Boolean) = f(value)
    override def ensure[FF >: Nothing](fail: => FF)(f: S => Boolean) = if(f(value)) this else failure(fail)
    override def exists(f: S => Boolean) = f(value)

    override def map[SS](f: S => SS) = copy(value = f(value))
    override def flatMap[FF >: Nothing, SS](f: S => Result[FF, SS]) = f(value)

    override def leftMap[FF](f: Nothing => FF) = this
    override def leftFlatMap[FF, SS >: S](f: Nothing => Result[FF, SS]) = this

    override def toOption = Some(value)
    override def toEither = Right(value)
    override def toList = List(value)
  }



  // - Failures --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  final case class Failure[F](value: F) extends Result[F, Nothing] {
    override def isSuccess = false

    override def orElse[FF >: F, SS >: Nothing](default: ⇒ Result[FF, SS]) = default
    override def getOrElse[SS >: Nothing](default: => SS) = default
    override def get = throw new NoSuchElementException(s"get on a Failure($value)")

    override def fold[C](ff: F => C, fs: Nothing => C): C = ff(value)

    override def foreach(f: Nothing => Unit): Unit = ()
    override def recover[SS >: Nothing](pf: PartialFunction[F, SS]) =
      if(pf.isDefinedAt(value)) success(pf(value))
      else                      this
    override def recoverWith[SS >: Nothing, FF >: F](pf: PartialFunction[F, Result[FF, SS]]) =
      if(pf.isDefinedAt(value)) pf(value)
      else                      this
    override def valueOr[SS >: Nothing](f: F => SS) = f(value)
    override def forall(f: Nothing => Boolean) = true
    override def ensure[FF >: F](fail: => FF)(f: Nothing => Boolean) = this
    override def exists(f: Nothing => Boolean) = false

    override def map[S](f: Nothing => S) = this
    override def flatMap[FF >: F, S](f: Nothing => Result[FF, S]) = this

    override def leftMap[FF](f: F => FF) = copy(value = f(value))
    override def leftFlatMap[FF, SS >: Nothing](f: F => Result[FF, SS]) = f(value)

    override def toOption = None
    override def toEither = Left(value)
    override def toList = Nil

    // Specific implementation for Throwable. Not the cleanest hack ever, but we really want two failures of the same
    // exception type to be the same.
    override def equals(obj: Any): Boolean = obj match {
      case Failure(f) ⇒
        if(f.isInstanceOf[Throwable]) f.getClass == value.getClass
        else                          f == value
      case _ ⇒ false
    }

    override def hashCode(): Int = value.hashCode()
  }
}
