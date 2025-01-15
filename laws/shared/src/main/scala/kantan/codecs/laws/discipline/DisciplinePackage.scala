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

import kantan.codecs.strings.DecodeError
import kantan.codecs.strings.codecs

trait DisciplinePackage extends PlatformSpecificDisciplinePackage {

  type CodecTests[E, D, F, T] = kantan.codecs.laws.discipline.CodecTests[E, D, F, T]
  val CodecTests = kantan.codecs.laws.discipline.CodecTests

  type DecoderTests[E, D, F, T] = kantan.codecs.laws.discipline.DecoderTests[E, D, F, T]
  val DecoderTests = kantan.codecs.laws.discipline.DecoderTests

  type EncoderTests[E, D, T] = kantan.codecs.laws.discipline.EncoderTests[E, D, T]
  val EncoderTests = kantan.codecs.laws.discipline.EncoderTests

  type StringEncoderTests[A] = EncoderTests[String, A, codecs.type]
  val StringEncoderTests = kantan.codecs.laws.discipline.StringEncoderTests

  type StringDecoderTests[A] = DecoderTests[String, A, DecodeError, codecs.type]
  val StringDecoderTests = kantan.codecs.laws.discipline.StringDecoderTests

  type StringCodecTests[A] = CodecTests[String, A, DecodeError, codecs.type]
  val StringCodecTests = kantan.codecs.laws.discipline.StringCodecTests

  type DisciplineSuite = kantan.codecs.laws.discipline.DisciplineSuite

}
