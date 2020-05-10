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

package kantan.codecs.strings.java8

import java.time.Instant
import kantan.codecs.strings.{StringDecoder, StringEncoder}
import kantan.codecs.strings.java8.laws.discipline.{
  DisciplineSuite,
  SerializableTests,
  StringCodecTests,
  StringDecoderTests,
  StringEncoderTests
}
import kantan.codecs.strings.java8.laws.discipline.arbitrary._

class InstantCodecTests extends DisciplineSuite {
  checkAll("StringDecoder[Instant]", StringDecoderTests[Instant].decoder[Int, Int])
  checkAll("StringDecoder[Instant]", SerializableTests[StringDecoder[Instant]].serializable)

  checkAll("StringEncoder[Instant]", StringEncoderTests[Instant].encoder[Int, Int])
  checkAll("StringEncoder[Instant]", SerializableTests[StringEncoder[Instant]].serializable)

  checkAll("StringCodec[Instant]", StringCodecTests[Instant].codec[Int, Int])
}
