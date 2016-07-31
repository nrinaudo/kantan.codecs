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

package kantan.codecs.exported

import kantan.codecs._
import kantan.codecs.export.{DerivedDecoder, DerivedEncoder}
import kantan.codecs.strings.{DecodeError, StringDecoder, StringEncoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class DerivedCodecTests extends FunSuite with GeneratorDrivenPropertyChecks {
  sealed abstract class Maybe[+A] extends Product with Serializable
  case class Just[A](value: A) extends Maybe[A]
  case object None extends Maybe[Nothing]

  val decode: String ⇒ Result[DecodeError, Maybe[Int]] = s ⇒
    if(s.trim.isEmpty) Result.success(None)
    else               Result.nonFatalOr(DecodeError(s"Not a valid Int: '$s'"))(Just(s.toInt))

  val encode: Maybe[Int] ⇒ String = _ match {
    case Just(i) ⇒ i.toString
    case None    ⇒ ""
  }

  test("Derived decoders should have a lower priority than bespoke ones") {
    implicit val bespoke = StringDecoder(decode)
    implicit val derived = DerivedDecoder[String, Maybe[Int], DecodeError, strings.codecs.type](decode)

    assert(implicitly[Decoder[String, Maybe[Int], DecodeError, strings.codecs.type]] == bespoke)
  }

  test("Derived encoders should have a lower priority than bespoke ones") {
    implicit val bespoke = StringEncoder(encode)
    implicit val derived = DerivedEncoder[String, Maybe[Int], strings.codecs.type](encode)

    assert(implicitly[Encoder[String, Maybe[Int], strings.codecs.type]] == bespoke)
  }

  test("Derived decoders should be picked up when no other is available") {
    implicit val derived = DerivedDecoder[String, Maybe[Int], Throwable, strings.codecs.type](decode)

    assert(implicitly[Decoder[String, Maybe[Int], Throwable, strings.codecs.type]] == derived.value)
  }

  test("Derived encoders should be picked up when no other is available") {
    implicit val derived = DerivedEncoder[String, Maybe[Int], strings.codecs.type](encode)

    assert(implicitly[Encoder[String, Maybe[Int], strings.codecs.type]] == derived.value)
  }
}
