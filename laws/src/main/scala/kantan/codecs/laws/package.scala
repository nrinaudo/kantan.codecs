package kantan.codecs

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}

package object laws {
  type StringValue[A] = CodecValue[String, A]
  type LegalString[A] = LegalValue[String, A]
  type IllegalString[A] = IllegalValue[String, A]
}
