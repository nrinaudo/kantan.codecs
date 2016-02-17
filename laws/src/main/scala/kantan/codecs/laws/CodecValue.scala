package kantan.codecs.laws

import java.util.UUID

import kantan.codecs.laws.discipline.arbitrary
import org.scalacheck.Arbitrary.{arbitrary ⇒ arb}
import arbitrary._

import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

import scala.util.Try

// TODO: investigate what type variance annotations can be usefully applied to CodecValue.
sealed abstract class CodecValue[E, D] extends Product with Serializable {
  def encoded: E
  def mapEncoded[EE](f: E ⇒ EE): CodecValue[EE, D]
  def mapDecoded[DD](f: D ⇒ DD): CodecValue[E, DD]
}

object CodecValue {
  final case class LegalValue[E, D](encoded: E, decoded: D) extends CodecValue[E, D] {
    override def mapDecoded[DD](f: D => DD) = copy(decoded = f(decoded))
    override def mapEncoded[EE](f: E ⇒ EE) = copy(encoded = f(encoded))
  }
  final case class IllegalValue[E, D](encoded: E) extends CodecValue[E, D] {
    override def mapDecoded[DD](f: D => DD) = IllegalValue(encoded)
    override def mapEncoded[EE](f: E => EE) = copy(encoded = f(encoded))
  }



  // - Helpers / bug workarounds ---------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // This is necessary to prevent ScalaCheck from generating BigDecimal values that cannot be serialized because their
  // scale is higher than MAX_INT.
  // Note that this isn't actually an issue with ScalaCheck but with Scala itself, and is(?) fixed in Scala 2.12:
  // https://github.com/scala/scala/pull/4320
  implicit lazy val arbBigDecimal: Arbitrary[BigDecimal] = {
    import java.math.MathContext._
    val mcGen = oneOf(DECIMAL32, DECIMAL64, DECIMAL128)
    val bdGen = for {
      x ← Arbitrary.arbitrary[BigInt]
      mc ← mcGen
      limit ← const(math.max(x.abs.toString.length - mc.getPrecision, 0))
      scale ← Gen.choose(Int.MinValue + limit , Int.MaxValue)
    } yield {
      try {
        BigDecimal(x, scale, mc)
      } catch {
        case ae: java.lang.ArithmeticException ⇒ BigDecimal(x, scale, UNLIMITED) // Handle the case where scale/precision conflict
      }
    }
    Arbitrary(bdGen)
  }

  implicit def arbValue[E, D](implicit arbL: Arbitrary[LegalValue[E, D]], arbI: Arbitrary[IllegalValue[E, D]]): Arbitrary[CodecValue[E, D]] =
    Arbitrary {
      for {
        legal ← Arbitrary.arbitrary[Boolean]
        value ← if(legal) arbL.arbitrary else arbI.arbitrary
      } yield value
    }

  // Encoding to / decoding from strings is such a common patterns that default arbitrary instances are provided for
  // common types.
  implicit val arbLegalStrStr: Arbitrary[LegalValue[String, String]] = arbLegal(identity)

  implicit val arbLegalStrInt: Arbitrary[LegalValue[String, Int]] = arbLegal(_.toString)
  implicit def arbIllegalStrInt: Arbitrary[IllegalValue[String, Int]] = arbIllegal(Integer.parseInt)

  implicit val arbLegalStrFloat: Arbitrary[LegalValue[String, Float]] = arbLegal(_.toString)
  implicit val arbIllegalStrFloat: Arbitrary[IllegalValue[String, Float]] = arbIllegal(_.toFloat)

  implicit val arbLegalStrDouble: Arbitrary[LegalValue[String, Double]] = arbLegal(_.toString)
  implicit val arbIllegalStrDouble: Arbitrary[IllegalValue[String, Double]] = arbIllegal(_.toDouble)

