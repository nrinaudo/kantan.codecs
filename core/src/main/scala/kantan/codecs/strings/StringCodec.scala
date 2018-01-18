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
package strings

import java.text.DateFormat
import java.util.Date

/** Provides instance creation methods.
  *
  * No instance summoning method is provided - this is by design. Developers should never work with codecs, but with
  * instances of [[StringEncoder]] and [[StringDecoder]] instead. Codecs are merely meant as a declaration convenience.
  *
  * Default instances are defined in [[codecs]].
  */
object StringCodec {

  /** Creates a new [[StringCodec]] instance from the specified decoding and encoding functions.
    *
    * @param f how to decode to instances of `D`.
    * @param g how to encode instances of `D`.
    * @tparam D decoded type.
    */
  def from[D](f: String ⇒ Either[DecodeError, D])(g: D ⇒ String): StringCodec[D] = Codec.from(f)(g)

  /** Creates a new [[StringCodec]] instance from the specified encoder and decoder.
    *
    * @param d how to decode to instances of `D`.
    * @param e how to encode instances of `D`.
    * @tparam D decoded type.
    */
  def from[D](d: StringDecoder[D], e: StringEncoder[D]): StringCodec[D] = Codec.from(d, e)

  /** Creates a [[StringCodec]] instance for `java.util.Date`. */
  def dateCodec(format: DateFormat): StringCodec[Date] =
    StringCodec.from(StringDecoder.dateDecoder(format), StringEncoder.dateEncoder(format))
}
