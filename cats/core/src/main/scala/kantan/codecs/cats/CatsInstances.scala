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

import _root_.cats._

trait CatsInstances extends LowPriorityCatsInstances {

  // - Decoder instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit def decoderFunctor[E, F, T]: Functor[Decoder[E, ?, F, T]] = new Functor[Decoder[E, ?, F, T]] {
    override def map[A, B](fa: Decoder[E, A, F, T])(f: A ⇒ B) = fa.map(f)
  }

  // - Encoder instances -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit def encoderContravariant[E, T]: Contravariant[Encoder[E, ?, T]] = new Contravariant[Encoder[E, ?, T]] {
    override def contramap[A, B](fa: Encoder[E, A, T])(f: B ⇒ A) = fa.contramap(f)
  }

}
