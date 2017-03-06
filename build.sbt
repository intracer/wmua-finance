
name := "finance"

version := "0.8"

scalacOptions += "-target:jvm-1.8"

scalaVersion := "2.11.8"

val scalawikiVersion = "0.5-M5"

resolvers += Resolver.bintrayRepo("intracer", "maven")
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
  "com.iheart" %% "ficus" % "1.2.6",
  "net.codingwell" %% "scala-guice" % "4.0.1",

  "com.github.nscala-time" %% "nscala-time" % "2.10.0",
  "com.squants" %% "squants" % "0.4.2",
  "org.sweble.wikitext" % "swc-engine" % "2.0.0",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "ch.qos.logback" % "logback-classic" % "1.1.3",

  "com.h2database" % "h2" % "1.4.187",
  "org.scalawiki" %% "scalawiki-core" % scalawikiVersion,
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",

  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3",
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2",
  "com.typesafe.play" %% "play-mailer" % "5.0.0",

  "io.spray" % "spray-util_2.11" % "1.3.3",
  "joda-time" % "joda-time" % "2.7",
  "mysql" % "mysql-connector-java" % "5.1.35",

  "com.google.jimfs" % "jimfs" % "1.0" % "test",
  "com.typesafe.play" %% "play-specs2" % "2.5.12" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.53.1" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//(managedClasspath in IntegrationTest) += (packageBin in Assets).value
// workaround for https://youtrack.jetbrains.com/issue/SCL-11141
unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

organization := "org.intracer"

rpmVendor := "intracer"

rpmUrl := Some("https://github.com/intracer/finance")

rpmLicense := Some("ASL 2.0")

packageSummary := "finance tool"

packageDescription :=  """finance tool""".stripMargin.replace('\n', ' ')

maintainer := "Ilya Korniiko <intracer@gmail.com>"

routesGenerator := StaticRoutesGenerator

debianPackageDependencies in Debian ++= Seq("java8-runtime")

debianPackageRecommends in Debian ++= Seq("virtual-mysql-server")

lazy val packageDebianSystemV = taskKey[File]("creates debian package with systemv")
lazy val packageDebianUpstart = taskKey[File]("creates debian package with upstart")
lazy val packageDebianSystemD = taskKey[File]("creates debian package with systemd")

lazy val packageRpmSystemV = taskKey[File]("creates rpm package with systemv")
lazy val packageRpmUpstart = taskKey[File]("creates rpm package with upstart")
lazy val packageRpmSystemD = taskKey[File]("creates rpm package with systemd")

packageDebianSystemV := {
  serverLoading in Debian := com.typesafe.sbt.packager.archetypes.ServerLoader.SystemV
  val output = baseDirectory.value / "package" / s"${name.value}-systemv-${version.value}.deb"
  val debianFile = (packageBin in Debian).value
  IO.move(debianFile, output)
  output
}

packageDebianUpstart := {
  serverLoading in Debian := com.typesafe.sbt.packager.archetypes.ServerLoader.Upstart
  val output = baseDirectory.value / "package" / s"${name.value}-upstart-${version.value}.deb"
  val debianFile = (packageBin in Debian).value
  IO.move(debianFile, output)
  output
}

packageDebianSystemD := {
  serverLoading in Debian := com.typesafe.sbt.packager.archetypes.ServerLoader.Systemd
  val output = baseDirectory.value / "package" / s"${name.value}-systemd-${version.value}.deb"
  val debianFile = (packageBin in Debian).value
  IO.move(debianFile, output)
  output
}

packageRpmSystemV := {
  serverLoading in Rpm := com.typesafe.sbt.packager.archetypes.ServerLoader.SystemV
  val output = baseDirectory.value / "package" / s"${name.value}-systemv-${version.value}.rpm"
  val rpmFile = (packageBin in Rpm).value
  IO.move(rpmFile, output)
  output
}

packageRpmUpstart := {
  serverLoading in Rpm := com.typesafe.sbt.packager.archetypes.ServerLoader.Upstart
  val output = baseDirectory.value / "package" / s"${name.value}-upstart-${version.value}.rpm"
  val rpmFile = (packageBin in Rpm).value
  IO.move(rpmFile, output)
  output
}

packageRpmSystemD := {
  serverLoading in Rpm := com.typesafe.sbt.packager.archetypes.ServerLoader.Systemd
  val output = baseDirectory.value / "package" / s"${name.value}-systemd-${version.value}.rpm"
  val rpmFile = (packageBin in Rpm).value
  IO.move(rpmFile, output)
  output
}
