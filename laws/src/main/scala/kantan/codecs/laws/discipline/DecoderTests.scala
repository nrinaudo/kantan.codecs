package kantan.codecs.laws.discipline

import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.laws.{CodecValue, DecoderLaws}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import arbitrary._

trait DecoderTests[E, D, F] extends Laws {
  def laws: DecoderLaws[E, D, F]


  implicit def arbLegal: Arbitrary[LegalValue[E, D]]
  implicit def arbIlllegal: Arbitrary[IllegalValue[E]]

  implicit def arbF: Arbitrary[F]
  implicit val arbD: Arbitrary[D] = Arbitrary(arbLegal.arbitrary.map(_.decoded))


  def decoder[A: Arbitrary, B: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "decoder",
    parent = None,
    "decode"                → forAll(laws.otherDecode _),
    "map identity"          → forAll(laws.mapIdentity _),
    "mapResult identity"    → forAll(laws.mapResultIdentity _),
    "map composition"       → forAll(laws.mapComposition[A, B] _),
    "mapResult composition" → forAll(laws.mapResultComposition[A, B] _)
  )
}

object DecoderTests {
  def apply[E, D, F: Arbitrary](implicit l: DecoderLaws[E, D, F],
                                al: Arbitrary[LegalValue[E, D]],
                                ai: Arbitrary[IllegalValue[E]]): DecoderTests[E, D, F] = new DecoderTests[E, D, F] {
    override val laws = l
    override val arbLegal = al
    override val arbIlllegal = ai
    override val arbF = implicitly[Arbitrary[F]]
  }
}