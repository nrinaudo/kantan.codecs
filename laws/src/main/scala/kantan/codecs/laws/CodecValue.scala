package kantan.codecs.laws

import java.util.UUID

import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

import scala.collection.generic.CanBuildFrom

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
    Arbitrary(Gen.oneOf(arbL.arbitrary, arbI.arbitrary))


  // - Derived instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbLegalSeq[E, D, C1[_], C2[_]](implicit arb: Arbitrary[LegalValue[E, D]],
                                               cbfE: CanBuildFrom[Nothing, E, C1[E]],
                                               cbfD: CanBuildFrom[Nothing, D, C2[D]]
                                              ): Arbitrary[LegalValue[C1[E], C2[D]]] = Arbitrary {
    Gen.sized { size ⇒
      val be = cbfE()
      val bd = cbfD()

      for(values ← Gen.listOfN(size, arb.arbitrary)) yield {
        values.foreach { case LegalValue(e, d) ⇒
          be += e
          bd += d
        }
        LegalValue(be.result(), bd.result())
      }
    }
  }

  implicit def arbIllegalSeq[E, D, C1[_], C2[_]](implicit arb: Arbitrary[CodecValue.IllegalValue[E, D]],
                                                 cbfE: CanBuildFrom[Nothing, E, C1[E]]
                                                ): Arbitrary[CodecValue.IllegalValue[C1[E], C2[D]]] = Arbitrary {
    Gen.sized { size ⇒ choose(1, size).flatMap { size =>
      val be = cbfE()
      for(values ← Gen.listOfN(size, arb.arbitrary)) yield {
        values.foreach { case CodecValue.IllegalValue(e) ⇒ be += e }
        CodecValue.IllegalValue(be.result())
      }
    }}
  }

  implicit def arbLegalEither[E, DL, DR](implicit al: Arbitrary[LegalValue[E, DL]], ar: Arbitrary[LegalValue[E, DR]])
  : Arbitrary[LegalValue[E, Either[DL, DR]]] = Arbitrary {
    arb[Either[LegalValue[E, DL], LegalValue[E, DR]]].map {
      case Left(l) ⇒ l.mapDecoded(Left.apply)
      case Right(r) ⇒ r.mapDecoded(Right.apply)
    }
  }

  implicit def arbIllegalEither[E, DL, DR](implicit al: Arbitrary[IllegalValue[E, DL]], ar: Arbitrary[IllegalValue[E, DR]])
  : Arbitrary[IllegalValue[E, Either[DL, DR]]] = Arbitrary {
    arb[Either[IllegalValue[E, DL], IllegalValue[E, DR]]].map {
      case Left(l)  ⇒ IllegalValue(l.encoded)
      case Right(r) ⇒ IllegalValue(r.encoded)
    }
  }



  // - String instances ------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // Encoding to / decoding from strings is such a common patterns that default arbitrary instances are provided for
  // common types.
  implicit val arbLegalStrStr: Arbitrary[LegalString[String]] = Arbitrary(genLegal(identity))

  implicit val arbLegalStrInt: Arbitrary[LegalString[Int]] = Arbitrary(genLegal(_.toString))
  implicit def arbIllegalStrInt: Arbitrary[IllegalString[Int]] = Arbitrary(genIllegalFromException(_.trim.toInt))

  implicit val arbLegalStrFloat: Arbitrary[LegalString[Float]] = Arbitrary(genLegal(_.toString))
  implicit val arbIllegalStrFloat: Arbitrary[IllegalString[Float]] = Arbitrary(genIllegalFromException(_.trim.toFloat))

  implicit val arbLegalStrDouble: Arbitrary[LegalString[Double]] = Arbitrary(genLegal(_.toString))
  implicit val arbIllegalStrDouble: Arbitrary[IllegalString[Double]] = Arbitrary(genIllegalFromException(_.trim.toDouble))

  implicit val arbLegalStrLong: Arbitrary[LegalString[Long]] = Arbitrary(genLegal(_.toString))
  implicit val arbIllegalStrLong: Arbitrary[IllegalString[Long]] = Arbitrary(genIllegalFromException(_.trim.toLong))

  implicit val arbLegalStrShort: Arbitrary[LegalString[Short]] = Arbitrary(genLegal(_.toString))
  implicit val arbIllegalStrShort: Arbitrary[IllegalString[Short]] = Arbitrary(genIllegalFromException(_.trim.toShort))

  implicit val arbLegalStrByte: Arbitrary[LegalString[Byte]] = Arbitrary(genLegal(_.toString))
  implicit val arbIllegalStrByte: Arbitrary[IllegalString[Byte]] = Arbitrary(genIllegalFromException(_.trim.toByte))

  implicit val arbLegalStrBoolean: Arbitrary[LegalString[Boolean]] = Arbitrary(genLegal(_.toString))
  implicit val arbIllegalStrBoolean: Arbitrary[IllegalString[Boolean]] = Arbitrary(genIllegalFromException(_.trim.toBoolean))

  implicit val arbLegalStrBigInt: Arbitrary[LegalString[BigInt]] = Arbitrary(genLegal(_.toString))
  implicit val arbIllegalStrBigInt: Arbitrary[IllegalString[BigInt]] = Arbitrary(genIllegalFromException(s ⇒ BigInt(s.trim)))

  implicit val arbLegalStrBigDecimal: Arbitrary[LegalString[BigDecimal]] = Arbitrary(genLegal(_.toString))
  implicit val arbIllegalStrBigDecimal: Arbitrary[IllegalString[BigDecimal]] = Arbitrary(genIllegalFromException(s ⇒ BigDecimal(s.trim)))

  implicit val arbLegalStrUUID: Arbitrary[LegalString[UUID]] = Arbitrary(genLegal[String, UUID](_.toString)(Arbitrary(Gen.uuid)))
  implicit val arbIllegalStrUUID: Arbitrary[IllegalString[UUID]] = Arbitrary(genIllegalFromException(s ⇒ UUID.fromString(s.trim)))

  implicit val arbLegalStrChar: Arbitrary[LegalString[Char]] = Arbitrary(genLegal(_.toString))
  implicit val arbIllegalStrChar: Arbitrary[IllegalString[Char]] = Arbitrary(genIllegalFromException { str =>
    val s = str.trim
    if(s.length == 1) s.charAt(0)
    else              sys.error(s"not a valid char: '$s'")
  })

  implicit def arbLegalStrOption[D](implicit dl: Arbitrary[LegalString[D]]): Arbitrary[LegalString[Option[D]]] =
    Arbitrary(genLegalOption[String, D](_.isEmpty)(""))

  implicit def arbIllegalStrOption[D](implicit dl: Arbitrary[IllegalString[D]]): Arbitrary[IllegalString[Option[D]]] =
    Arbitrary(genIllegalOption[String, D](_.isEmpty))
}
