package kantan.codecs.laws

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class CodecValueTests extends FunSuite with GeneratorDrivenPropertyChecks {
  test("mapDecoded should ignore illegal values and modify the encoded part of legal ones.") {
    forAll { (v: StringValue[Int], f: Int ⇒ Float) ⇒
      assert(v.mapDecoded(f) == (v match {
        case LegalValue(s, i) ⇒ LegalValue(s, f(i))
        case IllegalValue(s)  ⇒ IllegalValue(s)
      }))
    }
  }

  test("mapEncoded should modify the encoded part of all values.") {
    forAll { (v: StringValue[Int], f: String ⇒ Long) ⇒
      assert(v.mapEncoded(f) == (v match {
        case LegalValue(s, i) ⇒ LegalValue(f(s), i)
        case IllegalValue(s)  ⇒ IllegalValue(f(s))
      }))
    }
  }
}
