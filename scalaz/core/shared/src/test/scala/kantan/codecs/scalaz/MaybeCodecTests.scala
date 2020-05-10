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

package kantan.codecs.scalaz

import kantan.codecs.scalaz.laws.discipline.{DisciplineSuite, StringCodecTests, StringDecoderTests, StringEncoderTests}
import kantan.codecs.scalaz.laws.discipline.arbitrary._
import scalaz.Maybe

class MaybeCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[Maybe[Int]]", StringDecoderTests[Maybe[Int]].decoder[Int, Int])
  checkAll("StringEncoder[Maybe[Int]]", StringEncoderTests[Maybe[Int]].encoder[Int, Int])
  checkAll("StringCodec[Maybe[Int]]", StringCodecTests[Maybe[Int]].codec[Int, Int])

}
