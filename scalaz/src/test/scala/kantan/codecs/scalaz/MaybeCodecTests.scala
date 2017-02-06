/*
 * Copyright 2017 Nicolas Rinaudo
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

import kantan.codecs.laws.discipline.{CodecTests ⇒ CTests, DecoderTests ⇒ DTests, EncoderTests ⇒ ETests}
import kantan.codecs.scalaz.laws.discipline.arbitrary._
import kantan.codecs.strings._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import scalaz.Maybe
import scalaz.scalacheck.ScalazArbitrary._

class MaybeCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("StringDecoder[Maybe[Int]]", DTests[String, Maybe[Int], DecodeError, codecs.type].decoder[Int, Int])
  checkAll("StringEncoder[Maybe[Int]]", ETests[String, Maybe[Int], codecs.type].encoder[Int, Int])
  checkAll("StringCodec[Maybe[Int]]", CTests[String, Maybe[Int], DecodeError, codecs.type].codec[Int, Int])
}
