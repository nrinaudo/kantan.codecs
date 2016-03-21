package kantan.codecs.laws.discipline

import arbitrary._
import java.util.UUID
import kantan.codecs.laws._
import org.scalacheck.{Gen, Arbitrary}
import util.Try

/** Useful trait for generating arbitrary [[CodecValue codec values]]. */
trait GenCodecValue[E, D] {
  def arbE: Arbitrary[E]
  def arbD: Arbitrary[D]

  def isIllegal(e: E): Boolean
  def encode(d: D): E

  val arbLegal: Arbitrary[CodecValue.LegalValue[E, D]] =
    Arbitrary(arbD.arbitrary.map(d ⇒ CodecValue.LegalValue(encode(d), d)))

  val arbIllegal: Arbitrary[CodecValue.IllegalValue[E, D]] =
    Arbitrary(arbE.arbitrary.suchThat(isIllegal).map(CodecValue.IllegalValue.apply))
}

object GenCodecValue {
  def apply[E, D](g: D ⇒ E)(f: E ⇒ Boolean)(implicit ae: Arbitrary[E], ad: Arbitrary[D]): GenCodecValue[E, D] = new GenCodecValue[E, D] {
    override val arbE = ae
    override val arbD = ad

    override def isIllegal(e: E) = f(e)
    override def encode(d: D): E = g(d)
  }

  def nonFatal[E: Arbitrary, D: Arbitrary](g: D ⇒ E)(f: E ⇒ D): GenCodecValue[E, D] =
    apply[E, D](g)(e ⇒ Try(f(e)).isFailure)


  implicit def either[E: Arbitrary, DL: Arbitrary, DR: Arbitrary](implicit cl: GenCodecValue[E, DL], cr: GenCodecValue[E, DR]): GenCodecValue[E, Either[DL, DR]] =
    GenCodecValue[E, Either[DL, DR]] {
      case Left(dl) ⇒ cl.encode(dl)
      case Right(dr) ⇒ cr.encode(dr)
    }(e ⇒ cl.isIllegal(e) && cr.isIllegal(e))

  def option[E: Arbitrary, D: Arbitrary](empty: E)(implicit cd: GenCodecValue[E, D]): GenCodecValue[E, Option[D]] =
    GenCodecValue[E, Option[D]](od ⇒ od.map(cd.encode).getOrElse(empty))(e ⇒ e != empty && cd.isIllegal(e))


  // - Default string instances ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  private def strGen[D: Arbitrary](f: String ⇒ D): GenCodecValue[String, D] = nonFatal[String, D](_.toString)(f)

  implicit def strOpt[A: Arbitrary](implicit ca: GenCodecValue[String, A]): GenCodecValue[String, Option[A]] =
    option[String, A]("")

  implicit val strInt: GenCodecValue[String, Int] = strGen(_.trim.toInt)
  implicit val strFloat: GenCodecValue[String, Float] = strGen(_.trim.toFloat)
  implicit val strDouble: GenCodecValue[String, Double] = strGen(_.trim.toDouble)
  implicit val strLong: GenCodecValue[String, Long] = strGen(_.trim.toLong)
  implicit val strShort: GenCodecValue[String, Short] = strGen(_.trim.toShort)
  implicit val strByte: GenCodecValue[String, Byte] = strGen(_.trim.toByte)
  implicit val strBoolean: GenCodecValue[String, Boolean] = strGen(_.trim.toBoolean)
  implicit val strBigInt: GenCodecValue[String, BigInt] = strGen(s ⇒ BigInt(s.trim))
  implicit val strBigDecimal: GenCodecValue[String, BigDecimal] = strGen(s ⇒ BigDecimal(s.trim))
  implicit val strUUID: GenCodecValue[String, UUID] = strGen(s ⇒ UUID.fromString(s.trim))(Arbitrary(Gen.uuid))
  implicit val strChar: GenCodecValue[String, Char] = strGen { str =>
    val s = if(str.length > 1) str.trim else str
    if(s.length == 1) s.charAt(0)
    else              sys.error(s"not a valid char: '$s'")
  }
}
