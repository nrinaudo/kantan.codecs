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

import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import kantan.codecs.strings.joda.time._
import kantan.codecs.strings.joda.time.laws.discipline.arbitrary._
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class LocalDateCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = DateTimeFormat.mediumDate()

  checkAll("StringDecoder[LocalDate]",DecoderTests[String, LocalDate, DecodeError, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[LocalDate]", EncoderTests[String, LocalDate, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[LocalDate]", CodecTests[String, LocalDate, DecodeError, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[LocalDate]", DecoderTests[String, LocalDate, DecodeError, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[LocalDate]", EncoderTests[String, LocalDate, tagged.type].encoder[Int, Int])
}
