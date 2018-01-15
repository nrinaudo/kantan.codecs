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
package strings

import laws.discipline._, arbitrary._
import tagged._

class EitherCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[Either[Int, Boolean]]", StringDecoderTests[Either[Int, Boolean]].decoder[Int, Int])
  checkAll("StringDecoder[Either[Int, Boolean]]", SerializableTests[StringDecoder[Either[Int, Boolean]]].serializable)

  checkAll("StringEncoder[Either[Int, Boolean]]", StringEncoderTests[Either[Int, Boolean]].encoder[Int, Int])
  checkAll("StringEncoder[Either[Int, Boolean]]", SerializableTests[StringEncoder[Either[Int, Boolean]]].serializable)

  checkAll("StringCodec[Either[Int, Boolean]]", StringCodecTests[Either[Int, Boolean]].codec[Int, Int])

  checkAll(
    "TaggedDecoder[Either[Int, Boolean]]",
    DecoderTests[String, Either[Int, Boolean], DecodeError, tagged.type].decoder[Int, Int]
  )
  checkAll(
    "TaggedEncoder[Either[Int, Boolean]]",
    EncoderTests[String, Either[Int, Boolean], tagged.type].encoder[Int, Int]
  )

}
