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

  private def rules[A: Arbitrary, B: Arbitrary](name: String)(implicit arbED: Arbitrary[CodecValue[E, D]]): RuleSet = new DefaultRuleSet(
    name = name,
    parent = None,
    "decode" → forAll(laws.decode _),
    "map identity" → forAll(laws.mapIdentity _),
    "mapResult identity" → forAll(laws.mapResultIdentity _),
    "map composition" → forAll(laws.mapComposition[A, B] _),
    "mapResult composition" → forAll(laws.mapResultComposition[A, B] _)
  )

  def bijectiveDecoder[A: Arbitrary, B: Arbitrary]: RuleSet =
    rules("bijective decoder")(implicitly[Arbitrary[A]], implicitly[Arbitrary[B]], Arbitrary(arbLegal.arbitrary))

  def decoder[A: Arbitrary, B: Arbitrary](implicit ai: Arbitrary[IllegalValue[E, D]]): RuleSet = rules[A, B]("decoder")

}

object DecoderTests {
  def apply[E, D, F: Arbitrary, R[DD] <: Decoder[E, DD, F, R]](implicit l: DecoderLaws[E, D, F, R],
                                                                         al: Arbitrary[LegalValue[E, D]]): DecoderTests[E, D, F, R] = new DecoderTests[E, D, F, R] {
    override val laws = l
    override val arbLegal = al
    override val arbF = implicitly[Arbitrary[F]]
  }
}