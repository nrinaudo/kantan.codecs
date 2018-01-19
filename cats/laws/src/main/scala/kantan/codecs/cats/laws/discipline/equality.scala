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
import org.scalacheck.Arbitrary

object equality extends EqInstances

trait EqInstances {

  implicit def decoderEq[E: Arbitrary, D: Eq, F: Eq, T]: Eq[Decoder[E, D, F, T]] = new Eq[Decoder[E, D, F, T]] {
    override def eqv(a1: Decoder[E, D, F, T], a2: Decoder[E, D, F, T]) =
      kantan.codecs.laws.discipline.equality.eq(a1.decode, a2.decode) { (d1, d2) ⇒
        implicitly[Eq[Either[F, D]]].eqv(d1, d2)
      }
  }

  implicit def encoderEq[E: Eq, D: Arbitrary, T]: Eq[Encoder[E, D, T]] = new Eq[Encoder[E, D, T]] {
    override def eqv(a1: Encoder[E, D, T], a2: Encoder[E, D, T]) =
      kantan.codecs.laws.discipline.equality.eq(a1.encode, a2.encode) { (e1, e2) ⇒
        implicitly[Eq[E]].eqv(e1, e2)
      }
  }

}
