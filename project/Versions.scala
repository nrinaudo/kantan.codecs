object Versions {
  private val isScalaJS06  = Option(System.getenv("SCALAJS_VERSION")).filter(_.nonEmpty).exists(_.startsWith("0.6"))
  val cats                 = "2.1.1"
  val collectionCompat     = "2.1.6"
  val commonsIo            = "2.6"
  val disciplineScalatest  = "1.0.1"
  val enumeratum           = if (isScalaJS06) "1.6.0" else "1.6.1"
  val enumeratumScalacheck = if (isScalaJS06) "1.6.0" else "1.6.1"
  val imp                  = if (isScalaJS06) "0.4.1" else "0.5.0"
  val libra                = "0.7.0"
  val refined              = "0.9.14"
  val scalacheck           = "1.14.3"
  val scalacheckShapeless  = "1.2.5"
  val scalatest            = "3.1.1"
  val scalaz               = "7.2.30"
  val shapeless            = "2.3.3"
}
