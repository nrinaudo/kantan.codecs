package kantan.codecs.laws.discipline

import kantan.codecs.laws.CodecValue.{LegalValue, IllegalValue}
import kantan.codecs.laws.{CodecLaws, CodecValue}
import kantan.codecs.{Decoder, Encoder}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._

trait CodecTests[E, D, F, Dec[DD] <: Decoder[E, DD, F, Dec], Enc[DD] <: Encoder[E, DD, Enc]]
  extends DecoderTests[E, D, F, Dec] with EncoderTests[E, D, Enc] {
  def laws: CodecLaws[E, D, F, Dec, Enc]

  override implicit val arbD: Arbitrary[D] = Arbitrary(arbLegal.arbitrary.map(_.decoded))
  implicit val arbE: Arbitrary[E] = Arbitrary(arbLegal.arbitrary.map(_.encoded))

  def roundTrip: RuleSet = new DefaultRuleSet(
    name = "round trip",
    parent = None,
    "round trip (encoding)" → forAll(laws.roundTripEncoding _),
    "round trip (decoding)" → forAll(laws.roundTripDecoding _)
  )

  def bijectiveCodec[A: Arbitrary, B: Arbitrary]: RuleSet = {
    implicit val arbED: Arbitrary[CodecValue[E, D]] = Arbitrary(arbLegal.arbitrary)
    new RuleSet {
      def name = "bijective codec"
      def bases = Nil
      def parents = Seq(roundTrip, bijectiveDecoder[A, B], bijectiveEncoder[A, B])
      def props = Seq.empty
    }
  }

  def codec[A, B](implicit arbA: Arbitrary[A], arbB: Arbitrary[B], ai: Arbitrary[IllegalValue[E, D]]): RuleSet =
    new RuleSet {
      def name = "codec"
      def bases = Nil
      def parents = Seq(roundTrip, decoder[A, B], encoder[A, B])
      def props = Seq.empty
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
