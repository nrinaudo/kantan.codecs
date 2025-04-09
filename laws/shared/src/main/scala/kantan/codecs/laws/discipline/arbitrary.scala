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

import kantan.codecs.Decoder
import kantan.codecs.Encoder
import kantan.codecs.laws.CodecValue
import kantan.codecs.laws.CodecValue.IllegalValue
import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.strings.DecodeError
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.Cogen
import org.scalacheck.Gen

import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.Date
import java.util.UUID
import java.util.regex.Pattern
import scala.util.Try
import scala.util.matching.Regex

object arbitrary extends ArbitraryInstances

trait CommonArbitraryInstances extends ArbitraryArities {

  // - Arbitrary patterns ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val genRegexOptions: Gen[Int] = Gen
    .listOf(
      Gen.oneOf(
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
    )
    .map(_.toSet.foldLeft(0)(_ | _))

  // TODO: add more standard regexes to this list?
  val genRegularExpression: Gen[String] =
    Gen.oneOf("[a-zA-z]", "^[a-z0-9_-]{3,16}$", "^[a-z0-9_-]{6,18}$", "^#?([a-f0-9]{6}|[a-f0-9]{3})$")

  val genPattern: Gen[Pattern] = for {
    regex <- genRegularExpression
    flags <- genRegexOptions
  } yield Pattern.compile(regex, flags)

  implicit val arbPattern: Arbitrary[Pattern] = Arbitrary(genPattern)
  implicit val cogenPattern: Cogen[Pattern] = implicitly[Cogen[(String, Int)]].contramap(p => (p.pattern(), p.flags()))

  implicit val arbRegex: Arbitrary[Regex] = Arbitrary(genRegularExpression.map(_.r))
  implicit val cogenRegex: Cogen[Regex]   = implicitly[Cogen[String]].contramap(_.pattern.pattern())

  // - CodecValue ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbValue[E, D, T](implicit
    arbL: Arbitrary[LegalValue[E, D, T]],
    arbI: Arbitrary[IllegalValue[E, D, T]]
  ): Arbitrary[CodecValue[E, D, T]] =
    Arbitrary(Gen.oneOf(arbL.arbitrary, arbI.arbitrary))

  implicit def arbLegalValueFromEnc[E, A: Arbitrary, T](implicit ea: Encoder[E, A, T]): Arbitrary[LegalValue[E, A, T]] =
    arbLegalValue(ea.encode)

  implicit def arbIllegalValueFromDec[E: Arbitrary, A, T](implicit
    da: Decoder[E, A, _, T]
  ): Arbitrary[IllegalValue[E, A, T]] =
    arbIllegalValue(e => da.decode(e).isLeft)

  def arbLegalValue[E, A, T](encode: A => E)(implicit arbA: Arbitrary[A]): Arbitrary[LegalValue[E, A, T]] =
    Arbitrary {
      arbA.arbitrary.map(a => LegalValue(encode(a), a))
    }

  def arbIllegalValue[E: Arbitrary, A, T](illegal: E => Boolean): Arbitrary[IllegalValue[E, A, T]] =
    Arbitrary {
      implicitly[Arbitrary[E]].arbitrary.suchThat(illegal).map(e => IllegalValue(e))
    }

  // - Codecs ----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbDecoder[E: Cogen, D: Arbitrary, F: Arbitrary, T]: Arbitrary[Decoder[E, D, F, T]] =
    Arbitrary(arb[E => Either[F, D]].map(Decoder.from))

  implicit def arbEncoder[E: Arbitrary, D: Cogen, T]: Arbitrary[Encoder[E, D, T]] =
    Arbitrary(arb[D => E].map(Encoder.from))

  // - String codecs ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val arbStringDecodeError: Arbitrary[DecodeError] = Arbitrary(
    Gen.oneOf(
      for {
        id    <- Gen.identifier
        value <- implicitly[Arbitrary[String]].arbitrary
      } yield DecodeError(s"'$value' is not a valid $id"),
      arbException.arbitrary.map(DecodeError.apply)
    )
  )

  // This is necessary to prevent ScalaCheck from generating BigDecimal values that cannot be serialized because their
  // scale is higher than MAX_INT.
  // Note that this isn't actually an issue with ScalaCheck but with Scala itself, and is(?) fixed in Scala 2.12:
  // https://github.com/scala/scala/pull/4320
  implicit lazy val arbBigDecimal: Arbitrary[BigDecimal] = {
    import java.math.MathContext._
    val mcGen = Gen.oneOf(DECIMAL32, DECIMAL64, DECIMAL128)
    val bdGen = for {
      x     <- Arbitrary.arbitrary[BigInt]
      mc    <- mcGen
      limit <- Gen.const(math.max(x.abs.toString.length - mc.getPrecision, 0))
      scale <- Gen.choose(Int.MinValue + limit, Int.MaxValue)
    } yield try
      BigDecimal(x, scale, mc)
    catch {
      // Handle the case where scale/precision conflict
      case _: java.lang.ArithmeticException => BigDecimal(x, scale, UNLIMITED)
    }
    Arbitrary(bdGen)
  }

  // Saner arbitrary date: the default one causes issues with long overflow / underflow.
  implicit val arbDate: Arbitrary[Date] = {
    val now = System.currentTimeMillis()
    Arbitrary(Arbitrary.arbitrary[Int].map(offset => new Date(now + offset)))
  }

  implicit val arbUuid: Arbitrary[UUID] = Arbitrary(Gen.uuid)

  // - Cogen -----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val cogenStringDecodeError: Cogen[DecodeError] = implicitly[Cogen[String]].contramap(_.message)
  implicit val cogenUUID: Cogen[UUID] =
    Cogen.tuple2[Long, Long].contramap(uuid => (uuid.getMostSignificantBits, uuid.getLeastSignificantBits))
  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  implicit val cogenDate: Cogen[Date] = Cogen(_.getTime)

  // - Exceptions ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  val genUnsupportedEncoding: Gen[UnsupportedEncodingException] =
    Gen.identifier.map(i => new UnsupportedEncodingException(s"Unsupported encoding: $i"))

  val genIllegalArgument: Gen[IllegalArgumentException] =
    Gen.identifier.map(a => new IllegalArgumentException(s"Illegal argument: $a"))

  def genIoException: Gen[IOException]

  implicit val arbIoException: Arbitrary[IOException] = Arbitrary(genIoException)

  implicit def genException: Gen[Exception] =
    Gen.oneOf(
      genIoException,
      genIllegalArgument,
      Gen.const(new NullPointerException),
      Gen.const(new IllegalArgumentException)
    )

  implicit def arbException: Arbitrary[Exception] =
    Arbitrary(genException)

  implicit def arbTry[A](implicit aa: Arbitrary[A]): Arbitrary[Try[A]] =
    Arbitrary(
      Gen.oneOf(
        arb[Exception].map(e => scala.util.Failure(e): Try[A]),
        aa.arbitrary.map(a => scala.util.Success(a): Try[A])
      )
    )
}
