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

package kantan.codecs.laws.discipline

import kantan.codecs.laws.CodecValue.IllegalValue
import org.scalacheck.Arbitrary
import org.scalacheck.Cogen
import org.scalacheck.Gen

import java.io.EOFException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.AccessMode
import java.nio.file.Path

trait ArbitraryInstances extends CommonArbitraryInstances {
  // This is just a sample java enum.
  implicit val arbAccessMode: Arbitrary[AccessMode] =
    Arbitrary(Gen.oneOf(AccessMode.READ, AccessMode.WRITE, AccessMode.EXECUTE))

  implicit val cogenAccessMode: Cogen[AccessMode] = implicitly[Cogen[String]].contramap(_.name())

  implicit def arbIllegalURI[T]: Arbitrary[IllegalValue[String, URI, T]] =
    Arbitrary {
      for {
        str <- Gen.nonEmptyListOf(Gen.alphaNumChar)
        i   <- Gen.choose(0, str.length)
      } yield {
        val (h, t) = str.splitAt(i)
        IllegalValue(s"${h.toString} ${t.toString}")
      }
    }

  val genPathElement: Gen[String] = for {
    length <- Gen.choose(1, 10)
    path   <- Gen.listOfN(length, Gen.alphaLowerChar)
  } yield path.mkString

  val genURL: Gen[URL] = for {
    protocol <- Gen.oneOf("http", "https")
    host     <- Gen.identifier
    port     <- Gen.choose(0, 65535)
    length   <- Gen.choose(0, 5)
    path     <- Gen.listOfN(length, genPathElement)
  } yield
  // public URI(String scheme, String userInfo, String host, int port, String path, String query, String fragment)
  new URI(protocol, "", host, port, path.mkString("/", "/", ""), "", "").toURL()

  implicit val arbURL: Arbitrary[URL] = Arbitrary(genURL)
  implicit val arbURI: Arbitrary[URI] = Arbitrary(genURL.map(_.toURI))
  implicit val cogenUrl: Cogen[URL]   = implicitly[Cogen[String]].contramap(_.toString)
  implicit val cogenUri: Cogen[URI]   = implicitly[Cogen[String]].contramap(_.toString)

  implicit val arbFile: Arbitrary[File] = Arbitrary(
    Gen.nonEmptyListOf(Gen.identifier).map(ss => new File(ss.fold("")(_ + System.getProperty("file.separator") + _)))
  )

  implicit val arbPath: Arbitrary[Path] = Arbitrary(arbFile.arbitrary.map(_.toPath))

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  implicit val cogenPath: Cogen[Path] = implicitly[Cogen[String]].contramap(_.toString)

  implicit val cogenFile: Cogen[File] = implicitly[Cogen[String]].contramap(_.toString)
  val genFileNotFound: Gen[FileNotFoundException] =
    arbFile.arbitrary.map(f => new FileNotFoundException(s"File not found: ${f.toString}"))
  override val genIoException: Gen[IOException] =
    Gen.oneOf(
      genFileNotFound,
      genUnsupportedEncoding,
      Gen.const(new EOFException)
    )

}
