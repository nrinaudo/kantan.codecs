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

package kantan.codecs.resource.bom

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import scala.io.Codec

object BomWriter {

  /** Opens a `Writer` on the specified `OutputStream`, writing a BOM if the specified codec has one. */
  def apply(out: OutputStream, codec: Codec): Writer =
    new OutputStreamWriter(
      ByteOrderMark.findFor(codec.charSet).fold(out) { bom =>
        out.write(bom.bytes)
        out
      },
      codec.charSet
    )
}
