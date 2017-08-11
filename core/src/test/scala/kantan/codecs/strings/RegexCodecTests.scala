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

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

// TODO: this is currently disabled because of Java's messed up Pattern.equals implementation.
class RegexCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline /*{
  checkAll("StringDecoder[Regex]", DecoderTests[String, Regex, DecodeError, codecs.type].bijectiveDecoder[Int, Int])
  checkAll("StringDecoder[Regex]", SerializableTests[StringDecoder[Regex]].serializable)

  checkAll("StringEncoder[Regex]", EncoderTests[String, Regex, codecs.type].encoder[Int, Int])
  checkAll("StringEncoder[Regex]", SerializableTests[StringEncoder[Regex]].serializable)

  checkAll("StringCodec[Regex]", CodecTests[String, Regex, DecodeError, codecs.type].bijectiveCodec[Int, Int])

  checkAll("TaggedDecoder[Regex]", DecoderTests[String, Regex, DecodeError, tagged.type].bijectiveDecoder[Int, Int])
  checkAll("TaggedEncoder[Regex]", EncoderTests[String, Regex, tagged.type].encoder[Int, Int])
}
 */
