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

package kantan.codecs.laws

import java.util.UUID
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.laws.CodecValue._
import kantan.codecs.strings.{codecs ⇒ scodecs}
import org.scalacheck.{Arbitrary, Prop}
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
  def testArbitrary[E, D, T](E: String, D: String)(f: E ⇒ D)
                            (implicit arbL: Arbitrary[LegalValue[E, D, T]], arbI: Arbitrary[IllegalValue[E, D, T]])
  : Unit = {
    test(s"Arbitrary[LegalValue[$E, $D]] should generate legal values") {
      forAll { li: LegalValue[E, D, T] ⇒ assert(f(li.encoded) == li.decoded )}
    }

    test(s"Arbitrary[IllegalValue[$E, $D]] should generate illegal values") {
      forAll { li: IllegalValue[E, D, T] ⇒
        Prop.throws(classOf[Exception])(f(li.encoded))
        ()
      }
    }
  }

  testArbitrary[String, Int, scodecs.type]("String", "Int")(_.toInt)
  testArbitrary[String, Float, scodecs.type]("String", "Float")(_.toFloat)
  testArbitrary[String, Double, scodecs.type]("String", "Double")(_.toDouble)
  testArbitrary[String, Long, scodecs.type]("String", "Long")(_.toLong)
  testArbitrary[String, Short, scodecs.type]("String", "Short")(_.toShort)
  testArbitrary[String, Byte, scodecs.type]("String", "Byte")(_.toByte)
  testArbitrary[String, Boolean, scodecs.type]("String", "Boolean")(_.toBoolean)
  testArbitrary[String, BigInt, scodecs.type]("String", "BigInt")(BigInt.apply)
  testArbitrary[String, BigDecimal, scodecs.type]("String", "BigDecimal")(BigDecimal.apply)
  testArbitrary[String, UUID, scodecs.type]("String", "UUID")(UUID.fromString)
  testArbitrary[String, Char, scodecs.type]("String", "Char") { s ⇒
    if(s.length != 1) sys.error("not a valid char")
    else              s.charAt(0)
  }
  testArbitrary[String, Option[Int], scodecs.type]("String", "Option[Int]")(s ⇒ if(s.isEmpty) None else Some(s.toInt))
  testArbitrary[String, Either[Boolean, Int], scodecs.type]("String", "Either[Boolean, Int]") { s ⇒
    Try(Left(s.toBoolean)).getOrElse(Right(s.toInt))
  }
  // TODO: re-enable?
  //testArbitrary[Seq[String], Seq[Int], scodecs.type]("String", "Seq[Int]")(_.map(_.toInt))
}
