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

import kantan.codecs.Codec
import kantan.codecs.Decoder
import kantan.codecs.Encoder

object tagged {

  implicit def decoder[D: StringDecoder]: Decoder[String, D, DecodeError, tagged.type] =
    StringDecoder[D].tag[tagged.type]

  implicit def encoder[D: StringEncoder]: Encoder[String, D, tagged.type] =
    StringEncoder[D].tag[tagged.type]

  def codec[D: StringCodec]: Codec[String, D, DecodeError, tagged.type] =
    implicitly[StringCodec[D]].tag[tagged.type]

}
