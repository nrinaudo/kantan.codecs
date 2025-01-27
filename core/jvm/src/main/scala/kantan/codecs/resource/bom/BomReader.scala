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

import java.io.InputStream
import java.io.InputStreamReader
import java.io.PushbackInputStream
import java.io.Reader
import scala.io.Codec

object BomReader {

  /** Size, in bytes, of the largest BOM we support. */
  private val maxBytes: Int = ByteOrderMark.values.foldLeft(0)((acc, bom) => math.max(acc, bom.bytes.length))

  /** Opens a BOM-aware reader on the specified input stream.
    *
    * If the specified input stream contains a known BOM, discard it and use the associated charset to turn bytes into
    * characters. Otherwise, use the specified codec.
    */
  def apply(input: InputStream, codec: Codec): Reader = {
    def readBom: Option[Array[Byte]] = {
      val buf  = new Array[Byte](maxBytes)
      val read = input.read(buf)

      // Empty stream.
      if(read == -1) None

      // Stream with enough data for the largest BOM
      else if(read == maxBytes) Some(buf)

      // Stream with too little data for the largest BOM, resize our array to fit what was actually read.
      else {
        val trimmed = new Array[Byte](read)
        System.arraycopy(buf, 0, trimmed, 0, read)
        Some(trimmed)
      }
    }

    // If the stream was empty, return reader on it - there's nothing else we can do.
    // Otherwise, attempts to find the largest possible BOM that matches the stream's header.
    // Note that we need to do it this way because some BOMs clash - UTF-16LE has 0xFE 0xFF, and UTF-32LE has
    // 0xFE 0xFF 0x00 0x00.
    readBom.fold(new InputStreamReader(input, codec.charSet)) { buf =>
      ByteOrderMark.values.sortBy(-_.bytes.length).find { bom =>
        (bom.bytes.length <= buf.length) && bom.bytes.indices.forall(i => bom.bytes(i) == buf(i))
      } match {
        // No matching BOM was found, push all bytes back in the stream and open a reader on it.
        case None =>
          val in = new PushbackInputStream(input, buf.length)
          in.unread(buf)
          new InputStreamReader(in, codec.charSet)

        // A matching BOM was found. Either its' BOM is the same size as the amount of bytes we read from the stream,
        // in which case we can just open a reader on the remaining bytes, or it's smaller and we need to push some
        // bytes back into the stream.
        case Some(bom) =>
          if(bom.bytes.length == buf.length) new InputStreamReader(input, bom.charset)
          else {
            val in = new PushbackInputStream(input, buf.length - bom.bytes.length)
            in.unread(buf, bom.bytes.length, buf.length - bom.bytes.length)
            new InputStreamReader(in, bom.charset)
          }
      }
    }
  }
}
