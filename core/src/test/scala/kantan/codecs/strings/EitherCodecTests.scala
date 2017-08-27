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

package kantan.codecs.strings

import kantan.codecs.laws.discipline._
import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tagged._

class EitherCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll(
    "StringDecoder[Either[Int, Boolean]]",
    DecoderTests[String, Either[Int, Boolean], DecodeError, codecs.type].decoder[Int, Int]
  )
  checkAll("StringDecoder[Either[Int, Boolean]]", SerializableTests[StringDecoder[Either[Int, Boolean]]].serializable)

  checkAll(
    "StringEncoder[Either[Int, Boolean]]",
    EncoderTests[String, Either[Int, Boolean], codecs.type].encoder[Int, Int]
  )
  checkAll("StringEncoder[Either[Int, Boolean]]", SerializableTests[StringEncoder[Either[Int, Boolean]]].serializable)

  checkAll(
    "StringCodec[Either[Int, Boolean]]",
    CodecTests[String, Either[Int, Boolean], DecodeError, codecs.type].codec[Int, Int]
  )

  checkAll(
    "TaggedDecoder[Either[Int, Boolean]]",
    DecoderTests[String, Either[Int, Boolean], DecodeError, tagged.type].decoder[Int, Int]
  )
  checkAll(
    "TaggedEncoder[Either[Int, Boolean]]",
    EncoderTests[String, Either[Int, Boolean], tagged.type].encoder[Int, Int]
  )
}
