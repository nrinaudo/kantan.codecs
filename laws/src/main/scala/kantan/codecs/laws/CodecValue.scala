package kantan.codecs.laws

import java.util.UUID

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}

import scala.util.Try

sealed abstract class CodecValue[E, D] extends Product with Serializable {
  def encoded: E
}

object CodecValue {
  final case class LegalValue[E, D](encoded: E, decoded: D) extends CodecValue[E, D]
  final case class IllegalValue[E, D](encoded: E) extends CodecValue[E, D]


  // - Generic arbitrary / gen -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def genLegal[E, D: Arbitrary](f: D ⇒ E): Gen[LegalValue[E, D]] = arbitrary[D].map(d ⇒ LegalValue(f(d), d))
  def genIllegal[E: Arbitrary, D](f: E ⇒ D): Gen[IllegalValue[E, D]] =
    arbitrary[E].suchThat(e ⇒ Try(f(e)).isFailure).map(IllegalValue.apply)

  def arbLegal[E, D: Arbitrary](f: D ⇒ E): Arbitrary[LegalValue[E, D]] = Arbitrary(genLegal(f))
  def arbIllegal[E: Arbitrary, D](f: E ⇒ D): Arbitrary[IllegalValue[E, D]] = Arbitrary(genIllegal(f))

  implicit def arbValue[E, D](implicit arbL: Arbitrary[LegalValue[E, D]], arbI: Arbitrary[IllegalValue[E, D]]): Arbitrary[CodecValue[E, D]] =
    Arbitrary {
      for {
        legal ← Arbitrary.arbitrary[Boolean]
        value ← if(legal) arbL.arbitrary else arbI.arbitrary
      } yield value
    }


  // - String arbitrary / gen ------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // Encoding to / decoding from strings is such a common patterns that default arbitrary instances are provided for
  // common types.
  implicit val arbLegalStrStr: Arbitrary[LegalValue[String, String]] = CodecValue.arbLegal(identity)

  implicit val arbLegalStrInt: Arbitrary[LegalValue[String, Int]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrInt: Arbitrary[IllegalValue[String, Int]] = CodecValue.arbIllegal(Integer.parseInt)

  implicit val arbLegalStrFloat: Arbitrary[LegalValue[String, Float]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrFloat: Arbitrary[IllegalValue[String, Float]] = CodecValue.arbIllegal(_.toFloat)

  implicit val arbLegalStrDouble: Arbitrary[LegalValue[String, Double]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrDouble: Arbitrary[IllegalValue[String, Double]] = CodecValue.arbIllegal(_.toDouble)

  implicit val arbLegalStrLong: Arbitrary[LegalValue[String, Long]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrLong: Arbitrary[IllegalValue[String, Long]] = CodecValue.arbIllegal(_.toLong)

  implicit val arbLegalStrShort: Arbitrary[LegalValue[String, Short]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrShort: Arbitrary[IllegalValue[String, Short]] = CodecValue.arbIllegal(_.toShort)

  implicit val arbLegalStrByte: Arbitrary[LegalValue[String, Byte]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrByte: Arbitrary[IllegalValue[String, Byte]] = CodecValue.arbIllegal(_.toByte)

  implicit val arbLegalStrBoolean: Arbitrary[LegalValue[String, Boolean]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrBoolean: Arbitrary[IllegalValue[String, Boolean]] = CodecValue.arbIllegal(_.toBoolean)

  implicit val arbLegalStrBigInt: Arbitrary[LegalValue[String, BigInt]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrBigInt: Arbitrary[IllegalValue[String, BigInt]] = CodecValue.arbIllegal(BigInt.apply)

  implicit val arbLegalStrBigDecimal: Arbitrary[LegalValue[String, BigDecimal]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrBigDecimal: Arbitrary[IllegalValue[String, BigDecimal]] = CodecValue.arbIllegal(BigDecimal.apply)

  implicit val arbLegalStrUUID: Arbitrary[LegalValue[String, UUID]] = CodecValue.arbLegal[String, UUID](_.toString)(Arbitrary(Gen.uuid))
  implicit val arbIllegalStrUUID: Arbitrary[IllegalValue[String, UUID]] = CodecValue.arbIllegal(UUID.fromString)

  implicit val arbLegalStrChar: Arbitrary[LegalValue[String, Char]] = CodecValue.arbLegal(_.toString)
  implicit val arbIllegalStrChar: Arbitrary[IllegalValue[String, Char]] = CodecValue.arbIllegal { str =>
    if(str.length == 1) str.charAt(0)
    else                sys.error(s"not a valid char: '$str'")
  }
}
