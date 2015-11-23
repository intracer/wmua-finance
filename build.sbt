
name := "finance"

version := "1.0"

scalacOptions += "-target:jvm-1.8"

scalaVersion := "2.11.6"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.apache.poi" % "poi" % "3.12",
  "org.apache.poi" % "poi-ooxml" % "3.12",
  "org.scalaz" %% "scalaz-core" % "7.1.3",
  "com.github.nscala-time" %% "nscala-time" % "1.8.0",
  //"org.intracer" %% "mwbot" % "0.2.0",
  "com.squants"  %% "squants"  % "0.4.2",
  "org.sweble.wikitext" % "swc-engine" % "2.0.0",
  "com.google.gdata" % "core" % "1.47.1",
  "de.sciss" %% "sheet" % "0.1.0",

  "com.google.jimfs" % "jimfs" % "1.0" % "test"
  , specs2 % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)