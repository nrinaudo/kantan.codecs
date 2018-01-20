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
package cats

import _root_.cats.Eq
import _root_.cats.data.EitherT
import _root_.cats.instances.all._
import _root_.cats.laws.discipline.{MonadErrorTests, SemigroupKTests}
import _root_.cats.laws.discipline.SemigroupalTests.Isomorphisms
import laws.discipline._, arbitrary._, equality._
import strings.{DecodeError, StringDecoder}

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
