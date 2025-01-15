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

package kantan.codecs.cats

import cats.Eq
import cats.data.EitherT
import cats.laws.discipline.MonadErrorTests
import cats.laws.discipline.SemigroupKTests
import cats.laws.discipline.SemigroupalTests.Isomorphisms
import kantan.codecs.cats.laws.discipline.DisciplineSuite
import kantan.codecs.cats.laws.discipline.arbitrary._
import kantan.codecs.cats.laws.discipline.equality._
import kantan.codecs.strings.DecodeError
import kantan.codecs.strings.StringDecoder

class DecoderTests extends DisciplineSuite {

  // For some reason, these are not derived automatically. I *think* it's to do with StringDecoder being a type alias
  // for a type with many holes, but this is slightly beyond me.
  implicit val eqEitherT: Eq[EitherT[StringDecoder, DecodeError, Int]] =
    EitherT.catsDataEqForEitherT[StringDecoder, DecodeError, Int]

  implicit val iso: Isomorphisms[StringDecoder] =
    Isomorphisms.invariant[StringDecoder]

  checkAll("StringDecoder", SemigroupKTests[StringDecoder].semigroupK[Int])
  checkAll("StringDecoder", MonadErrorTests[StringDecoder, DecodeError].monadError[Int, Int, Int])

}
