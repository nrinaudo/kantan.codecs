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

import kantan.codecs.laws.discipline.DisciplineSuite
import kantan.codecs.laws.discipline.SerializableTests

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.AccessMode
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SerializationTests extends DisciplineSuite {

  checkAll("StringDecoder[BigDecimal]", SerializableTests[StringDecoder[BigDecimal]].serializable)
  checkAll("StringEncoder[BigDecimal]", SerializableTests[StringEncoder[BigDecimal]].serializable)

  checkAll("StringDecoder[BigInt]", SerializableTests[StringDecoder[BigInt]].serializable)
  checkAll("StringEncoder[BigInt]", SerializableTests[StringEncoder[BigInt]].serializable)

  checkAll("StringDecoder[Boolean]", SerializableTests[StringDecoder[Boolean]].serializable)
  checkAll("StringEncoder[Boolean]", SerializableTests[StringEncoder[Boolean]].serializable)

  checkAll("StringDecoder[Byte]", SerializableTests[StringDecoder[Byte]].serializable)
  checkAll("StringEncoder[Byte]", SerializableTests[StringEncoder[Byte]].serializable)

  checkAll("StringDecoder[Char]", SerializableTests[StringDecoder[Char]].serializable)
  checkAll("StringEncoder[Char]", SerializableTests[StringEncoder[Char]].serializable)

  implicit val codec: StringCodec[Date] =
    StringCodec.dateCodec(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz", Locale.ENGLISH))

  checkAll("StringDecoder[Date]", SerializableTests[StringDecoder[Date]].serializable)
  checkAll("StringEncoder[Date]", SerializableTests[StringEncoder[Date]].serializable)

  checkAll("StringDecoder[Double]", SerializableTests[StringDecoder[Double]].serializable)
  checkAll("StringEncoder[Double]", SerializableTests[StringEncoder[Double]].serializable)

  checkAll("StringDecoder[Either[Int, Boolean]]", SerializableTests[StringDecoder[Either[Int, Boolean]]].serializable)
  checkAll("StringEncoder[Either[Int, Boolean]]", SerializableTests[StringEncoder[Either[Int, Boolean]]].serializable)

  checkAll("StringDecoder[Float]", SerializableTests[StringDecoder[Float]].serializable)
  checkAll("StringEncoder[Float]", SerializableTests[StringEncoder[Float]].serializable)

  checkAll("StringDecoder[Int]", SerializableTests[StringDecoder[Int]].serializable)
  checkAll("StringEncoder[Int]", SerializableTests[StringEncoder[Int]].serializable)

  checkAll("StringDecoder[Long]", SerializableTests[StringDecoder[Long]].serializable)
  checkAll("StringEncoder[Long]", SerializableTests[StringEncoder[Long]].serializable)

  checkAll("StringDecoder[Option[Int]]", SerializableTests[StringDecoder[Option[Int]]].serializable)
  checkAll("StringEncoder[Option[Int]]", SerializableTests[StringEncoder[Option[Int]]].serializable)

  checkAll("StringDecoder[Short]", SerializableTests[StringDecoder[Short]].serializable)
  checkAll("StringEncoder[Short]", SerializableTests[StringEncoder[Short]].serializable)

  checkAll("StringDecoder[String]", SerializableTests[StringDecoder[String]].serializable)
  checkAll("StringEncoder[String]", SerializableTests[StringEncoder[String]].serializable)

  checkAll("StringDecoder[UUID]", SerializableTests[StringDecoder[UUID]].serializable)
  checkAll("StringEncoder[UUID]", SerializableTests[StringEncoder[UUID]].serializable)

  checkAll("StringDecoder[AccessMode]", SerializableTests[StringDecoder[AccessMode]].serializable)
  checkAll("StringEncoder[AccessMode]", SerializableTests[StringEncoder[AccessMode]].serializable)

  checkAll("StringDecoder[File]", SerializableTests[StringDecoder[File]].serializable)
  checkAll("StringEncoder[File]", SerializableTests[StringEncoder[File]].serializable)

  checkAll("StringDecoder[Path]", SerializableTests[StringDecoder[Path]].serializable)
  checkAll("StringEncoder[Path]", SerializableTests[StringEncoder[Path]].serializable)

  checkAll("StringDecoder[URI]", SerializableTests[StringDecoder[URI]].serializable)
  checkAll("StringEncoder[URI]", SerializableTests[StringEncoder[URI]].serializable)

  checkAll("StringDecoder[URL]", SerializableTests[StringDecoder[URL]].serializable)
  checkAll("StringEncoder[URL]", SerializableTests[StringEncoder[URL]].serializable)

}
