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

import java.io.File
import kantan.codecs.laws.discipline._
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.strings.tagged._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FileCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[File]", DecoderTests[String, File, DecodeError, codecs.type].bijectiveDecoder[Int, Int])
  checkAll("StringDecoder[File]", SerializableTests[StringDecoder[File]].serializable)

  checkAll("StringEncoder[File]", EncoderTests[String, File, codecs.type].encoder[Int, Int])
  checkAll("StringEncoder[File]", SerializableTests[StringEncoder[File]].serializable)

  checkAll("StringCodec[File]", CodecTests[String, File, DecodeError, codecs.type].bijectiveCodec[Int, Int])

  checkAll("TaggedDecoder[File]", DecoderTests[String, File, DecodeError, tagged.type].bijectiveDecoder[Int, Int])
  checkAll("TaggedEncoder[File]", EncoderTests[String, File, tagged.type].encoder[Int, Int])
}
