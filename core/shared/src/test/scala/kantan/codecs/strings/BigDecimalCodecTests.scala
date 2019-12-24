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

class BigDecimalCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[BigDecimal]", StringCodecTests[BigDecimal].decoder[Int, Int])
  checkAll("StringEncoder[BigDecimal]", StringCodecTests[BigDecimal].encoder[Int, Int])
  checkAll("StringCodec[BigDecimal]", StringCodecTests[BigDecimal].codec[Int, Int])

  checkAll("TaggedDecoder[BigDecimal]", tagged.DecoderTests[BigDecimal].decoder[Int, Int])
  checkAll("TaggedEncoder[BigDecimal]", tagged.EncoderTests[BigDecimal].encoder[Int, Int])

}
