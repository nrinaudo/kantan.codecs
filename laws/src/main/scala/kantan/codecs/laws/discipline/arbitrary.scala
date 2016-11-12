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

import imp.imp
import java.io._
import java.net.{URI, URL}
import java.nio.file.Path
import java.util.{Date, UUID}
import kantan.codecs._
import kantan.codecs.Result.{Failure, Success}
import kantan.codecs.laws.CodecValue
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.strings.{DecodeError, StringDecoder, StringEncoder}
import org.scalacheck._
import org.scalacheck.Arbitrary.{arbitrary ⇒ arb}
import org.scalacheck.Gen._
import scala.util.Try

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances extends ArbitraryArities {
  // - Arbitrary results -----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def success[S: Arbitrary]: Gen[Success[S]] =
  Arbitrary.arbitrary[S].map(Success.apply)
  def failure[F: Arbitrary]: Gen[Failure[F]] =
    Arbitrary.arbitrary[F].map(Failure.apply)

  implicit def arbSuccess[S: Arbitrary]: Arbitrary[Success[S]] = Arbitrary(success)
  implicit def arbFailure[F: Arbitrary]: Arbitrary[Failure[F]] = Arbitrary(failure)

  implicit def arbResult[F: Arbitrary, S: Arbitrary]: Arbitrary[Result[F, S]] =
    Arbitrary(Gen.oneOf(success[S], failure[F]))



  // - CodecValue ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbValue[E, D, T](implicit arbL: Arbitrary[LegalValue[E, D, T]], arbI: Arbitrary[IllegalValue[E, D, T]])
  : Arbitrary[CodecValue[E, D, T]] =
  Arbitrary(Gen.oneOf(arbL.arbitrary, arbI.arbitrary))

  implicit def arbLegalValueFromEnc[E, A: Arbitrary, T](implicit ea: Encoder[E, A, T]): Arbitrary[LegalValue[E, A, T]] =
    arbLegalValue(ea.encode)

  implicit def arbIllegalValueFromDec[E: Arbitrary, A, T](implicit da: Decoder[E, A, _, T])
  : Arbitrary[IllegalValue[E, A, T]] = arbIllegalValue(e ⇒ da.decode(e).isFailure)

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
      IllegalValue(h + " " + t)
    }
  }



  // - String codecs ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbStringDecodeError: Arbitrary[DecodeError] = Arbitrary(Gen.oneOf(
    for {
      id    ← Gen.identifier
      value ← implicitly[Arbitrary[String]].arbitrary
    } yield DecodeError(s"Not a valid $id: '$value'"),
    arbException.arbitrary.map(DecodeError.apply)
  ))

  implicit def arbStringEncoder[A: Arbitrary: Cogen]: Arbitrary[StringEncoder[A]] =
    Arbitrary(Arbitrary.arbitrary[A ⇒ String].map(StringEncoder.from))

  implicit def arbStringDecoder[A: Arbitrary]: Arbitrary[StringDecoder[A]] =
    Arbitrary(Arbitrary.arbitrary[String ⇒ Result[DecodeError, A]].map(StringDecoder.from))

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
      x ← Arbitrary.arbitrary[BigInt]
      mc ← mcGen
      limit ← const(math.max(x.abs.toString.length - mc.getPrecision, 0))
      scale ← Gen.choose(Int.MinValue + limit , Int.MaxValue)
    } yield {
      try {
        BigDecimal(x, scale, mc)
      } catch {
        // Handle the case where scale/precision conflict
        case ae: java.lang.ArithmeticException ⇒ BigDecimal(x, scale, UNLIMITED)
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
  implicit def cogenResult[A: Cogen, B: Cogen]: Cogen[Result[A, B]] =
  implicitly[Cogen[Either[A, B]]].contramap(_.toEither)

  implicit val cogenUrl: Cogen[URL] = implicitly[Cogen[String]].contramap(_.toString)
  implicit val cogenUri: Cogen[URI] = implicitly[Cogen[String]].contramap(_.toString)
  implicit val cogenStringDecodeError: Cogen[DecodeError] = implicitly[Cogen[String]].contramap(_.message)
  implicit val cogenUUID: Cogen[UUID] =
    Cogen.tuple2[Long, Long].contramap(uuid ⇒ (uuid.getMostSignificantBits, uuid.getLeastSignificantBits))
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
    Arbitrary(Gen.oneOf(arb[Exception].map(scala.util.Failure.apply), aa.arbitrary.map(scala.util.Success.apply)))
}
