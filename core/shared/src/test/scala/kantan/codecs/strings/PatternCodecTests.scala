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

import kantan.codecs.laws.discipline.DisciplineSuite

// TODO: this is currently disabled because of Java's messed up Pattern.equals implementation.
class PatternCodecTests extends DisciplineSuite /*{

  checkAll("StringDecoder[Pattern]", StringDecoderTests[Pattern].bijectiveDecoder[Int, Int])
  checkAll("StringDecoder[Pattern]", SerializableTests[StringDecoder[Pattern]].serializable)

  checkAll("StringEncoder[Pattern]", StringEncoderTests[Pattern].encoder[Int, Int])
  checkAll("StringEncoder[Pattern]", SerializableTests[StringEncoder[Pattern]].serializable)

  checkAll("StringCodec[Pattern]", StringCodecTests[Pattern].bijectiveCodec[Int, Int])

  checkAll("TaggedDecoder[Pattern]", DecoderTests[String, Pattern, DecodeError, tagged.type].bijectiveDecoder[Int, Int])
  checkAll("TaggedEncoder[Pattern]", EncoderTests[String, Pattern, tagged.type].encoder[Int, Int])

}
 */
