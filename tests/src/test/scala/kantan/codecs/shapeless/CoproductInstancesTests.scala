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

package kantan.codecs.shapeless

import kantan.codecs.Encoder
import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline._
import kantan.codecs.shapeless.laws._
import kantan.codecs.shapeless.laws.discipline.arbitrary._
import kantan.codecs.strings._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import shapeless.CNil

class CoproductInstancesTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val cnil: StringDecoder[CNil] = cnilDecoder(_ ⇒ new Exception())

  implicit def leftDecoder[A](implicit da: StringDecoder[A]): StringDecoder[Left[A]] = da.map(Left.apply)
  implicit def rightDecoder[A](implicit da: StringDecoder[A]): StringDecoder[Right[A]] = da.map(Right.apply)
  implicit def leftEncoder[A](implicit ea: StringEncoder[A]): StringEncoder[Left[A]] = ea.contramap(_.a)
  implicit def rightEncoder[A](implicit ea: StringEncoder[A]): StringEncoder[Right[A]] = ea.contramap(_.b)

  // TODO: Arbitrary[Or]

  implicitly[Encoder[String, Int Or Boolean, codecs.type]]
//  implicit val test = arbLegalValueFromEncoder[String, Int Or Boolean, codecs.type]
  implicitly[Arbitrary[LegalValue[String, Int Or Boolean]]]


  checkAll("StringDecoder[Int Or Boolean]", DecoderTests[String, Int Or Boolean, Throwable, codecs.type].decoder[Int, Int])
  //checkAll("StringEncoder[Int Or String]", EncoderTests[String, Int Or String, codecs.type].encoder[Int, Int])
  //checkAll("StringCodec[Int Or String]", CodecTests[String, Int Or String, Throwable, codecs.type].codec[Int, Int])

  //checkAll("TaggedDecoder[Int Or Boolean]", DecoderTests[String, Int Or Boolean, Throwable, tagged.type].decoder[Int, Int])
  //checkAll("TaggedEncoder[Int Or String]", EncoderTests[String, Int Or String, tagged.type].encoder[Int, Int])
}
