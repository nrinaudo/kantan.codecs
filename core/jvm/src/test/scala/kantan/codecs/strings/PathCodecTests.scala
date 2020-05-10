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
import kantan.codecs.laws.discipline.{
  DecoderTests,
  DisciplineSuite,
  EncoderTests,
  StringCodecTests,
  StringDecoderTests,
  StringEncoderTests
}
import kantan.codecs.laws.discipline.arbitrary._
class PathCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[Path]", StringDecoderTests[Path].bijectiveDecoder[Int, Int])
  checkAll("StringEncoder[Path]", StringEncoderTests[Path].encoder[Int, Int])
  checkAll("StringCodec[Path]", StringCodecTests[Path].bijectiveCodec[Int, Int])

  checkAll("TaggedDecoder[Path]", DecoderTests[String, Path, DecodeError, tagged.type].bijectiveDecoder[Int, Int])
  checkAll("TaggedEncoder[Path]", EncoderTests[String, Path, tagged.type].encoder[Int, Int])

}
