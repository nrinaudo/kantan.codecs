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

import java.nio.charset.Charset

private[bom] sealed abstract class ByteOrderMark(val charset: Charset) {
  val bytes: Array[Byte] = "\uFEFF".getBytes(charset)
}

private[bom] object ByteOrderMark {
  object Utf8 extends ByteOrderMark(Charset.forName("UTF-8"))
  object Utf16BE extends ByteOrderMark(Charset.forName("UTF-16BE"))
  object Utf16LE extends ByteOrderMark(Charset.forName("UTF-16LE"))
  object Utf32BE extends ByteOrderMark(Charset.forName("UTF-32BE"))
  object Utf32LE extends ByteOrderMark(Charset.forName("UTF-32LE"))

  def findFor(charset: Charset): Option[ByteOrderMark] =
    values.find(_.charset.name() == charset.name())

  val values: List[ByteOrderMark] = List(Utf8, Utf16BE, Utf16LE, Utf32BE, Utf32LE)
}
