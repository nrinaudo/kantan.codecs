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

package kantan.codecs

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.strings.{codecs, DecodeError}

package object laws {

  type StringValue[A]            = CodecValue[String, A, codecs.type]
  type LegalString[A]            = LegalValue[String, A, codecs.type]
  type IllegalString[A]          = IllegalValue[String, A, codecs.type]
  type HasLegalStringValues[D]   = HasLegalValues[String, D, codecs.type]
  type HasIllegalStringValues[D] = HasIllegalValues[String, D, codecs.type]

  type StringEncoderLaws[A] = EncoderLaws[String, A, codecs.type]
  type StringDecoderLaws[A] = DecoderLaws[String, A, DecodeError, codecs.type]
  type StringCodecLaws[A]   = CodecLaws[String, A, DecodeError, codecs.type]

}
