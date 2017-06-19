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

import java.util.UUID
import kantan.codecs.laws.discipline._
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.strings.tagged._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class UUIDCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[UUID]", DecoderTests[String, UUID, DecodeError, codecs.type].decoder[Int, Int])
  checkAll("StringDecoder[UUID]", SerializableTests[StringDecoder[UUID]].serializable)

  checkAll("StringEncoder[UUID]", EncoderTests[String, UUID, codecs.type].encoder[Int, Int])
  checkAll("StringEncoder[UUID]", SerializableTests[StringEncoder[UUID]].serializable)

  checkAll("StringCodec[UUID]", CodecTests[String, UUID, DecodeError, codecs.type].codec[Int, Int])

  checkAll("TaggedDecoder[UUID]", DecoderTests[String, UUID, DecodeError, tagged.type].decoder[Int, Int])
  checkAll("TaggedEncoder[UUID]", EncoderTests[String, UUID, tagged.type].encoder[Int, Int])
}
