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
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DateTimeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val formatter = ISODateTimeFormat.dateTime

  checkAll("StringDecoder[DateTime]", DecoderTests[String, DateTime, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[DateTime]", EncoderTests[String, DateTime, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[DateTime]", CodecTests[String, DateTime, Throwable, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[DateTime]", DecoderTests[String, DateTime, Throwable, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[DateTime]", EncoderTests[String, DateTime, tagged.type].encoder[Int, Int])
}
