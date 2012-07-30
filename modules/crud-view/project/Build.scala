import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "crud-view"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "commons" % "commons_2.9.1" % "1.0-SNAPSHOT",
      "crud" % "crud_2.9.1" % "1.0-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
