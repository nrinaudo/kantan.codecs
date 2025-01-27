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

import kantan.codecs.laws.SerializableLaws
import org.scalacheck.Prop
import org.typelevel.discipline.Laws

trait SerializableTests[A] extends Laws {
  def laws: SerializableLaws[A]

  def serializable: RuleSet =
    new DefaultRuleSet(
      "serializable",
      None,
      "serialize" -> Prop(laws.serializable())
    )
}

object SerializableTests {
  def apply[A](implicit l: SerializableLaws[A]): SerializableTests[A] =
    new SerializableTests[A] {
      override def laws =
        l
    }
}
