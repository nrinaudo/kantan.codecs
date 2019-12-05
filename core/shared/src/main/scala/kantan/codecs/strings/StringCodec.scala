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

/** Provides instance creation methods.
  *
  * No instance summoning method is provided - this is by design. Developers should never work with codecs, but with
  * instances of [[StringEncoder]] and [[StringDecoder]] instead. Codecs are merely meant as a declaration convenience.
  *
  * Default instances are defined in [[codecs]].
  */
object StringCodec extends CodecCompanion[String, DecodeError, codecs.type] with PlatformSpecificCodecs
