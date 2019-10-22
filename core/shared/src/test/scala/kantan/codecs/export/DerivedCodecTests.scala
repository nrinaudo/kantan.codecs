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

package kantan.codecs
package export

import DerivedCodecTests.{Just, Maybe, None}
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import strings.{DecodeError, StringDecoder, StringEncoder, StringResult}

class DerivedCodecTests extends AnyFunSuite with ScalaCheckPropertyChecks with Matchers {
  val decode: String => StringResult[Maybe[Int]] = s =>
    if(s.trim.isEmpty) Right(None)
    else StringResult(Just(s.toInt))

  val encode: Maybe[Int] => String = _ match {
    case Just(i) => i.toString
    case None    => ""
  }

  test("Derived decoders should have a lower priority than bespoke ones") {
    implicit val bespoke: StringDecoder[Maybe[Int]] = StringDecoder.from(decode)
    implicit val derived: DerivedDecoder[String, Maybe[Int], DecodeError, strings.codecs.type] =
      DerivedDecoder.from[String, Maybe[Int], DecodeError, strings.codecs.type](decode)

    implicitly[Decoder[String, Maybe[Int], DecodeError, strings.codecs.type]] should be(bespoke)
    derived // Allows this to compile - otherwise the compiler detects correctly that derived isn't used and fails.
  }

  test("Derived encoders should have a lower priority than bespoke ones") {
    implicit val bespoke: StringEncoder[Maybe[Int]] = StringEncoder.from(encode)
    implicit val derived: DerivedEncoder[String, Maybe[Int], strings.codecs.type] =
      DerivedEncoder.from[String, Maybe[Int], strings.codecs.type](encode)

    implicitly[Encoder[String, Maybe[Int], strings.codecs.type]] should be(bespoke)
    derived // Allows this to compile - otherwise the compiler detects correctly that derived isn't used and fails.
  }

  test("Derived decoders should be picked up when no other is available") {
    implicit val derived: DerivedDecoder[String, Maybe[Int], Throwable, strings.codecs.type] =
      DerivedDecoder.from[String, Maybe[Int], Throwable, strings.codecs.type](decode)

    implicitly[Decoder[String, Maybe[Int], Throwable, strings.codecs.type]] should be(derived.value)
  }

  test("Derived encoders should be picked up when no other is available") {
    implicit val derived: DerivedEncoder[String, Maybe[Int], strings.codecs.type] =
      DerivedEncoder.from[String, Maybe[Int], strings.codecs.type](encode)

    implicitly[Encoder[String, Maybe[Int], strings.codecs.type]] should be(derived.value)
  }
}

object DerivedCodecTests {
  sealed abstract class Maybe[+A]    extends Product with Serializable
  final case class Just[A](value: A) extends Maybe[A]
  final case object None             extends Maybe[Nothing]
}
