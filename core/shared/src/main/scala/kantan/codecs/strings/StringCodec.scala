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

import kantan.codecs.CodecCompanion
import scala.reflect.ClassTag

/** Provides instance creation methods.
  *
  * No instance summoning method is provided - this is by design. Developers should never work with codecs, but with
  * instances of [[StringEncoder]] and [[StringDecoder]] instead. Codecs are merely meant as a declaration convenience.
  *
  * Default instances are defined in [[codecs]].
  */
object StringCodec extends CodecCompanion[String, DecodeError, codecs.type] with PlatformSpecificCodecs {

  /** Creates a [[StringCodec]] from the specified (unsafe) decode and encode functions.
    *
    * This methods allows callers to provide a decode method that throws, and have the error handled neatly.
    *
    * @example
    * {{{
    * scala> val intCodec: StringCodec[Int] =
    *      |   StringCodec.fromUnsafe(
    *      |     decode = Integer.parseInt,
    *      |     encode = decoded => decoded.toString()
    *      |   )
    *
    * scala> intCodec.decode("Foobar")
    * res0: StringResult[Int] = Left(DecodeError: 'Foobar' is not a valid int)
    * }}}
    *
    * @see [[fromUnsafe[D](typeName:String)* fromUnsafe]]
    */
  def fromUnsafe[D](decode: String => D, encode: D => String)(implicit tag: ClassTag[D]): StringCodec[D] =
    StringCodec.from(StringDecoder.makeSafe(decode))(encode)

  /** Creates a [[StringCodec]] from the specified (unsafe) decode and encode functions.
    *
    * @see [[fromUnsafe[D](decode:String=>D,encode:D=>String)* fromUnsafe]]
    */
  def fromUnsafe[D](typeName: String)(decode: String => D, encode: D => String): StringCodec[D] =
    StringCodec.from(StringDecoder.makeSafe(typeName)(decode))(encode)
}
