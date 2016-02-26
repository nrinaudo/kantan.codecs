package kantan.codecs.laws.discipline

import org.scalacheck.Arbitrary

object equality {
  def eq[A, B: Arbitrary](a1: B ⇒ A, a2: B ⇒ A)(f: (A, A) ⇒ Boolean): Boolean = {
    val samples = List.fill(100)(Arbitrary.arbitrary[B].sample).collect {
      case Some(a) ⇒ a
      case None ⇒ sys.error("Could not generate arbitrary values to compare two functions")
    }
    samples.forall(b ⇒ f(a1(b), a2(b)))
  }

  def simpleEncoderEquals[A: Arbitrary](c1: SimpleEncoder[A], c2: SimpleEncoder[A]): Boolean =
    eq(c1.encode, c2.encode)(_ == _)

  def simpleDecoderEquals[A: Arbitrary](c1: SimpleDecoder[A], c2: SimpleDecoder[A]): Boolean =
    eq(c1.decode, c2.decode)(_ == _)
}
