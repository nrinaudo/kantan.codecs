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

package kantan.codecs.strings

import java.text.DateFormat
import java.util.Date
import kantan.codecs.Encoder

object StringEncoder {

  /** Summons an implicit instance of `StringEncoder[D]` if one can be found, fails compilation otherwise. */
  def apply[D](implicit ev: StringEncoder[D]): StringEncoder[D] = macro imp.summon[StringEncoder[D]]

  def from[D](f: D ⇒ String): StringEncoder[D] = Encoder.from(f)

  def dateEncoder(format: DateFormat): StringEncoder[Date] =
    StringEncoder.from(d ⇒ format.synchronized(format.format(d)))
}
