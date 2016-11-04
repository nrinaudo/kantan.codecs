// - Dependency versions -----------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
val catsVersion                = "0.8.0"
val disciplineVersion          = "0.7.2"
val impVersion                 = "0.2.0"
val jodaConvertVersion         = "1.8.1"
val jodaVersion                = "2.9.5"
val scalacheckVersion          = "1.13.4"
val scalacheckShapelessVersion = "1.1.3"
val scalatestVersion           = "3.0.1-SNAP1"
val scalazVersion              = "7.3.0-M6"
val shapelessVersion           = "2.3.2"


kantanProject in ThisBuild := "codecs"


// - root projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val root = Project(id = "kantan-codecs", base = file("."))
  .settings(moduleName := "root")
  .enablePlugins(UnpublishedPlugin)
  .aggregate(core, laws, catsLaws, scalazLaws, shapelessLaws, cats, scalaz, shapeless, jodaTime, jodaTimeLaws, docs,
    tests)
  .dependsOn(core)

lazy val tests = project
  .enablePlugins(UnpublishedPlugin)
  .dependsOn(core, laws, catsLaws, scalazLaws, jodaTimeLaws, shapelessLaws)
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion % "test")

lazy val docs = project
  .enablePlugins(DocumentationPlugin)
  .dependsOn(core)



// - core projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val core = project
  .settings(
    moduleName := "kantan.codecs",
    name       := "core"
  )
  .settings(libraryDependencies += "org.spire-math" %% "imp" % impVersion)
  .enablePlugins(PublishedPlugin)

lazy val laws = project
  .settings(
    moduleName := "kantan.codecs-laws",
    name       := "laws"
  )
  .enablePlugins(BoilerplatePlugin)
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies ++= Seq(
    "org.scalacheck" %% "scalacheck" % scalacheckVersion,
    "org.typelevel"  %% "discipline" % disciplineVersion
  ))


// - cats projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val cats = project
  .settings(
    moduleName := "kantan.codecs-cats",
    name       := "cats"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "org.typelevel" %% "cats" % catsVersion)

lazy val catsLaws = Project(id = "cats-laws", base = file("cats-laws"))
  .settings(
    moduleName := "kantan.codecs-cats-laws",
    name       := "cats-laws"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws, cats)
  .settings(libraryDependencies ++= Seq(
    "org.typelevel" %% "cats"      % catsVersion,
    "org.typelevel" %% "cats-laws" % catsVersion
  ))



// - joda-time projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val jodaTime = Project(id = "joda-time", base = file("joda-time"))
  .settings(
    moduleName := "kantan.codecs-joda-time",
    name       := "joda-time"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies ++= Seq(
    "joda-time" % "joda-time"    % jodaVersion,
    "org.joda"  % "joda-convert" % jodaConvertVersion
  ))

lazy val jodaTimeLaws = Project(id = "joda-time-laws", base = file("joda-time-laws"))
  .settings(
    moduleName := "kantan.codecs-joda-time-laws",
    name       := "joda-time-laws"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws, jodaTime)



// - scalaz projects ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalaz = project
  .settings(
    moduleName := "kantan.codecs-scalaz",
    name       := "scalaz"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "org.scalaz" %% "scalaz-core" % scalazVersion)

lazy val scalazLaws = Project(id = "scalaz-laws", base = file("scalaz-laws"))
  .settings(
    moduleName := "kantan.codecs-scalaz-laws",
    name       := "scalaz-laws"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws, scalaz)
  .settings(libraryDependencies ++= Seq(
    "org.scalaz"    %% "scalaz-core"               % scalazVersion,
    "org.scalaz"    %% "scalaz-scalacheck-binding" % scalazVersion,
    "org.scalatest" %% "scalatest"                 % scalatestVersion % "optional"
  ))



// - shapeless projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val shapeless = project
  .settings(
    moduleName := "kantan.codecs-shapeless",
    name       := "shapeless"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.chuusai" %% "shapeless" % shapelessVersion)

lazy val shapelessLaws = Project(id = "shapeless-laws", base = file("shapeless-laws"))
  .settings(
    moduleName := "kantan.codecs-shapeless-laws",
    name       := "shapeless-laws"
  )
  .settings(
    libraryDependencies += "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % scalacheckShapelessVersion
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws, shapeless)
