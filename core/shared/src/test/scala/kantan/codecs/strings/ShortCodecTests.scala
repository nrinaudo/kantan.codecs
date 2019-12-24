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

import kantan.codecs.laws.discipline.{DisciplineSuite, StringCodecTests}
import kantan.codecs.laws.discipline.arbitrary._

class ShortCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[Short]", StringCodecTests[Short].decoder[Int, Int])
  checkAll("StringEncoder[Short]", StringCodecTests[Short].encoder[Int, Int])
  checkAll("StringCodec[Short]", StringCodecTests[Short].codec[Int, Int])

  checkAll("TaggedDecoder[Short]", tagged.DecoderTests[Short].decoder[Int, Int])
  checkAll("TaggedEncoder[Short]", tagged.EncoderTests[Short].encoder[Int, Int])

}
