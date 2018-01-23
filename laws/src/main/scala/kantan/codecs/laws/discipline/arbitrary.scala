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
package laws
package discipline

import CodecValue.{IllegalValue, LegalValue}
import imp.imp
import java.io._
import java.net.{URI, URL}
import java.nio.file.{AccessMode, Path}
import java.util.{Date, UUID}
import java.util.regex.Pattern
import org.scalacheck._, Arbitrary.{arbitrary ⇒ arb}, Gen._
import scala.util.Try
import scala.util.matching.Regex
import strings.DecodeError

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances extends ArbitraryArities {

  // - Arbitrary AccessMode --------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // This is just a sample java enum.
  implicit val arbAccessMode: Arbitrary[AccessMode] =
    Arbitrary(Gen.oneOf(AccessMode.READ, AccessMode.WRITE, AccessMode.EXECUTE))

  implicit val cogenAccessMode: Cogen[AccessMode] = implicitly[Cogen[String]].contramap(_.name())

  // - Arbitrary patterns ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val genRegexOptions: Gen[Int] = listOf(
    oneOf(
      Pattern.UNIX_LINES,
      256,
      Pattern.CANON_EQ,
      Pattern.CASE_INSENSITIVE,
      Pattern.MULTILINE,
      Pattern.DOTALL,
      Pattern.LITERAL,
      Pattern.UNICODE_CASE,
      Pattern.COMMENTS
    )
  ).map(_.toSet.foldLeft(0)(_ | _))

  // TODO: add more standard regexes to this list?
  val genRegularExpression: Gen[String] =
    oneOf("[a-zA-z]", "^[a-z0-9_-]{3,16}$", "^[a-z0-9_-]{6,18}$", "^#?([a-f0-9]{6}|[a-f0-9]{3})$")

  val genPattern: Gen[Pattern] = for {
    regex ← genRegularExpression
    flags ← genRegexOptions
  } yield Pattern.compile(regex, flags)

  implicit val arbPattern: Arbitrary[Pattern] = Arbitrary(genPattern)
  implicit val cogenPattern: Cogen[Pattern]   = implicitly[Cogen[(String, Int)]].contramap(p ⇒ (p.pattern(), p.flags()))

  implicit val arbRegex: Arbitrary[Regex] = Arbitrary(genRegularExpression.map(_.r))
  implicit val cogenRegex: Cogen[Regex]   = implicitly[Cogen[String]].contramap(_.pattern.pattern())

  // - CodecValue ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbValue[E, D, T](implicit arbL: Arbitrary[LegalValue[E, D, T]],
                                 arbI: Arbitrary[IllegalValue[E, D, T]]): Arbitrary[CodecValue[E, D, T]] =
    Arbitrary(Gen.oneOf(arbL.arbitrary, arbI.arbitrary))

  implicit def arbLegalValueFromEnc[E, A: Arbitrary, T](implicit ea: Encoder[E, A, T]): Arbitrary[LegalValue[E, A, T]] =
    arbLegalValue(ea.encode)

  implicit def arbIllegalValueFromDec[E: Arbitrary, A, T](
    implicit da: Decoder[E, A, _, T]
  ): Arbitrary[IllegalValue[E, A, T]] = arbIllegalValue(e ⇒ da.decode(e).isLeft)

  def arbLegalValue[E, A, T](encode: A ⇒ E)(implicit arbA: Arbitrary[A]): Arbitrary[LegalValue[E, A, T]] = Arbitrary {
    arbA.arbitrary.map(a ⇒ LegalValue(encode(a), a))
  }

  def arbIllegalValue[E: Arbitrary, A, T](illegal: E ⇒ Boolean): Arbitrary[IllegalValue[E, A, T]] =
    Arbitrary {
      imp[Arbitrary[E]].arbitrary.suchThat(illegal).map(e ⇒ IllegalValue(e))
    }

  implicit def arbIllegalURI[T]: Arbitrary[IllegalValue[String, URI, T]] = Arbitrary {
    for {
      str ← Gen.nonEmptyListOf(Gen.alphaNumChar)
      i   ← Gen.choose(0, str.length)
    } yield {
      val (h, t) = str.splitAt(i)
      IllegalValue(s"$h $t")
    }
  }

  // - Codecs ----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbDecoder[E: Arbitrary: Cogen, D: Arbitrary, F: Arbitrary, T]: Arbitrary[Decoder[E, D, F, T]] =
    Arbitrary(arb[E ⇒ Either[F, D]].map(Decoder.from))

  implicit def arbEncoder[E: Arbitrary, D: Arbitrary: Cogen, T]: Arbitrary[Encoder[E, D, T]] =
    Arbitrary(arb[D ⇒ E].map(Encoder.from))

  // - String codecs ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbStringDecodeError: Arbitrary[DecodeError] = Arbitrary(
    Gen.oneOf(
      for {
        id    ← Gen.identifier
        value ← implicitly[Arbitrary[String]].arbitrary
      } yield DecodeError(s"'$value' is not a valid $id"),
      arbException.arbitrary.map(DecodeError.apply)
    )
  )

  val genPathElement: Gen[String] = for {
    length ← choose(1, 10)
    path   ← listOfN(length, alphaLowerChar)
  } yield path.mkString

  val genURL: Gen[URL] = for {
    protocol ← oneOf("http", "https")
    host     ← identifier
    port     ← choose(0, 65535)
    length   ← choose(0, 5)
    path     ← listOfN(length, genPathElement)
  } yield new URL(protocol, host, port, path.mkString("/", "/", ""))

  implicit val arbURL: Arbitrary[URL] = Arbitrary(genURL)
  implicit val arbURI: Arbitrary[URI] = Arbitrary(genURL.map(_.toURI))

  implicit val arbFile: Arbitrary[File] = Arbitrary(
    Gen.nonEmptyListOf(Gen.identifier).map(ss ⇒ new File(ss.fold("")(_ + System.getProperty("file.separator") + _)))
  )

  implicit val arbPath: Arbitrary[Path] = Arbitrary(arbFile.arbitrary.map(_.toPath))

  // This is necessary to prevent ScalaCheck from generating BigDecimal values that cannot be serialized because their
  // scale is higher than MAX_INT.
  // Note that this isn't actually an issue with ScalaCheck but with Scala itself, and is(?) fixed in Scala 2.12:
  // https://github.com/scala/scala/pull/4320
  implicit lazy val arbBigDecimal: Arbitrary[BigDecimal] = {
    import java.math.MathContext._
    val mcGen = oneOf(DECIMAL32, DECIMAL64, DECIMAL128)
    val bdGen = for {
      x     ← Arbitrary.arbitrary[BigInt]
      mc    ← mcGen
      limit ← const(math.max(x.abs.toString.length - mc.getPrecision, 0))
      scale ← Gen.choose(Int.MinValue + limit, Int.MaxValue)
    } yield {
      try {
        BigDecimal(x, scale, mc)
      } catch {
        // Handle the case where scale/precision conflict
        case _: java.lang.ArithmeticException ⇒ BigDecimal(x, scale, UNLIMITED)
      }
    }
    Arbitrary(bdGen)
  }

  // Saner arbitrary date: the default one causes issues with long overflow / underflow.
  implicit val arbDate: Arbitrary[Date] = {
    val now = System.currentTimeMillis()
    Arbitrary(Arbitrary.arbitrary[Int].map(offset ⇒ new Date(now + offset)))
  }

  implicit val arbUuid: Arbitrary[UUID] = Arbitrary(Gen.uuid)

  // - Cogen -----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val cogenUrl: Cogen[URL]                       = implicitly[Cogen[String]].contramap(_.toString)
  implicit val cogenUri: Cogen[URI]                       = implicitly[Cogen[String]].contramap(_.toString)
  implicit val cogenStringDecodeError: Cogen[DecodeError] = implicitly[Cogen[String]].contramap(_.message)
  implicit val cogenUUID: Cogen[UUID] =
    Cogen.tuple2[Long, Long].contramap(uuid ⇒ (uuid.getMostSignificantBits, uuid.getLeastSignificantBits))
  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  implicit val cogenPath: Cogen[Path] = implicitly[Cogen[String]].contramap(_.toString)
  implicit val cogenFile: Cogen[File] = implicitly[Cogen[String]].contramap(_.toString)
  implicit val cogenDate: Cogen[Date] = Cogen(_.getTime)

  // - Exceptions ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val genFileNotFound: Gen[FileNotFoundException] =
    arbFile.arbitrary.map(f ⇒ new FileNotFoundException(s"File not found: $f"))

  val genUnsupportedEncoding: Gen[UnsupportedEncodingException] =
    Gen.identifier.map(i ⇒ new UnsupportedEncodingException(s"Unsupported encoding: $i"))

  val genIoException: Gen[IOException] =
    Gen.oneOf(genFileNotFound, genUnsupportedEncoding, Gen.const(new EOFException))

  val genIllegalArgument: Gen[IllegalArgumentException] =
    Gen.identifier.map(a ⇒ new IllegalArgumentException(s"Illegal argument: $a"))

  implicit val arbIoException: Arbitrary[IOException] = Arbitrary(genIoException)

  implicit val genException: Gen[Exception] = Gen.oneOf(
    genIoException,
    genIllegalArgument,
    Gen.const(new NullPointerException),
    Gen.const(new IllegalArgumentException)
  )

  implicit val arbException: Arbitrary[Exception] = Arbitrary(genException)

  implicit def arbTry[A](implicit aa: Arbitrary[A]): Arbitrary[Try[A]] =
    Arbitrary(
      Gen.oneOf(
        arb[Exception].map(e ⇒ scala.util.Failure(e): Try[A]),
        aa.arbitrary.map(a ⇒ scala.util.Success(a): Try[A])
      )
    )
}
