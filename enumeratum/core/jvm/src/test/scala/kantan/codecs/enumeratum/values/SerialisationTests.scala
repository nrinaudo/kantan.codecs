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

package kantan.codecs.enumeratum.values

import kantan.codecs.enumeratum.laws.discipline.{
  DisciplineSuite,
  EnumeratedByte,
  EnumeratedChar,
  EnumeratedInt,
  EnumeratedLong,
  EnumeratedShort,
  EnumeratedString,
  SerializableTests
}
import kantan.codecs.strings.{StringDecoder, StringEncoder}

class SerialisationTests extends DisciplineSuite {

  checkAll("StringDecoder[EnumeratedByte]", SerializableTests[StringDecoder[Int]].serializable)
  checkAll("StringEncoder[EnumeratedByte]", SerializableTests[StringEncoder[EnumeratedByte]].serializable)

  checkAll("StringDecoder[EnumeratedChar]", SerializableTests[StringDecoder[Int]].serializable)
  checkAll("StringEncoder[EnumeratedChar]", SerializableTests[StringEncoder[EnumeratedChar]].serializable)

  checkAll("StringDecoder[EnumeratedInt]", SerializableTests[StringDecoder[Int]].serializable)
  checkAll("StringEncoder[EnumeratedInt]", SerializableTests[StringEncoder[EnumeratedInt]].serializable)

  checkAll("StringDecoder[EnumeratedLong]", SerializableTests[StringDecoder[Int]].serializable)
  checkAll("StringEncoder[EnumeratedLong]", SerializableTests[StringEncoder[EnumeratedLong]].serializable)

  checkAll("StringDecoder[EnumeratedShort]", SerializableTests[StringDecoder[Int]].serializable)
  checkAll("StringEncoder[EnumeratedShort]", SerializableTests[StringEncoder[EnumeratedShort]].serializable)

  checkAll("StringDecoder[EnumeratedString]", SerializableTests[StringDecoder[Int]].serializable)
  checkAll("StringEncoder[EnumeratedString]", SerializableTests[StringEncoder[EnumeratedString]].serializable)

}
