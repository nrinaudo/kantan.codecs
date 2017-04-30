/*
 * Copyright 2017 Nicolas Rinaudo
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

package kantan.codecs.strings.joda.time

import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import kantan.codecs.strings._
import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import org.joda.time.LocalDateTime
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalDateTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[LocalDateTime]",
    DecoderTests[String, LocalDateTime, DecodeError, codecs.type].decoder[Int, Int])
  //checkAll("StringDecoder[LocalDateTime]", SerializableTests[StringEncoder[LocalDateTime]].serializable)

  checkAll("StringEncoder[LocalDateTime]", EncoderTests[String, LocalDateTime, codecs.type].encoder[Int, Int])
  //checkAll("StringEncoder[LocalDateTime]", SerializableTests[StringEncoder[LocalDateTime]].serializable)

  checkAll("StringCodec[LocalDateTime]", CodecTests[String, LocalDateTime, DecodeError, codecs.type].codec[Int, Int])
}
