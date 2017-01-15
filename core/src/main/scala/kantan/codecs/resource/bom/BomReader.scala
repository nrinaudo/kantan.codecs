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

package kantan.codecs.resource.bom

import java.io._
import scala.io.Codec

object BomReader {
  /** Size, in bytes, of the largest BOM we support. */
  private val maxBytes: Int = ByteOrderMark.values.foldLeft(0)((acc, bom) ⇒ math.max(acc, bom.bytes.length))

  /** Opens a BOM-aware reader on the specified input stream.
    *
    * If the specified input stream contains a known BOM, discard it and use the associated charset to turn bytes into
    * characters. Otherwise, use the specified codec.
    */
  def apply(input: InputStream, codec: Codec): Reader = {
    val buf: Array[Byte] = {
      val buf  = new Array[Byte](maxBytes)
      val read = input.read(buf)

      if(read == maxBytes) buf
      else {
        val trimmed = new Array[Byte](read)
        System.arraycopy(buf, 0, trimmed, 0, read)
        trimmed
      }
    }

    ByteOrderMark.values.sortBy(-_.bytes.length).find { bom ⇒
      (bom.bytes.length <= buf.length) && bom.bytes.indices.forall(i ⇒ bom.bytes(i) == buf(i))
    } match {
      case None ⇒
        val in = new PushbackInputStream(input, buf.length)
        in.unread(buf)
        new InputStreamReader(in, codec.charSet)

      case Some(bom) ⇒
        if(bom.bytes.length == buf.length) new InputStreamReader(input, bom.charset)
        else {
          try {
            val in = new PushbackInputStream(input, buf.length - bom.bytes.length)
            in.unread(buf, bom.bytes.length, buf.length - bom.bytes.length)
            new InputStreamReader(in, bom.charset)
          }
          catch {
            case e: Exception ⇒ e.printStackTrace(); throw e
          }
        }
    }
  }
}
