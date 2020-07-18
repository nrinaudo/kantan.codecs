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

import discipline.arbitrary._
import imp.imp
import java.util.UUID
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.strings.codecs
import org.scalacheck.{Arbitrary, Prop}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import scala.reflect.ClassTag
import scala.util.Try

class CodecValueTests extends AnyFunSuite with ScalaCheckPropertyChecks with Matchers {
  test("mapDecoded should ignore illegal values and modify the encoded part of legal ones.") {
    forAll { (v: StringValue[Int], f: Int => Float) =>
      v match {
        case LegalValue(s, i) => v.mapDecoded(f) should be(LegalValue(s, f(i)))
        case IllegalValue(s)  => v.mapDecoded(f) should be(IllegalValue(s))
      }
    }
  }

  test("mapEncoded should modify the encoded part of all values.") {
    forAll { (v: StringValue[Int], f: String => Long) =>
      v match {
        case LegalValue(s, i) => v.mapEncoded(f) should be(LegalValue(f(s), i))
        case IllegalValue(s)  => v.mapEncoded(f) should be(IllegalValue(f(s)))
      }
    }
  }
  // Not the most elegant code I wrote, but it does the job.
  def testArbitrary[E: ClassTag, D: ClassTag](
    f: E => D
  )(implicit arbL: Arbitrary[LegalValue[E, D, codecs.type]], arbI: Arbitrary[IllegalValue[E, D, codecs.type]]): Unit = {

    val e = imp[ClassTag[E]].runtimeClass.getSimpleName
    val d = imp[ClassTag[D]].runtimeClass.getSimpleName

    test(s"Arbitrary[LegalValue[$e, $d]] should generate legal values") {
      forAll { li: LegalValue[E, D, codecs.type] =>
        f(li.encoded) should be(li.decoded)
      }
    }

    test(s"Arbitrary[IllegalValue[$e, $d]] should generate illegal values") {
      forAll { li: IllegalValue[E, D, codecs.type] =>
        Prop.throws(classOf[Exception])(f(li.encoded))
        ()
      }
    }
  }

  testArbitrary[String, Int](_.toInt)
  testArbitrary[String, Float](_.toFloat)
  testArbitrary[String, Double](_.toDouble)
  testArbitrary[String, Long](_.toLong)
  testArbitrary[String, Short](_.toShort)
  testArbitrary[String, Byte](_.toByte)
  testArbitrary[String, Boolean](_.toBoolean)
  testArbitrary[String, BigInt](BigInt.apply)
  testArbitrary[String, BigDecimal](BigDecimal.apply)
  testArbitrary[String, UUID](UUID.fromString)
  testArbitrary[String, Char] { s =>
    if(s.length != 1) sys.error("not a valid char")
    else s.charAt(0)
  }
  testArbitrary[String, Option[Int]](s => if(s.isEmpty) None else Some(s.toInt))
  testArbitrary[String, Either[Boolean, Int]] { s =>
    Try(Left(s.toBoolean): Either[Boolean, Int]).getOrElse(Right(s.toInt): Either[Boolean, Int])
  }
  // TODO: re-enable?
  //testArbitrary[Seq[String], Seq[Int]](_.map(_.toInt))
}
