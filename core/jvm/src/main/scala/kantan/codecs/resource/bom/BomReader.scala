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

import java.io.{InputStream, InputStreamReader, PushbackInputStream, Reader}
import java.nio.charset.Charset
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

    // Attempts to read something that looks like it might be a byte order mark from `input`.
    def readBom: Option[Array[Byte]] = {
      val buf = new Array[Byte](maxBytes)

      input.read(buf) match {

        // Empty stream.
        case -1 => None

        // Stream with too little data for the largest BOM, resize our array to fit what was actually read.
        case read if read < maxBytes =>
          val trimmed = new Array[Byte](read)
          System.arraycopy(buf, 0, trimmed, 0, read)
          Some(trimmed)

        // Stream with enough data for the largest BOM
        case _ => Some(buf)
      }
    }

    // Attempts to find the largest possible BOM that matches the specified header.
    // Note that we need to do it this way because some BOMs clash - UTF-16LE has 0xFE 0xFF, and UTF-32LE has
    // 0xFE 0xFF 0x00 0x00.
    def findMatchingBom(buf: Array[Byte]): Option[ByteOrderMark] =
      ByteOrderMark.values
        .sortBy(-_.bytes.length)
        .find { bom =>
          (bom.bytes.length <= buf.length) &&
          bom.bytes.indices.forall(i => bom.bytes(i) == buf(i))
        }

    // Pushes back some bytes on the input stream before opening a Reader on it.
    def pushBack(buf: Array[Byte], offset: Int, length: Int, charset: Charset): InputStreamReader = {
      val in = new PushbackInputStream(input, length)
      in.unread(buf, offset, length)
      new InputStreamReader(in, charset)
    }

    // If the stream was empty, return reader on it - there's nothing else we can do.
    // Otherwise, analyse its header to try and match it to known BOMs.
    readBom.fold(new InputStreamReader(input, codec.charSet)) { buf =>
      findMatchingBom(buf) match {

        // No matching BOM was found, push all bytes back in the stream and open a reader on it.
        case None => pushBack(buf, 0, buf.length, codec.charSet)

        // A matching BOM was found, and it's exactly the same size as what we read from the stream.
        // We can just open a Reader on the remaining bytes.
        case Some(bom) if bom.bytes.length == buf.length =>
          new InputStreamReader(input, bom.charset)

        // A matching BOM was found, and it's smaller than what we have read. We need to push the extra bytes back
        // on the stream before opening a reader.
        case Some(bom) =>
          pushBack(buf, bom.bytes.length, buf.length - bom.bytes.length, bom.charset)
      }
    }
  }
}
