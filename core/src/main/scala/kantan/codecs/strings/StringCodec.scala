package kantan.codecs.strings

import java.util.UUID

import kantan.codecs.{Codec, Result}

object StringCodec {
  def apply[D](f: String ⇒ Result[Throwable, D])(g: D ⇒ String): StringCodec[D] = Codec(f)(g)
}

trait StringCodecInstances extends StringEncoderInstances with StringDecoderInstances {
  implicit val bigDecimal: StringCodec[BigDecimal] = StringCodec(s ⇒ Result.nonFatal(BigDecimal(s)))(_.toString)
  implicit val bigInt: StringCodec[BigInt] = StringCodec(s ⇒ Result.nonFatal(BigInt(s)))(_.toString)
  implicit val boolean: StringCodec[Boolean] = StringCodec(s ⇒ Result.nonFatal(s.toBoolean))(_.toString)
  implicit val char: StringCodec[Char] = StringCodec { s ⇒
    if(s.length == 1) Result.success(s.charAt(0))
    else              Result.failure(new IllegalArgumentException(s"Not a character: '$s'"))
  }(_.toString)
  implicit val double: StringCodec[Double] = StringCodec(s ⇒ Result.nonFatal(s.toDouble))(_.toString)
  implicit val byte: StringCodec[Byte] = StringCodec(s ⇒ Result.nonFatal(s.toByte))(_.toString)
  implicit val float: StringCodec[Float] = StringCodec(s ⇒ Result.nonFatal(s.toFloat))(_.toString)
  implicit val int: StringCodec[Int] = StringCodec(s ⇒ Result.nonFatal(s.toInt))(_.toString)
  implicit val long: StringCodec[Long] = StringCodec(s ⇒ Result.nonFatal(s.toLong))(_.toString)
  implicit val short: StringCodec[Short] = StringCodec(s ⇒ Result.nonFatal(s.toShort))(_.toString)
  implicit val string: StringCodec[String] = StringCodec(s ⇒ Result.nonFatal(s))(_.toString)
  implicit val uuid: StringCodec[UUID] = StringCodec(s ⇒ Result.nonFatal(UUID.fromString(s)))(_.toString)
}

