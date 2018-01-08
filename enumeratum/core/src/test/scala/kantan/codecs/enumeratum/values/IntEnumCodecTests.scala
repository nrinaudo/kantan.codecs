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

package kantan.codecs.enumeratum.values

import kantan.codecs.enumeratum.laws.discipline._
import kantan.codecs.enumeratum.laws.discipline.arbitrary._
import kantan.codecs.laws.discipline._
import kantan.codecs.strings._
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

class IntEnumCodecTests extends FunSuite with Discipline {

  checkAll("StringDecoder[EnumeratedInt]", StringDecoderTests[EnumeratedInt].decoder[Int, Int])
  checkAll("StringDecoder[EnumeratedInt]", SerializableTests[StringDecoder[Int]].serializable)
  checkAll("StringEncoder[EnumeratedInt]", StringEncoderTests[EnumeratedInt].encoder[Int, Int])
  checkAll("StringEncoder[EnumeratedInt]", SerializableTests[StringEncoder[EnumeratedInt]].serializable)
  checkAll("StringCodec[EnumeratedInt]", StringCodecTests[EnumeratedInt].codec[Int, Int])

}
