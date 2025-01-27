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

package kantan.codecs.cats.laws.discipline

import cats.Eq
import cats.instances.either._
import kantan.codecs.Decoder
import kantan.codecs.Encoder
import org.scalacheck.Arbitrary

object equality extends EqInstances

trait EqInstances {

  // This is needed because cats 2.0.0 introduces the notion of ExhaustiveCheck to derive instances of Eq[A => B], and
  // I do not know how to provide an exhaustive list of string values.
  implicit def eqFunction[A: Arbitrary, B: Eq]: Eq[A => B] =
    new Eq[A => B] {
      def eqv(x: A => B, y: A => B): Boolean = {
        val samples = List.fill(50)(Arbitrary.arbitrary[A].sample).collect {
          case Some(a) => a
          case None    => sys.error("Could not generate arbitrary values to compare two functions")
        }
        samples.forall(a => Eq[B].eqv(x(a), y(a)))
      }
    }

  implicit def decoderEq[E: Arbitrary, D: Eq, F: Eq, T]: Eq[Decoder[E, D, F, T]] =
    Eq.by(_.decode _)

  implicit def encoderEq[E: Eq, D: Arbitrary, T]: Eq[Encoder[E, D, T]] =
    Eq.by(_.encode _)

}
