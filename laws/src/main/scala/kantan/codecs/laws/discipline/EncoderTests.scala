package kantan.codecs.laws.discipline

import kantan.codecs.Encoder
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.laws.{CodecValue, EncoderLaws}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

trait EncoderTests[E, D, R[DD] <: Encoder[E, DD, R]] extends Laws {
  def laws: EncoderLaws[E, D, R]

  implicit def arbLegal: Arbitrary[LegalValue[E, D]]
  implicit val arbD: Arbitrary[D] = Arbitrary(arbLegal.arbitrary.map(_.decoded))

  private def rules[A: Arbitrary, B: Arbitrary](name: String)(implicit arbED: Arbitrary[CodecValue[E, D]]): RuleSet = new DefaultRuleSet(
    name = name,
    parent = None,
    "encode"                → forAll(laws.encode _),
    "contramap identity"    → forAll(laws.contramapIdentity _),
    "contramap composition" → forAll(laws.contramapComposition[A, B] _)
  )

  def bijectiveEncoder[A, B](implicit aa: Arbitrary[A], ab: Arbitrary[B]): RuleSet =
    rules("bijective encoder")(aa, ab, Arbitrary(arbLegal.arbitrary))

  def encoder[A: Arbitrary, B: Arbitrary](implicit ai: Arbitrary[IllegalValue[E, D]]): RuleSet = rules[A, B]("encoder")
}

object EncoderTests {
  def apply[E, D, R[DD] <: Encoder[E, DD, R]](implicit l: EncoderLaws[E, D, R],
                  al: Arbitrary[LegalValue[E, D]]): EncoderTests[E, D, R] = new EncoderTests[E, D, R] {
    override val laws = l
    override val arbLegal = al
  }
}