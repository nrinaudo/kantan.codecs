package kantan.codecs.laws.discipline

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.laws.{DecoderLaws, EncoderLaws}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

trait EncoderTests[E, D] extends Laws {
  def laws: EncoderLaws[D, E]

  implicit def arbLegal: Arbitrary[LegalValue[E, D]]
  implicit def arbIllegal: Arbitrary[IllegalValue[E]]
  implicit val arbD: Arbitrary[D] = Arbitrary(arbLegal.arbitrary.map(_.decoded))

  def encoder[A: Arbitrary, B: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "encoder",
    parent = None,
    "encode"                → forAll(laws.encode _),
    "contramap identity"    → forAll(laws.contramapIdentity _),
    "contramap composition" → forAll(laws.contramapComposition[A, B] _)
    )
}

object EncoderTests {
  def apply[E, D](implicit l: EncoderLaws[D, E],
                  al: Arbitrary[LegalValue[E, D]],
                  ai: Arbitrary[IllegalValue[E]]): EncoderTests[E, D] = new EncoderTests[E, D] {
    override val laws = l
    override val arbLegal = al
    override val arbIllegal = ai
  }
}