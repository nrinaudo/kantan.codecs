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

import kantan.codecs.DecoderCompanion

/** Provides instance creation and summing methods for [[StringDecoder]].
  *
  * Default [[StringDecoder]] instances are provided in [[codecs]].
  */
object StringDecoder extends DecoderCompanion[String, DecodeError, codecs.type] with PlatformSpecificDecoders {

  /** Creates a safe decoding function from the specified unsafe one.
    *
    * This method expects the specified decoding function to be able to fail by throwing exceptions. These will be
    * caught and wrapped in [[DecodeError]].
    *
    * This is typically used in conjunction with [StringDecoder.from], when creating instances for types that are not
    * isomorphic to `String`.
    *
    * @example
    *   {{{
    * scala> val decoder = StringDecoder.makeSafe("Int")(_.toInt)
    *
    * scala> decoder("1")
    * res1: StringResult[Int] = Right(1)
    *
    * scala> decoder("foobar")
    * res2: StringResult[Int] = Left(DecodeError: 'foobar' is not a valid Int)
    *   }}}
    *
    * @param typeName
    *   name of the decoded type (used in error messages).
    * @param f
    *   decoding function.
    * @tparam D
    *   decoded type.
    */
  def makeSafe[D](typeName: String)(f: String => D): String => StringResult[D] =
    s => StringResult(f(s)).left.map(t => DecodeError(s"'$s' is not a valid $typeName", t))

}
