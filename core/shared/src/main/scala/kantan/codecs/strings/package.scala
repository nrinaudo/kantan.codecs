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

/** Defines codecs for encoding to and decoding from strings.
  *
  * These codecs are not necessary meant to use directly, but more as part of larger ones.
  * [[https://github.com/nrinaudo/kantan.csv kantan.csv]], for example, works with CSV files but delegates the act of
  * encoding to or decoding from a CSV cell to string codecs.
  *
  * Default instances can be found in [[kantan.codecs.strings.codecs]].
  */
package object strings {

  /** Type of values values returned when attempting to decode a `String`. */
  type StringResult[A] = Either[DecodeError, A]

  /** [[Encoder]] for strings.
    *
    * Default instances can be found in [[codecs]].
    */
  type StringEncoder[A] = Encoder[String, A, codecs.type]

  /** [[Decoder]] for strings.
    *
    * Default instances can be found in [[codecs]].
    */
  type StringDecoder[A] = Decoder[String, A, DecodeError, codecs.type]

  /** [[Codec]] for strings. */
  type StringCodec[A] = Codec[String, A, DecodeError, codecs.type]
}
