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

import org.scalacheck.Arbitrary

object equality {
  def eq[A, B: Arbitrary](a1: B => A, a2: B => A)(f: (A, A) => Boolean): Boolean = {
    val samples = List.fill(100)(Arbitrary.arbitrary[B].sample).collect { case Some(a) =>
      a
    }
    samples.forall(b => f(a1(b), a2(b)))
  }
}
