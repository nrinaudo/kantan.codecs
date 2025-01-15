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

package kantan.codecs.enumeratum.laws.discipline

import enumeratum.Enum
import enumeratum.EnumEntry
import enumeratum.values._

/** Defines a standard `Enum` for tests to use. */
sealed trait Enumerated extends EnumEntry

object Enumerated extends Enum[Enumerated] {

  val values = findValues

  case object Value1 extends Enumerated
  case object Value2 extends Enumerated
  case object Value3 extends Enumerated

}

/** Defines a standard `IntEnum` for tests to use.
  *
  * See [this Enumeratum issue](https://github.com/lloydmeta/enumeratum/issues/140) for an explanation to the odd way
  * this is designed.
  */
sealed abstract class EnumeratedInt extends IntEnumEntry

object EnumeratedInt extends IntEnum[EnumeratedInt] {

  val values = findValues

  case object Value1 extends EnumeratedInt { override val value = 1 }
  case object Value2 extends EnumeratedInt { override val value = 2 }
  case object Value3 extends EnumeratedInt { override val value = 3 }

}

/** Defines a standard `LongEnum` for tests to use.
  *
  * See [this Enumeratum issue](https://github.com/lloydmeta/enumeratum/issues/140) for an explanation to the odd way
  * this is designed.
  */
sealed abstract class EnumeratedLong extends LongEnumEntry

object EnumeratedLong extends LongEnum[EnumeratedLong] {

  val values = findValues

  case object Value1 extends EnumeratedLong { override val value = 1L }
  case object Value2 extends EnumeratedLong { override val value = 2L }
  case object Value3 extends EnumeratedLong { override val value = 3L }

}

/** Defines a standard `ShortEnum` for tests to use.
  *
  * See [this Enumeratum issue](https://github.com/lloydmeta/enumeratum/issues/140) for an explanation to the odd way
  * this is designed.
  */
sealed abstract class EnumeratedShort extends ShortEnumEntry

object EnumeratedShort extends ShortEnum[EnumeratedShort] {

  val values = findValues

  case object Value1 extends EnumeratedShort { override val value: Short = 1 }
  case object Value2 extends EnumeratedShort { override val value: Short = 2 }
  case object Value3 extends EnumeratedShort { override val value: Short = 3 }

}

/** Defines a standard `StringEnum` for tests to use.
  *
  * See [this Enumeratum issue](https://github.com/lloydmeta/enumeratum/issues/140) for an explanation to the odd way
  * this is designed.
  */
sealed abstract class EnumeratedString extends StringEnumEntry

object EnumeratedString extends StringEnum[EnumeratedString] {

  val values = findValues

  case object Value1 extends EnumeratedString { override val value = "1" }
  case object Value2 extends EnumeratedString { override val value = "2" }
  case object Value3 extends EnumeratedString { override val value = "3" }

}

/** Defines a standard `ByteEnum` for tests to use.
  *
  * See [this Enumeratum issue](https://github.com/lloydmeta/enumeratum/issues/140) for an explanation to the odd way
  * this is designed.
  */
sealed abstract class EnumeratedByte extends ByteEnumEntry

object EnumeratedByte extends ByteEnum[EnumeratedByte] {

  val values = findValues

  case object Value1 extends EnumeratedByte { override val value: Byte = 1 }
  case object Value2 extends EnumeratedByte { override val value: Byte = 2 }
  case object Value3 extends EnumeratedByte { override val value: Byte = 3 }

}

/** Defines a standard `CharEnum` for tests to use.
  *
  * See [this Enumeratum issue](https://github.com/lloydmeta/enumeratum/issues/140) for an explanation to the odd way
  * this is designed.
  */
sealed abstract class EnumeratedChar extends CharEnumEntry

object EnumeratedChar extends CharEnum[EnumeratedChar] {

  val values = findValues

  case object Value1 extends EnumeratedChar { override val value = '1' }
  case object Value2 extends EnumeratedChar { override val value = '2' }
  case object Value3 extends EnumeratedChar { override val value = '3' }

}
