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
package strings
package joda

import export.Exported
import org.joda.time._

package object time extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] with ToFormatLiteral {

  override def encoderFrom[D](d: StringEncoder[D]) = d
  override def decoderFrom[D](d: StringDecoder[D]) = d

  implicit val defaultDateTimeStringDecoder: Exported[StringDecoder[DateTime]] = Exported(defaultDateTimeDecoder)
  implicit val defaultLocalDateTimeStringDecoder: Exported[StringDecoder[LocalDateTime]] = Exported(
    defaultLocalDateTimeDecoder
  )
  implicit val defaultLocalTimeStringDecoder: Exported[StringDecoder[LocalTime]] = Exported(defaultLocalTimeDecoder)
  implicit val defaultLocalDateStringDecoder: Exported[StringDecoder[LocalDate]] = Exported(defaultLocalDateDecoder)

  implicit val defaultDateTimeStringEncoder: Exported[StringEncoder[DateTime]] = Exported(defaultDateTimeEncoder)
  implicit val defaultLocalDateTimeStringEncoder: Exported[StringEncoder[LocalDateTime]] = Exported(
    defaultLocalDateTimeEncoder
  )
  implicit val defaultLocalTimeStringEncoder: Exported[StringEncoder[LocalTime]] = Exported(defaultLocalTimeEncoder)
  implicit val defaultLocalDateStringEncoder: Exported[StringEncoder[LocalDate]] = Exported(defaultLocalDateEncoder)

}
