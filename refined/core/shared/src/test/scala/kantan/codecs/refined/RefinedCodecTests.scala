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

package kantan.codecs.refined

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import kantan.codecs.refined.laws.discipline.{DisciplineSuite, StringDecoderTests, StringEncoderTests}
import kantan.codecs.refined.laws.discipline.arbitrary._

class RefinedCodecTests extends DisciplineSuite {

  checkAll("StringDecoder[Int Refined Positive]", StringDecoderTests[Int Refined Positive].decoder[Int, Int])
  checkAll("StringEncoder[Int Refined Positive]", StringEncoderTests[Int Refined Positive].encoder[Int, Int])

}