  implicit val arbLegalStrLong: Arbitrary[LegalValue[String, Long]] = arbLegal(_.toString)
  implicit val arbIllegalStrLong: Arbitrary[IllegalValue[String, Long]] = arbIllegal(_.toLong)

  implicit val arbLegalStrShort: Arbitrary[LegalValue[String, Short]] = arbLegal(_.toString)
  implicit val arbIllegalStrShort: Arbitrary[IllegalValue[String, Short]] = arbIllegal(_.toShort)

  implicit val arbLegalStrByte: Arbitrary[LegalValue[String, Byte]] = arbLegal(_.toString)
  implicit val arbIllegalStrByte: Arbitrary[IllegalValue[String, Byte]] = arbIllegal(_.toByte)

  implicit val arbLegalStrBoolean: Arbitrary[LegalValue[String, Boolean]] = arbLegal(_.toString)
  implicit val arbIllegalStrBoolean: Arbitrary[IllegalValue[String, Boolean]] = arbIllegal(_.toBoolean)

  implicit val arbLegalStrBigInt: Arbitrary[LegalValue[String, BigInt]] = arbLegal(_.toString)
  implicit val arbIllegalStrBigInt: Arbitrary[IllegalValue[String, BigInt]] = arbIllegal(BigInt.apply)

  implicit val arbLegalStrBigDecimal: Arbitrary[LegalValue[String, BigDecimal]] = arbLegal(_.toString)
  implicit val arbIllegalStrBigDecimal: Arbitrary[IllegalValue[String, BigDecimal]] = arbIllegal(BigDecimal.apply)

  implicit val arbLegalStrUUID: Arbitrary[LegalValue[String, UUID]] = arbLegal[String, UUID](_.toString)(Arbitrary(Gen.uuid))
  implicit val arbIllegalStrUUID: Arbitrary[IllegalValue[String, UUID]] = arbIllegal(UUID.fromString)

  implicit val arbLegalStrChar: Arbitrary[LegalValue[String, Char]] = arbLegal(_.toString)
  implicit val arbIllegalStrChar: Arbitrary[IllegalValue[String, Char]] = arbIllegal { str =>
    if(str.length == 1) str.charAt(0)
    else                sys.error(s"not a valid char: '$str'")
  }

  implicit def arbLegalStrEither[L, R](implicit al: Arbitrary[LegalValue[String, L]], ar: Arbitrary[LegalValue[String, R]])
  : Arbitrary[LegalValue[String, Either[L, R]]] = Arbitrary {
    arb[Either[LegalValue[String, L], LegalValue[String, R]]].map {
      case Left(l) ⇒ l.mapDecoded(Left.apply)
      case Right(r) ⇒ r.mapDecoded(Right.apply)
    }
  }

  implicit def arbIllegalStrEither[L, R](implicit al: Arbitrary[IllegalValue[String, L]], ar: Arbitrary[IllegalValue[String, R]])
  : Arbitrary[IllegalValue[String, Either[L, R]]] = Arbitrary {
    arb[Either[IllegalValue[String, L], IllegalValue[String, R]]].map {
      case Left(l)  ⇒ IllegalValue(l.encoded)
      case Right(r) ⇒ IllegalValue(r.encoded)
    }
  }

  implicit def arbLegalStrOption[D](implicit dl: Arbitrary[LegalValue[String, D]]): Arbitrary[LegalValue[String, Option[D]]] =
    Arbitrary {
      arb[Option[LegalValue[String, D]]]
        .suchThat(_.map(_.encoded.nonEmpty).getOrElse(true))
        .map(_.map(l ⇒ l.copy(decoded = Option(l.decoded))).getOrElse(LegalValue("", None)))
    }

  implicit def arbIllegalStrOption[D](implicit dl: Arbitrary[IllegalValue[String, D]]): Arbitrary[IllegalValue[String, Option[D]]] =
    Arbitrary {
      arb[IllegalValue[String, D]].suchThat(_.encoded.nonEmpty).map(d ⇒ IllegalValue(d.encoded))
    }
}
