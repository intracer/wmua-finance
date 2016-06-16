
name := "finance"

version := "1.0"

scalacOptions += "-target:jvm-1.8"

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "1.8.0",
  "com.squants" %% "squants" % "0.4.2",
  "org.sweble.wikitext" % "swc-engine" % "2.0.0",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.4.187",

  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "io.spray" % "spray-util_2.11" % "1.3.3",
  "joda-time" % "joda-time" % "2.7",
  "mysql" % "mysql-connector-java" % "5.1.35",

  "com.google.jimfs" % "jimfs" % "1.0" % "test"
  , specs2 % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)