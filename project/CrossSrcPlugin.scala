import sbt._
import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport._

object CrossSrcPlugin extends AutoPlugin {
  override def trigger =
    allRequirements

  override def projectSettings: Seq[Def.Setting[?]] =
    Def.settings(
      Seq(Compile, Test).map { c =>
        c / unmanagedSourceDirectories ++= {
          scalaBinaryVersion.value match {
            case "2.13" | "3" =>
              Seq(
                (
                  crossProjectCrossType.?.value,
                  crossProjectBaseDirectory.?.value,
                  crossProjectPlatform.?.value
                ).zipped.map { (crossType, base, platform) =>
                  crossType.platformDir(base, platform) / "src" / Defaults.nameForSrc(c.name) / "scala-2.13+"
                }.toSeq, {
                  val base = crossProjectBaseDirectory.?.value.fold(baseDirectory.value)(_ / "shared")
                  Seq(
                    base / "src" / Defaults.nameForSrc(c.name) / "scala-2.13+"
                  )
                }
              ).flatten
            case _ =>
              Nil
          }
        }
      }
    )
}
