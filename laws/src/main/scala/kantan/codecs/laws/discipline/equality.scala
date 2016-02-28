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
}
