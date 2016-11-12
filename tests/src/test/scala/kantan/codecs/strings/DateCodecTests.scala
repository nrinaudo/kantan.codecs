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

import java.text.SimpleDateFormat
import java.util.{Date, Locale}
import kantan.codecs.laws.discipline._
import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DateCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val codec = StringCodec.dateCodec(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz", Locale.ENGLISH))

  checkAll("StringDecoder[Date]", DecoderTests[String, Date, DecodeError, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Date]", EncoderTests[String, Date, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Date]", CodecTests[String, Date, DecodeError, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[Date]", DecoderTests[String, Date, DecodeError, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[Date]", EncoderTests[String, Date, tagged.type].encoder[Int, Int])
}
