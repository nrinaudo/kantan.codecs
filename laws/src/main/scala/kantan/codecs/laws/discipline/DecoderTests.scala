package kantan.codecs.laws.discipline

import kantan.codecs.Decoder
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.laws.{CodecValue, DecoderLaws}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

trait DecoderTests[E, D, F, R[DD] <: Decoder[E, DD, F, R]] extends Laws {
  def laws: DecoderLaws[E, D, F, R]

  implicit def arbLegal: Arbitrary[LegalValue[E, D]]

  implicit def arbF: Arbitrary[F]
  implicit val arbD: Arbitrary[D] = Arbitrary(arbLegal.arbitrary.map(_.decoded))

  private def coreRules[A: Arbitrary, B: Arbitrary](implicit arbED: Arbitrary[CodecValue[E, D]]) =
    new SimpleRuleSet("core",
      "decode" → forAll(laws.decode _),
      "map identity" → forAll(laws.mapIdentity _),
      "mapResult identity" → forAll(laws.mapResultIdentity _),
      "map composition" → forAll(laws.mapComposition[A, B] _),
      "mapResult composition" → forAll(laws.mapResultComposition[A, B] _)
    )

  def bijectiveDecoder[A, B](implicit arbA: Arbitrary[A], arbB: Arbitrary[B]): RuleSet = new DefaultRuleSet(
    "bijective decoder",
    Some(coreRules(arbA, arbB, Arbitrary(arbLegal.arbitrary)))
  )


  def decoder[A, B](implicit arbA: Arbitrary[A], arbB: Arbitrary[B], ai: Arbitrary[IllegalValue[E, D]]): RuleSet =
    new DefaultRuleSet(
      "decoder",
      Some(coreRules(arbA, arbB, CodecValue.arbValue(arbLegal, ai))),
      "decode failure" → forAll(laws.decodeFailure _)
    )


}

object DecoderTests {
  def apply[E, D, F: Arbitrary, R[DD] <: Decoder[E, DD, F, R]](implicit l: DecoderLaws[E, D, F, R],
                                                               al: Arbitrary[LegalValue[E, D]]): DecoderTests[E, D, F, R] = new DecoderTests[E, D, F, R] {
    override val laws = l
    override val arbLegal = al
    override val arbF = implicitly[Arbitrary[F]]
  }
}