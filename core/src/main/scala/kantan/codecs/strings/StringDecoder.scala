/*
 * Copyright 2017 Nicolas Rinaudo
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
import kantan.codecs.{Decoder, Result}

object StringDecoder {
  /** Summons an implicit instance of `StringDecoder[D]` if one can be found, fails compilation otherwise. */
  def apply[D](implicit ev: StringDecoder[D]): StringDecoder[D] = macro imp.summon[StringDecoder[D]]

  def from[D](f: String ⇒ Result[DecodeError, D]): StringDecoder[D] = Decoder.from(f)

  def decoder[A](typeName: String)(f: String ⇒ A): String ⇒ Result[DecodeError, A] =
    s ⇒ Result.nonFatal(f(s)).leftMap(t ⇒ DecodeError(s"Not a valid $typeName: '$s'", t))

  def dateDecoder(format: DateFormat): StringDecoder[Date] =
    StringDecoder.from(StringDecoder.decoder("Date")(s ⇒ format.synchronized(format.parse(s))))
}

trait StringDecoderInstances
