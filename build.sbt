import play.PlayScala
import sbt.Keys._
import sbt._

name := "finance"

version := "1.0"

scalacOptions += "-target:jvm-1.7"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.apache.poi" % "poi" % "3.9",
  "org.apache.poi" % "poi-ooxml" % "3.9",
  "org.scalaz" % "scalaz-core_2.10" % "7.0.0",
  "com.github.nscala-time" %% "nscala-time" % "1.8.0",
  //"org.intracer" %% "mwbot" % "0.2.0",
  "com.squants"  %% "squants"  % "0.4.2",
  "org.sweble.wikitext" % "swc-engine" % "2.0.0",
  "com.google.gdata" % "core" % "1.47.1"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)