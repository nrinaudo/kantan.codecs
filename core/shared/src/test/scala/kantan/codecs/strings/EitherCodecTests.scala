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
import kantan.codecs.strings.tagged._

class EitherCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[Either[Int, Boolean]]", StringCodecTests[Either[Int, Boolean]].decoder[Int, Int])
  checkAll("StringEncoder[Either[Int, Boolean]]", StringCodecTests[Either[Int, Boolean]].encoder[Int, Int])
  checkAll("StringCodec[Either[Int, Boolean]]", StringCodecTests[Either[Int, Boolean]].codec[Int, Int])

  checkAll(
    "TaggedDecoder[Either[Int, Boolean]]",
    tagged.DecoderTests[Either[Int, Boolean]].decoder[Int, Int]
  )
  checkAll(
    "TaggedEncoder[Either[Int, Boolean]]",
    tagged.EncoderTests[Either[Int, Boolean]].encoder[Int, Int]
  )

}
