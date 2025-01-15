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

package kantan.codecs.refined.laws.discipline

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.scalacheck._
import kantan.codecs.laws.CodecValue
import kantan.codecs.laws.IllegalString
import kantan.codecs.laws.LegalString
import kantan.codecs.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary

object arbitrary extends ArbitraryInstances with kantan.codecs.laws.discipline.ArbitraryInstances

trait ArbitraryInstances
    extends BooleanInstances with CharInstances with GenericInstances with NumericInstances with RefTypeInstances
    with StringInstances {

  implicit val arbLegalPositiveIntString: Arbitrary[LegalString[Int Refined Positive]] =
    arbLegalValue(_.toString)
  // format: off
  implicit val arbIllegalPositiveIntString: Arbitrary[IllegalString[Int Refined Positive]] =
    Arbitrary(Arbitrary.arbitrary[Int].map(i => CodecValue.IllegalValue((-math.abs(i)).toString)))
  // format: on
}
