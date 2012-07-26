import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "search"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.apache.lucene" % "lucene-core" % "3.6.0"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
