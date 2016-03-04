package kantan.codecs.laws.discipline

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.laws.discipline.arbitrary._
import kantan.codecs.laws.{CodecValue, DecoderLaws}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

trait DecoderTests[E, D, F, T] extends Laws {
  def laws: DecoderLaws[E, D, F, T]

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

  def bijectiveDecoder[A: Arbitrary, B: Arbitrary]: RuleSet = {
    implicit val arbValues: Arbitrary[CodecValue[E, D]] = Arbitrary(arbLegal.arbitrary)
    new DefaultRuleSet(
      "bijective decoder",
      Some(coreRules[A, B])
    )
  }

  def decoder[A, B](implicit arbA: Arbitrary[A], arbB: Arbitrary[B], ai: Arbitrary[IllegalValue[E, D]]): RuleSet =
    new DefaultRuleSet(
      "decoder",
      Some(coreRules[A, B]),
      "decode failure" → forAll(laws.decodeFailure _)
    )
}

object DecoderTests {
  def apply[E, D, F: Arbitrary, T](implicit l: DecoderLaws[E, D, F, T],al: Arbitrary[LegalValue[E, D]]): DecoderTests[E, D, F, T] =
    new DecoderTests[E, D, F, T] {
      override val laws = l
      override val arbLegal = al
      override val arbF = implicitly[Arbitrary[F]]
    }
}