package kantan.codecs

import scala.util.Try

/** Represents the result of a decode operation
  *
  * This is very similar to [[Either]], with a few more bells and whistles and a more specific type. It's also much
  * more convenient to use in for-comprehensions, as it has proper [[map]] and [[flatMap]] methods.
  *
  * @tparam F failure type.
  * @tparam S success type.
  */
sealed abstract class Result[+F, +S] extends Product with Serializable {
  // - Result status ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def isSuccess: Boolean
  def isFailure: Boolean = !isSuccess


  // - Value retrieval -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def orElse[FF >: F, SS >: S](default: ⇒ Result[FF, SS]): Result[FF, SS]
  def getOrElse[SS >: S](default: ⇒ SS): SS
  def get: S



  // - Standard Scala operations ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def foreach(f: S ⇒ Unit): Unit
  def recover[SS >: S](pf: PartialFunction[F, SS]): Result[F, SS]
  def recoverWith[SS >: S, FF >: F](pf: PartialFunction[F, Result[FF, SS]]): Result[FF, SS]
  def valueOr[SS >: S](f: F ⇒ SS): SS
  def forall(f: S ⇒ Boolean): Boolean
  def exists(f: S ⇒ Boolean): Boolean
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
  def toOption: Option[S]
  def toEither: Either[F, S]
  def toList: List[S]
}

object Result {
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
  }
}