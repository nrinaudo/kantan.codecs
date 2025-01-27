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

package kantan.codecs.libra

import kantan.codecs.laws.discipline.DisciplineSuite
import kantan.codecs.laws.discipline.SerializableTests
import kantan.codecs.laws.discipline.StringCodecTests
import kantan.codecs.laws.discipline.StringDecoderTests
import kantan.codecs.laws.discipline.StringEncoderTests
import kantan.codecs.libra.laws.discipline.arbitrary._
import kantan.codecs.strings.StringDecoder
import kantan.codecs.strings.StringEncoder
import libra.Quantity
import shapeless.HNil

class LibraCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[Quantity[Double, HNil]]", StringDecoderTests[Quantity[Double, HNil]].decoder[Int, Int])
  checkAll(
    "StringDecoder[Quantity[Double, HNil]]",
    SerializableTests[StringDecoder[Quantity[Double, HNil]]].serializable
  )

  checkAll("StringDecoder[Quantity[Int, HNil]]", StringDecoderTests[Quantity[Int, HNil]].decoder[Int, Int])
  checkAll("StringDecoder[Quantity[Int, HNil]]", SerializableTests[StringDecoder[Quantity[Int, HNil]]].serializable)

  checkAll("StringEncoder[Quantity[Double, HNil]]", StringEncoderTests[Quantity[Double, HNil]].encoder[Int, Int])
  checkAll(
    "StringEncoder[Quantity[Double, HNil]]",
    SerializableTests[StringEncoder[Quantity[Double, HNil]]].serializable
  )

  checkAll("StringEncoder[Quantity[Int, HNil]]", StringEncoderTests[Quantity[Int, HNil]].encoder[Int, Int])
  checkAll("StringEncoder[Quantity[Int, HNil]]", SerializableTests[StringEncoder[Quantity[Int, HNil]]].serializable)

  checkAll("StringCodec[Quantity[Double, HNil]]", StringCodecTests[Quantity[Double, HNil]].codec[Int, Int])

  checkAll("StringCodec[Quantity[Int, HNil]]", StringCodecTests[Quantity[Int, HNil]].codec[Int, Int])

}
