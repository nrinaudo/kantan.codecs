package kantan.codecs

import scala.util.Try

/** Represents the result of a decode operation
  *
  * @tparam F failure type.
  * @tparam S success type.
  */
sealed abstract class DecodeResult[+F, +S] extends Product with Serializable {
  // - Result status ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def isSuccess: Boolean
  def isFailure: Boolean = !isSuccess


  // - Value retrieval -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def orElse[F2 >: F, S2 >: S](default: ⇒ DecodeResult[F2, S2]): DecodeResult[F2, S2]
  def getOrElse[S2 >: S](default: ⇒ S2): S2
  def get: S



  // - Standard Scala operations ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def foreach(f: S ⇒ Unit): Unit
  def recover[SS >: S](pf: PartialFunction[F, SS]): DecodeResult[F, SS]
  def recoverWith[SS >: S, FF >: F](pf: PartialFunction[F, DecodeResult[FF, SS]]): DecodeResult[FF, SS]
  def valueOr[SS >: S](f: F ⇒ SS): SS
  def forall(f: S ⇒ Boolean): Boolean
  def exists(f: S ⇒ Boolean): Boolean
  def ensure[FF >: F](fail: ⇒ FF)(f: S ⇒ Boolean): DecodeResult[FF, S]



  // - Foldable operations ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def fold[A](ff: F => A, fs: S => A): A
  def foldLeft[A](acc: A)(f: (A, S) => A): A = fold(_ => acc, f(acc, _))
  def foldRight[A](acc: A)(f: (S, A) ⇒ A): A =
    fold(_ ⇒ acc, s ⇒ f(s, acc))


  // - Monad operations ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def map[S2](f: S ⇒ S2): DecodeResult[F, S2]
  def flatMap[F2 >: F, S2](f: S ⇒ DecodeResult[F2, S2]): DecodeResult[F2, S2]


  // - Comonad operations ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def leftMap[F2](f: F ⇒ F2): DecodeResult[F2, S]
  def leftFlatMap[F2, S2 >: S](f: F ⇒ DecodeResult[F2, S2]): DecodeResult[F2, S2]


  // - BiMonad operations ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def bimap[F2, S2](f: F ⇒ F2, s: S ⇒ S2): DecodeResult[F2, S2] = leftMap(f).map(s)
  def biFlatMap[F2 >: F, S2 >: S](f: F2 ⇒ DecodeResult[F2, S2], s: S2 ⇒ DecodeResult[F2, S2]): DecodeResult[F2, S2] =
    leftFlatMap(f).flatMap(s)



  // - Conversion to standard types ------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def toOption: Option[S]
  def toEither: Either[F, S]
  def toList: List[S]
}

object DecodeResult {
  // - Instance construction -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def nonFatal[S](s: ⇒ S): DecodeResult[Throwable, S] =
    try {success(s)}
    catch { case scala.util.control.NonFatal(t) ⇒ failure(t) }

  def nonFatalOr[F, S](f: ⇒ F)(s: ⇒ S): DecodeResult[F, S] = nonFatal(s).leftMap(_ ⇒ f)

  def fromTry[S](t: Try[S]): DecodeResult[Throwable, S] = t match {
    case scala.util.Success(s) ⇒ success(s)
    case scala.util.Failure(f) ⇒ failure(f)
  }

  def fromEither[F, S](e: Either[F, S]): DecodeResult[F, S] = e.fold(failure, success)
  def fromOption[F, S](o: Option[S], f: ⇒ F): DecodeResult[F, S] = o.fold(failure[F, S](f))(success)

  def success[F, S](s: S): DecodeResult[F, S] = Success(s)
  def failure[F, S](f: F): DecodeResult[F, S] = Failure(f)



  // - Successes -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  final case class Success[S](value: S) extends DecodeResult[Nothing, S] {
    override def isSuccess= true

    override def orElse[F2 >: Nothing, S2 >: S](default: ⇒ DecodeResult[F2, S2]) = this
    override def getOrElse[S2 >: S](default: => S2) = value
    override def get = value

    override def fold[C](ff: Nothing => C, fs: S => C): C = fs(value)

    override def foreach(f: S => Unit) = f(value)
    override def recover[SS >: S](pf: PartialFunction[Nothing, SS]) = this
    override def recoverWith[SS >: S, FF >: Nothing](pf: PartialFunction[Nothing, DecodeResult[FF, SS]]) = this
    override def valueOr[SS >: S](f: Nothing => SS) = value
    override def forall(f: S => Boolean) = f(value)
    override def ensure[FF >: Nothing](fail: => FF)(f: S => Boolean) = if(f(value)) this else failure(fail)
    override def exists(f: S => Boolean) = f(value)

    override def map[S2](f: S => S2) = copy(value = f(value))
    override def flatMap[F2 >: Nothing, S2](f: S => DecodeResult[F2, S2]) = f(value)

    override def leftMap[F2](f: Nothing => F2) = this
    override def leftFlatMap[F2, S2 >: S](f: Nothing => DecodeResult[F2, S2]) = this

    override def toOption = Some(value)
    override def toEither = Right(value)
    override def toList = List(value)
  }



  // - Failures --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  final case class Failure[F](value: F) extends DecodeResult[F, Nothing] {
    override def isSuccess = false

    override def orElse[F2 >: F, S2 >: Nothing](default: ⇒ DecodeResult[F2, S2]) = default
    override def getOrElse[S2 >: Nothing](default: => S2) = default
    override def get = sys.error(s"get on a Failure($value)")

    override def fold[C](ff: F => C, fs: Nothing => C): C = ff(value)

    override def foreach(f: Nothing => Unit): Unit = ()
    override def recover[SS >: Nothing](pf: PartialFunction[F, SS]) =
      if(pf.isDefinedAt(value)) success(pf(value))
      else                      this
    override def recoverWith[SS >: Nothing, FF >: F](pf: PartialFunction[F, DecodeResult[FF, SS]]) =
      if(pf.isDefinedAt(value)) pf(value)
      else                      this
    override def valueOr[SS >: Nothing](f: F => SS) = f(value)
    override def forall(f: Nothing => Boolean) = true
    override def ensure[FF >: F](fail: => FF)(f: Nothing => Boolean) = this
    override def exists(f: Nothing => Boolean) = false

    override def map[S](f: Nothing => S) = this
    override def flatMap[F2 >: F, S](f: Nothing => DecodeResult[F2, S]) = this

    override def leftMap[F2](f: F => F2) = copy(value = f(value))
    override def leftFlatMap[F2, S2 >: Nothing](f: F => DecodeResult[F2, S2]) = f(value)

    override def toOption = None
    override def toEither = Left(value)
    override def toList = Nil
  }
}