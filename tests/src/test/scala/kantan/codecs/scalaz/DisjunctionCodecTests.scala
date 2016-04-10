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

import kantan.codecs.laws.discipline.{CodecTests => CTests, DecoderTests => DTests, EncoderTests => ETests}
import kantan.codecs.scalaz.laws.discipline.arbitrary._
import kantan.codecs.strings.codecs
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import scalaz.\/

class DisjunctionCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Int \\/ Boolean]", DTests[String, Int \/ Boolean, Throwable, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Int \\/ Boolean]", ETests[String, Int \/ Boolean, codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Int \\/ Boolean]", CTests[String, Int \/ Boolean, Throwable, codecs.type].codec[Int, Int])
}
