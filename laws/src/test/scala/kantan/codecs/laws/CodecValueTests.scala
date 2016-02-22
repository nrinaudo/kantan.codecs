package kantan.codecs.laws

import java.util.UUID

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalacheck.{Prop, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.util.Try

class CodecValueTests extends FunSuite with GeneratorDrivenPropertyChecks {
  test("mapDecoded should ignore illegal values and modify the encoded part of legal ones.") {
    forAll { (v: StringValue[Int], f: Int ⇒ Float) ⇒
      assert(v.mapDecoded(f) == (v match {
        case LegalValue(s, i) ⇒ LegalValue(s, f(i))
        case IllegalValue(s)  ⇒ IllegalValue(s)
      }))
    }
  }

  test("mapEncoded should modify the encoded part of all values.") {
    forAll { (v: StringValue[Int], f: String ⇒ Long) ⇒
      assert(v.mapEncoded(f) == (v match {
        case LegalValue(s, i) ⇒ LegalValue(f(s), i)
        case IllegalValue(s)  ⇒ IllegalValue(f(s))
      }))
    }
  }

  // Not the most elegant code I wrote, but it does the job.
  def testArbitrary[E, D](E: String, D: String)(f: E ⇒ D)(implicit arbLegal: Arbitrary[LegalValue[E, D]], arbIllegal: Arbitrary[IllegalValue[E, D]]): Unit = {
    test(s"Arbitrary[LegalValue[$E, $D]] should generate legal values") {
      forAll { li: LegalValue[E, D] ⇒ assert(f(li.encoded) == li.decoded )}
    }

    test(s"Arbitrary[IllegalValue[$E, $D]] should generate illegal values") {
      forAll { li: IllegalValue[E, D] ⇒
        Prop.throws(classOf[Exception])(f(li.encoded))
        ()
      }
    }
  }

  testArbitrary[String, Int]("String", "Int")(_.toInt)
  testArbitrary[String, Float]("String", "Float")(_.toFloat)
  testArbitrary[String, Double]("String", "Double")(_.toDouble)
  testArbitrary[String, Long]("String", "Long")(_.toLong)
  testArbitrary[String, Short]("String", "Short")(_.toShort)
  testArbitrary[String, Byte]("String", "Byte")(_.toByte)
  testArbitrary[String, Boolean]("String", "Boolean")(_.toBoolean)
  testArbitrary[String, BigInt]("String", "BigInt")(BigInt.apply)
  testArbitrary[String, BigDecimal]("String", "BigDecimal")(BigDecimal.apply)
  testArbitrary[String, UUID]("String", "UUID")(UUID.fromString)
  testArbitrary[String, Char]("String", "Char")(s ⇒ if(s.length != 1) sys.error("not a valid char") else s.charAt(0))
  testArbitrary[String, Option[Int]]("String", "Option[Int]")(s ⇒ if(s.isEmpty) None else Some(s.toInt))
  testArbitrary[String, Either[Boolean, Int]]("String", "Either[Boolean, Int]")(s ⇒ Try(Left(s.toBoolean)).getOrElse(Right(s.toInt)))
  testArbitrary[Seq[String], Seq[Int]]("String", "Seq[Int]")(_.map(_.toInt))
}
