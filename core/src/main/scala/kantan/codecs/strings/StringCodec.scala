package kantan.codecs.strings

import java.util.UUID

import kantan.codecs.{Codec, Result}

object StringCodec {
  def apply[D](f: String ⇒ Result[Throwable, D])(g: D ⇒ String): StringCodec[D] = Codec(f)(g)
}

/** Defines default instances of [[StringCodec]] for all primitive types. */
trait StringCodecInstances extends StringEncoderInstances with StringDecoderInstances {
  implicit val bigDecimal: StringCodec[BigDecimal] = StringCodec(s ⇒ Result.nonFatal(BigDecimal(s.trim)))(_.toString)
  implicit val bigInt: StringCodec[BigInt] = StringCodec(s ⇒ Result.nonFatal(BigInt(s.trim)))(_.toString)
  implicit val boolean: StringCodec[Boolean] = StringCodec(s ⇒ Result.nonFatal(s.trim.toBoolean))(_.toString)
  implicit val char: StringCodec[Char] = StringCodec { s ⇒
    // This is a bit dodgy, but necessary: if the string has a length greater than 1, it might be a legal character with
    // padding. The only issue is if the character is *whitespace* with whitespace padding. This is acknowledged and
    // willfully ignored, at least for the time being.
    val t = if(s.length > 1) s.trim else s
    if(t.length == 1) Result.success(t.charAt(0))
    else              Result.failure(new IllegalArgumentException(s"Not a character: '$s'"))
  }(_.toString)
  implicit val double: StringCodec[Double] = StringCodec(s ⇒ Result.nonFatal(s.trim.toDouble))(_.toString)
  implicit val byte: StringCodec[Byte] = StringCodec(s ⇒ Result.nonFatal(s.trim.toByte))(_.toString)
  implicit val float: StringCodec[Float] = StringCodec(s ⇒ Result.nonFatal(s.trim.toFloat))(_.toString)
  implicit val int: StringCodec[Int] = StringCodec(s ⇒ Result.nonFatal(s.trim.toInt))(_.toString)
  implicit val long: StringCodec[Long] = StringCodec(s ⇒ Result.nonFatal(s.trim.toLong))(_.toString)
  implicit val short: StringCodec[Short] = StringCodec(s ⇒ Result.nonFatal(s.trim.toShort))(_.toString)
  implicit val string: StringCodec[String] = StringCodec(s ⇒ Result.nonFatal(s))(_.toString)
  implicit val uuid: StringCodec[UUID] = StringCodec(s ⇒ Result.nonFatal(UUID.fromString(s.trim)))(_.toString)
}

