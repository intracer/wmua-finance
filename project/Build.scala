import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "finance-play"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
//    jdbc,
//    anorm,
    "org.apache.poi" % "poi" % "3.9",
    "org.apache.poi" % "poi-ooxml" % "3.9",
    "org.scalaz" % "scalaz-core_2.10" % "7.0.0",
    "com.github.nscala-time" %% "nscala-time" % "0.4.2"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
