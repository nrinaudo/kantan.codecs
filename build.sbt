import sbtunidoc.Plugin.UnidocKeys._

kantanProject in ThisBuild := "codecs"


// - root projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val root = Project(id = "kantan-codecs", base = file("."))
  .settings(moduleName := "root")
  .enablePlugins(UnpublishedPlugin)
  .aggregate(core, laws, catsLaws, scalazLaws, shapelessLaws, cats, scalaz, shapeless, jodaTime, jodaTimeLaws, docs)
  .aggregateIf(java8Supported)(java8, java8Laws)
  .dependsOn(core)

lazy val docs = project
  .enablePlugins(DocumentationPlugin)
  .settings(unidocProjectFilter in (ScalaUnidoc, unidoc) :=
    inAnyProject -- inProjectsIf(java8Supported)(java8, java8Laws)
  )
  .dependsOn(core, laws, catsLaws, scalazLaws, shapelessLaws, cats, scalaz, shapeless, jodaTime, jodaTimeLaws)
  .dependsOnIf(java8Supported)(java8, java8Laws)



// - core projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val core = project
  .settings(
    moduleName := "kantan.codecs",
    name       := "core"
  )
  .settings(libraryDependencies ++= Seq(
    "org.spire-math" %% "imp"       % Versions.imp,
    "org.scalatest"  %% "scalatest" % Versions.scalatest % "test",
    "commons-io"     % "commons-io" % Versions.commonsIo % "test"
  ))
  .laws("laws")
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
    "org.scalacheck" %% "scalacheck" % Versions.scalacheck,
    "org.typelevel"  %% "discipline" % Versions.discipline,
    "org.scalatest"  %% "scalatest"  % Versions.scalatest % "test"
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
  .settings(libraryDependencies ++= Seq(
    "org.typelevel" %% "cats"      % Versions.cats,
    "org.scalatest" %% "scalatest" % Versions.scalatest % "test"
  ))
  .laws("cats-laws")

lazy val catsLaws = Project(id = "cats-laws", base = file("cats-laws"))
  .settings(
    moduleName := "kantan.codecs-cats-laws",
    name       := "cats-laws"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws, cats)
  .settings(libraryDependencies ++= Seq(
    "org.typelevel" %% "cats"      % Versions.cats,
    "org.typelevel" %% "cats-laws" % Versions.cats
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
    "joda-time"      % "joda-time"    % Versions.joda,
    "org.joda"       % "joda-convert" % Versions.jodaConvert,
    "org.scalatest" %% "scalatest"    % Versions.scalatest % "test"
  ))
  .laws("joda-time-laws")

lazy val jodaTimeLaws = Project(id = "joda-time-laws", base = file("joda-time-laws"))
  .settings(
    moduleName := "kantan.codecs-joda-time-laws",
    name       := "joda-time-laws"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws, jodaTime)



// - java8 projects ----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val java8 = project
  .settings(
    moduleName    := "kantan.codecs-java8",
    name          := "java8"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % Versions.scalatest % "test")
  .laws("java8-laws")

lazy val java8Laws = Project(id = "java8-laws", base = file("java8-laws"))
  .settings(
    moduleName    := "kantan.codecs-java8-laws",
    name          := "java8-laws"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws, java8)




// - scalaz projects ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalaz = project
  .settings(
    moduleName := "kantan.codecs-scalaz",
    name       := "scalaz"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies ++= Seq(
    "org.scalaz"    %% "scalaz-core" % Versions.scalaz,
    "org.scalatest" %% "scalatest"   % Versions.scalatest % "test"
  ))
  .laws("scalaz-laws")

lazy val scalazLaws = Project(id = "scalaz-laws", base = file("scalaz-laws"))
  .settings(
    moduleName := "kantan.codecs-scalaz-laws",
    name       := "scalaz-laws"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws, scalaz)
  .settings(libraryDependencies ++= Seq(
    "org.scalaz"    %% "scalaz-core"               % Versions.scalaz,
    "org.scalaz"    %% "scalaz-scalacheck-binding" % (Versions.scalaz + "-scalacheck-1.13"),
    "org.scalatest" %% "scalatest"                 % Versions.scalatest % "optional"
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
  .settings(libraryDependencies ++= Seq(
    "com.chuusai"   %% "shapeless" % Versions.shapeless,
    "org.scalatest" %% "scalatest" % Versions.scalatest % "test"
  ))
  .laws("shapeless-laws")

lazy val shapelessLaws = Project(id = "shapeless-laws", base = file("shapeless-laws"))
  .settings(
    moduleName := "kantan.codecs-shapeless-laws",
    name       := "shapeless-laws"
  )
  .settings(
    libraryDependencies += "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % Versions.scalacheckShapeless
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws, shapeless)
