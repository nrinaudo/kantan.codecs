package kantan.codecs.laws.discipline

import kantan.codecs.laws.CodecLaws
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.{Decoder, Encoder}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._

trait CodecTests[E, D, F, Dec[DD] <: Decoder[E, DD, F, Dec], Enc[DD] <: Encoder[E, DD, Enc]]
  extends DecoderTests[E, D, F, Dec] with EncoderTests[E, D, Enc] {
  def laws: CodecLaws[E, D, F, Dec, Enc]

  override implicit val arbD: Arbitrary[D] = Arbitrary(arbLegal.arbitrary.map(_.decoded))
  implicit val arbE: Arbitrary[E] = Arbitrary(arbLegal.arbitrary.map(_.encoded))

  private def coreRules[A: Arbitrary, B: Arbitrary]: RuleSet = new DefaultRuleSet(
    "round trip",
    Some(encoder[A, B]),
    "round trip (encoding)" → forAll(laws.roundTripEncoding _),
    "round trip (decoding)" → forAll(laws.roundTripDecoding _)
  )

  def bijectiveCodec[A: Arbitrary, B: Arbitrary]: RuleSet = new RuleSet {
    val name = "bijective codec"
    val bases = Nil
    val parents = Seq(coreRules[A, B], bijectiveDecoder[A, B])
    val props = Seq.empty
  }

  def codec[A, B](implicit arbA: Arbitrary[A], arbB: Arbitrary[B], ai: Arbitrary[IllegalValue[E, D]]): RuleSet =
    new RuleSet {
      val name = "codec"
      val bases = Nil
      val parents = Seq(coreRules[A, B], decoder[A, B])
      val props = Seq.empty
    }
}

object CodecTests {
  def apply[E, D, F, Dec[DD] <: Decoder[E, DD, F, Dec], Enc[DD] <: Encoder[E, DD, Enc]]
  (implicit l: CodecLaws[E, D, F, Dec, Enc], af: Arbitrary[F], al: Arbitrary[LegalValue[E, D]])
  : CodecTests[E, D, F, Dec, Enc] = new CodecTests[E, D, F, Dec, Enc] {
    override def laws = l
    override implicit def arbLegal = al
    override implicit def arbF = af
  }
}
