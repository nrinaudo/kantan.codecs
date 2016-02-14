import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.SbtSite.SiteKeys._
import UnidocKeys._

val catsVersion          = "0.4.1"
val macroParadiseVersion = "2.1.0"
val scalatestVersion     = "3.0.0-M9"
val scalaCheckVersion    = "1.12.5"
val disciplineVersion    = "0.4"

lazy val buildSettings = Seq(
  organization       := "com.nrinaudo",
  scalaVersion       := "2.11.7",
  crossScalaVersions := Seq("2.10.6", "2.11.7")
)

lazy val compilerOptions = Seq("-deprecation",
  "-target:jvm-1.7",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture")

lazy val baseSettings = Seq(
  scalacOptions ++= compilerOptions,
  libraryDependencies ++= Seq(
    compilerPlugin("org.scalamacros" % "paradise" % macroParadiseVersion cross CrossVersion.full),
    compilerPlugin("org.spire-math" % "kind-projector" % "0.7.1" cross CrossVersion.binary)
  ),
  incOptions     := incOptions.value.withNameHashing(true)
)

lazy val noPublishSettings = Seq(
  publish         := (),
  publishLocal    := (),
  publishArtifact := false
)

lazy val publishSettings = Seq(
  homepage := Some(url("https://nrinaudo.github.io/kantan.codecs")),
  licenses := Seq("MIT License" → url("http://www.opensource.org/licenses/mit-license.php")),
  apiURL := Some(url("https://nrinaudo.github.io/kantan.codecs/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/nrinaudo/kantan.codecs"),
      "scm:git:git@github.com:nrinaudo/kantan.codecs.git"
    )
  ),
  pomExtra := <developers>
    <developer>
      <id>nrinaudo</id>
      <name>Nicolas Rinaudo</name>
      <url>http://nrinaudo.github.io</url>
    </developer>
  </developers>
)


lazy val allSettings = buildSettings ++ baseSettings ++ publishSettings

lazy val root = Project(id = "kantan-codecs", base = file("."))
  .settings(moduleName := "root")
  .settings(allSettings)
  .settings(noPublishSettings)
  .aggregate(core, laws, cats, tests)

lazy val core = project
  .settings(
    moduleName := "kantan.codecs",
    name       := "core"
  )
  .settings(allSettings: _*)

lazy val laws = project
  .settings(
    moduleName := "kantan.codecs-laws",
    name       := "laws"
  )
  .settings(libraryDependencies ++= Seq(
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion,
    "org.typelevel"  %% "discipline" % disciplineVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  ))
  .settings(allSettings: _*)
  .dependsOn(core)

lazy val cats = project
  .settings(
    moduleName := "kantan.codecs-cats",
    name       := "cats"
  )
  .settings(libraryDependencies ++= Seq(
    "org.typelevel" %% "cats"      % catsVersion,
    "org.typelevel" %% "cats-laws" % catsVersion      % "test",
    "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  ))
  .settings(allSettings: _*)
  .dependsOn(core, laws % "test")

lazy val tests = project
  .settings(allSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion % "test")
  .dependsOn(core, laws % "test")
