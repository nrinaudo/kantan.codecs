package kantan.codecs.laws

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}

import scala.util.Try

sealed abstract class CodecValue[E, +D] extends Product with Serializable {
  def encoded: E
}

object CodecValue {
  final case class LegalValue[E, D](encoded: E, decoded: D) extends CodecValue[E, D]
  final case class IllegalValue[E](encoded: E) extends CodecValue[E, Nothing]


  // - Generic arbitrary / gen -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def genLegal[E, D: Arbitrary](f: D ⇒ E): Gen[LegalValue[E, D]] = arbitrary[D].map(d ⇒ LegalValue(f(d), d))
  def genIllegal[E: Arbitrary, D](f: E ⇒ D): Gen[IllegalValue[E]] =
    arbitrary[E].suchThat(e ⇒ Try(f(e)).isFailure).map(IllegalValue.apply)

  def arbLegal[E, D: Arbitrary](f: D ⇒ E): Arbitrary[LegalValue[E, D]] = Arbitrary(genLegal(f))
  def arbIllegal[E: Arbitrary, D](f: E ⇒ D): Arbitrary[IllegalValue[E]] = Arbitrary(genIllegal(f))

  implicit def arbValue[E, D](implicit arbL: Arbitrary[LegalValue[E, D]], arbI: Arbitrary[IllegalValue[E]]): Arbitrary[CodecValue[E, D]] =
    Arbitrary {
      for {
        legal ← Arbitrary.arbitrary[Boolean]
        value ← if(legal) arbL.arbitrary else arbI.arbitrary
      } yield value
    }
}
