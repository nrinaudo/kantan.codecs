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

package kantan.codecs.strings.joda.time

import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests, SerializableTests}
import kantan.codecs.strings._
import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import org.joda.time.LocalTime
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[LocalTime]", DecoderTests[String, LocalTime, DecodeError, codecs.type].decoder[Int, Int])
  checkAll("StringDecoder[LocalTime]", SerializableTests[StringEncoder[LocalTime]].serializable)

  checkAll("StringEncoder[LocalTime]", EncoderTests[String, LocalTime, codecs.type].encoder[Int, Int])
  checkAll("StringEncoder[LocalTime]", SerializableTests[StringEncoder[LocalTime]].serializable)

  checkAll("StringCodec[LocalTime]", CodecTests[String, LocalTime, DecodeError, codecs.type].codec[Int, Int])
}
