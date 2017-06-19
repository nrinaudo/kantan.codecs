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

import java.nio.file.Path
import kantan.codecs.laws.discipline._
import kantan.codecs.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class PathCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Path]", DecoderTests[String, Path, DecodeError, codecs.type].bijectiveDecoder[Int, Int])
  checkAll("StringDecoder[Path]", SerializableTests[StringDecoder[Path]].serializable)

  checkAll("StringEncoder[Path]", EncoderTests[String, Path, codecs.type].encoder[Int, Int])
  checkAll("StringEncoder[Path]", SerializableTests[StringEncoder[Path]].serializable)

  checkAll("StringCodec[Path]", CodecTests[String, Path, DecodeError, codecs.type].bijectiveCodec[Int, Int])

  checkAll("TaggedDecoder[Path]", DecoderTests[String, Path, DecodeError, tagged.type].bijectiveDecoder[Int, Int])
  checkAll("TaggedEncoder[Path]", EncoderTests[String, Path, tagged.type].encoder[Int, Int])
}
