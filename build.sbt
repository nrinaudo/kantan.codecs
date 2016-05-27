import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.SbtSite.SiteKeys._
import UnidocKeys._
import de.heikoseeberger.sbtheader.license.Apache2_0

val catsVersion          = "0.5.0"
val disciplineVersion    = "0.4"
val kindProjectorVersion = "0.7.1"
val scalaCheckVersion    = "1.12.5"
val scalatestVersion     = "3.0.0-M9"
val scalazVersion        = "7.2.2"
val jodaVersion          = "2.9.3"
val jodaConvertVersion   = "1.8.1"
val shapelessVersion     = "2.3.1"

lazy val buildSettings = Seq(
  organization       := "com.nrinaudo",
  scalaVersion       := "2.11.8",
  crossScalaVersions := Seq("2.10.6", "2.11.8"),
  autoAPIMappings    := true
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
  scalacOptions ++= compilerOptions ++ (
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 11)) => Seq("-Ywarn-unused-import")
      case _ => Nil
    }
  ),
  scalacOptions in (Compile, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import"))
  },
  headers := Map("scala" -> Apache2_0("2016", "Nicolas Rinaudo")),
  libraryDependencies += compilerPlugin("org.spire-math" % "kind-projector" % kindProjectorVersion cross CrossVersion.binary),
  ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "kantan\\.codecs.*\\.laws\\..*",
  incOptions  := incOptions.value.withNameHashing(true)
)

lazy val noPublishSettings = Seq(
  publish         := (),
  publishLocal    := (),
  publishArtifact := false
)

lazy val publishSettings = Seq(
  homepage := Some(url("https://nrinaudo.github.io/kantan.codecs")),
  licenses := Seq("Apache-2.0" → url("https://www.apache.org/licenses/LICENSE-2.0.html")),
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
  .aggregate(core, laws, catsLaws, scalazLaws, shapelessLaws, cats, scalaz, shapeless, jodaTime, jodaTimeLaws, docs, tests)
  .dependsOn(core)
  .enablePlugins(AutomateHeaderPlugin)

lazy val core = project
  .settings(
    moduleName := "kantan.codecs",
    name       := "core"
  )
  .settings(allSettings: _*)
  .enablePlugins(AutomateHeaderPlugin)

lazy val laws = project
  .settings(
    moduleName := "kantan.codecs-laws",
    name       := "laws"
  )
  .settings(libraryDependencies ++= Seq(
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion,
    "org.typelevel"  %% "discipline" % disciplineVersion
  ))
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .settings(allSettings: _*)
  .dependsOn(core)
  .enablePlugins(AutomateHeaderPlugin)

lazy val cats = project
  .settings(
    moduleName := "kantan.codecs-cats",
    name       := "cats"
  )
  .settings(libraryDependencies += "org.typelevel" %% "cats" % catsVersion)
  .settings(allSettings: _*)
  .dependsOn(core)
  .enablePlugins(AutomateHeaderPlugin)

lazy val jodaTime = Project(id = "joda-time", base = file("joda-time"))
  .settings(
    moduleName := "kantan.codecs-joda-time",
    name       := "joda-time"
  )
  .settings(libraryDependencies ++= Seq(
    "joda-time" % "joda-time"    % jodaVersion,
    "org.joda"  % "joda-convert" % jodaConvertVersion
  ))
  .settings(allSettings: _*)
  .dependsOn(core)
  .enablePlugins(AutomateHeaderPlugin)

lazy val jodaTimeLaws = Project(id = "joda-time-laws", base = file("joda-time-laws"))
  .settings(
    moduleName := "kantan.codecs-joda-time-laws",
    name       := "joda-time-laws"
  )
  .settings(allSettings: _*)
  .dependsOn(core, laws, jodaTime)
  .enablePlugins(AutomateHeaderPlugin)

lazy val scalaz = project
  .settings(
    moduleName := "kantan.codecs-scalaz",
    name       := "scalaz"
  )
  .settings(allSettings: _*)
  .settings(libraryDependencies += "org.scalaz" %% "scalaz-core" % scalazVersion)
  .dependsOn(core, laws % "test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val shapeless = project
  .settings(
    moduleName := "kantan.codecs-shapeless",
    name       := "shapeless"
  )
  .settings(allSettings: _*)
  .settings(libraryDependencies += "com.chuusai" %% "shapeless" % shapelessVersion)
  .dependsOn(core)
  .enablePlugins(AutomateHeaderPlugin)


lazy val catsLaws = Project(id = "cats-laws", base = file("cats-laws"))
  .settings(
    moduleName := "kantan.codecs-cats-laws",
    name       := "cats-laws"
  )
  .settings(libraryDependencies ++= Seq(
    "org.typelevel" %% "cats"      % catsVersion,
    "org.typelevel" %% "cats-laws" % catsVersion
  ))
  .settings(allSettings: _*)
  .dependsOn(core, laws, cats)
  .enablePlugins(AutomateHeaderPlugin)

lazy val scalazLaws = Project(id = "scalaz-laws", base = file("scalaz-laws"))
  .settings(
    moduleName := "kantan.codecs-scalaz-laws",
    name       := "scalaz-laws"
  )
  .settings(libraryDependencies ++= Seq(
    "org.scalaz"    %% "scalaz-core"               % scalazVersion,
    "org.scalaz"    %% "scalaz-scalacheck-binding" % scalazVersion
  ))
  .settings(allSettings: _*)
  .dependsOn(core, laws, scalaz)
  .enablePlugins(AutomateHeaderPlugin)

lazy val shapelessLaws = Project(id = "shapeless-laws", base = file("shapeless-laws"))
  .settings(
    moduleName := "kantan.codecs-shapeless-laws",
    name       := "shapeless-laws"
  )
  .settings(allSettings: _*)
  .dependsOn(core, laws, shapeless)
  .enablePlugins(AutomateHeaderPlugin)

lazy val tests = project
  .settings(allSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion % "test")
  .dependsOn(core, laws % "test", catsLaws % "test", scalazLaws % "test", jodaTimeLaws % "test")
  .enablePlugins(AutomateHeaderPlugin)

lazy val docs = project
  .settings(allSettings: _*)
  .settings(site.settings: _*)
  .settings(ghpages.settings: _*)
  .settings(unidocSettings: _*)
  .settings(site.preprocessSite())
  .settings(
    apiURL := Some(url("http://nrinaudo.github.io/kantan.codecs/api/")),
    scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
      "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
      "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
    )
  )
  .settings(tutSettings: _*)
  .settings(tutScalacOptions ~= (_.filterNot(Set("-Ywarn-unused-import"))))
  .settings(
    site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "api"),
    site.addMappingsToSiteDir(tut, "_tut"),
    git.remoteRepo := "git@github.com:nrinaudo/kantan.codecs.git",
    ghpagesNoJekyll := false,
    includeFilter in makeSite := "*.yml" | "*.md" | "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" |
                                 "*.eot" | "*.svg" | "*.ttf" | "*.woff" | "*.woff2" | "*.otf"
  )
  .settings(noPublishSettings:_*)
  .dependsOn(core)

addCommandAlias("validate", "; clean; scalastyle; test:scalastyle; coverage; test; coverageReport; coverageAggregate; docs/makeSite")
