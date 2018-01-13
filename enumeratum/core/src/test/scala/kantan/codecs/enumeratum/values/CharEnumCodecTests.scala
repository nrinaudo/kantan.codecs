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

package kantan.codecs
package enumeratum
package values

import laws.discipline._
import laws.discipline.arbitrary._
import strings._

class CharEnumCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[EnumeratedChar]", StringDecoderTests[EnumeratedChar].decoder[Int, Int])
  checkAll("StringDecoder[EnumeratedChar]", SerializableTests[StringDecoder[Int]].serializable)
  checkAll("StringEncoder[EnumeratedChar]", StringEncoderTests[EnumeratedChar].encoder[Int, Int])
  checkAll("StringEncoder[EnumeratedChar]", SerializableTests[StringEncoder[EnumeratedChar]].serializable)
  checkAll("StringCodec[EnumeratedChar]", StringCodecTests[EnumeratedChar].codec[Int, Int])

}
