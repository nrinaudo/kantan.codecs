package kantan.codecs.laws.discipline

import kantan.codecs.{Decoder, Encoder}
import kantan.codecs.laws.DecoderLaws
import org.scalacheck.Arbitrary
import arbitrary._
import org.typelevel.discipline.Laws
import org.scalacheck.Prop._

trait DecoderTests[E, D, F] extends Laws {
  def laws: DecoderLaws[E, D, F]

  implicit def arbD: Arbitrary[D]
  implicit def arbF: Arbitrary[F]

  def decoder[A: Arbitrary, B: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "decoder",
    parent = None,
    "decode"                → forAll(laws.decode _),
    "map identity"          → forAll(laws.mapIdentity _),
    "mapResult identity"    → forAll(laws.mapResultIdentity _),
    "map composition"       → forAll(laws.mapComposition[A, B] _),
    "mapResult composition" → forAll(laws.mapResultComposition[A, B] _)
  )
}

object DecoderTests {
  def apply[E, D: Arbitrary, F: Arbitrary](l: DecoderLaws[E, D, F]): DecoderTests[E, D, F] = new DecoderTests[E, D, F] {
    override val laws = l
    override val arbD = implicitly[Arbitrary[D]]
    override val arbF = implicitly[Arbitrary[F]]
  }

  implicit def fromCodec[E, D: Arbitrary, F: Arbitrary](implicit de: Decoder[E, D, F], ee: Encoder[D, E]): DecoderTests[E, D, F] =
    DecoderTests(DecoderLaws.fromCodec)
}