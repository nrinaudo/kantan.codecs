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

package kantan.codecs.resource

import scala.io.Codec

/** Adds Byte Order Mark support to resources.
  *
  * By importing this package, the default behaviour of kantan libraries will be changed to: * add a BOM when writing
  * textual data (if one exists for the output charset). * attempt to find a BOM when reading textual data and, if one
  * is found, use the corresponding charset (potentially overriding whatever charset was specified "in-code").
  *
  * Those behaviours are not enabled by default as they're very Microsoft-specific.
  */
package object bom {
  implicit def writerFromStream[A: OutputResource](implicit codec: Codec): WriterResource[A] =
    OutputResource[A].map(BomWriter(_, codec))

  implicit def readerFromStream[A: InputResource](implicit codec: Codec): ReaderResource[A] =
    InputResource[A].map(BomReader(_, codec))
}
