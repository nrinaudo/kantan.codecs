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
package laws
package discipline

import _root_.cats._
import _root_.cats.instances.either._
import _root_.cats.laws.discipline.eq._
import org.scalacheck.Arbitrary

object equality extends EqInstances

trait EqInstances {

  implicit def decoderEq[E: Arbitrary, D: Eq, F: Eq, T]: Eq[Decoder[E, D, F, T]] =
    Eq.by(_.decode _)

  implicit def encoderEq[E: Eq, D: Arbitrary, T]: Eq[Encoder[E, D, T]] =
    Eq.by(_.encode _)

}
