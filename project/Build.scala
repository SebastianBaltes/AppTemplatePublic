import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "AppTemplate"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "postgresql" % "postgresql" % "9.1-901.jdbc4",
        "commons-lang" % "commons-lang" % "2.6",
        "commons-collections" % "commons-collections" % "3.2.1",
        "commons-io" % "commons-io" % "2.3", 
        "org.apache.commons" % "commons-email" % "1.1"
    )

    // Only compile the bootstrap bootstrap.less file and any other *.less file in the stylesheets directory 
	def customLessEntryPoints(base: File): PathFinder = ( 
	    (base / "app" / "assets" / "admin" / "bootstrap" / "less" * "bootstrap.less") +++
	    (base / "app" / "assets" / "admin" / "bootstrap" / "less" * "responsive.less") +++ 
	    (base / "app" / "assets" / "admin" / "stylesheets" * "*.less") +++
	    (base / "app" / "assets" / "site" / "bootstrap" / "less" * "bootstrap.less") +++
	    (base / "app" / "assets" / "site" / "bootstrap" / "less" * "responsive.less") +++ 
	    (base / "app" / "assets" / "site" / "stylesheets" * "*.less")
	)
	
	val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
	  // Add your own project settings here
	    lessEntryPoints <<= baseDirectory(customLessEntryPoints)
	)    

}
