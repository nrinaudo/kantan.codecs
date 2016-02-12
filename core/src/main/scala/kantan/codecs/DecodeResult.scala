package kantan.codecs

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
}

object DecodeResult {
  // - Instance construction -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def apply[S](s: ⇒ S): DecodeResult[Exception, S] =
    try { Success(s) }
    catch { case e: Exception ⇒ Failure(e) }

  def apply[F, S](s: ⇒ S, f: ⇒ F): DecodeResult[F, S] = DecodeResult(s).leftMap(_ ⇒ f)

  def success[S](s: S): DecodeResult[Nothing, S] = Success(s)
  def failure[F](f: F): DecodeResult[F, Nothing] = Failure(f)



  // - Successes -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  final case class Success[S](value: S) extends DecodeResult[Nothing, S] {
    override def isSuccess= true

    override def orElse[F2 >: Nothing, S2 >: S](default: ⇒ DecodeResult[F2, S2]) = this
    override def getOrElse[S2 >: S](default: => S2) = value
    override def get = value

    override def map[S2](f: S => S2) = copy(value = f(value))
    override def flatMap[F2 >: Nothing, S2](f: S => DecodeResult[F2, S2]) = f(value)

    override def leftMap[F2](f: Nothing => F2) = this
    override def leftFlatMap[F2, S2 >: S](f: Nothing => DecodeResult[F2, S2]) = this

    override def toOption = Some(value)
    override def toEither = Right(value)
  }



  // - Failures --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  final case class Failure[F](value: F) extends DecodeResult[F, Nothing] {
    override def isSuccess = false

    override def orElse[F2 >: F, S2 >: Nothing](default: ⇒ DecodeResult[F2, S2]) = default
    override def getOrElse[S2 >: Nothing](default: => S2) = default
    override def get = sys.error(s"get on a Failure($value)")

    override def map[S](f: Nothing => S) = this
    override def flatMap[F2 >: F, S](f: Nothing => DecodeResult[F2, S]) = this

    override def leftMap[F2](f: F => F2) = copy(value = f(value))
    override def leftFlatMap[F2, S2 >: Nothing](f: F => DecodeResult[F2, S2]) = f(value)

    override def toOption = None
    override def toEither = Left(value)
  }
}