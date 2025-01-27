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

package kantan.codecs.laws.discipline

import kantan.codecs.laws.LegalString
import kantan.codecs.laws.StringDecoderLaws
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.strings.DecodeError
import kantan.codecs.strings.codecs
import org.scalacheck.Arbitrary
import org.scalacheck.Cogen

object StringDecoderTests {

  def apply[D: Arbitrary: Cogen](implicit
    l: StringDecoderLaws[D],
    al: Arbitrary[LegalString[D]]
  ): StringDecoderTests[D] =
    DecoderTests[String, D, DecodeError, codecs.type]

}
