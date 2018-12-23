
name := "finance"

version := "0.8"

scalacOptions += "-target:jvm-1.8"

scalaVersion := "2.12.6"

val scalawikiVersion = "0.5.0"

resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.typesafeRepo("releases"),
  Resolver.bintrayRepo("intracer", "maven")
)

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "5.0.6",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.6",
  "com.iheart" %% "ficus" % "1.4.3",
  "net.codingwell" %% "scala-guice" % "4.1.0",

  "org.sweble.wikitext" % "swc-engine" % "2.0.0",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "ch.qos.logback" % "logback-classic" % "1.1.3",

  "com.h2database" % "h2" % "1.4.187",
  "org.scalawiki" %% "scalawiki-core" % scalawikiVersion,
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",

  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",
  "com.typesafe.play" %% "play-mailer" % "6.0.1",

  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",  
  
  guice,

  "io.spray" % "spray-util_2.11" % "1.3.3",
  "joda-time" % "joda-time" % "2.7",
  "jp.ne.opt" %% "chronoscala" % "0.2.1",
  "mysql" % "mysql-connector-java" % "5.1.35",

  "com.google.jimfs" % "jimfs" % "1.0" % "test",
  "com.typesafe.play" %% "play-specs2" % "2.6.15" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.53.1" % "test",
  "com.mohiva" %% "play-silhouette-testkit" % "5.0.6" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//(managedClasspath in IntegrationTest) += (packageBin in Assets).value
// workaround for https://youtrack.jetbrains.com/issue/SCL-11141
// unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

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

addCommandAlias(
  "packageAll", "; clean" +
    "; packageDebianSystemV" +
    "; clean " +
    "; packageDebianUpstart" +
    "; clean " +
    "; packageDebianSystemd" +
    "; clean " +
    "; packageRpmSystemV" +
    "; clean " +
    "; packageRpmUpstart" +
    "; clean " +
    "; packageRpmSystemd"
)

addCommandAlias(
  "packageDebSystemV", "; set serverLoading in Debian := Some(com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.SystemV)" +
    "; internalPackageDebianSystemV"
)

addCommandAlias(
  "packageDebUpstart", "; set serverLoading in Debian := Some(com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.Upstart)" +
    "; internalPackageDebianUpstart"
)

addCommandAlias(
  "packageDebSystemd", "; set serverLoading in Debian := Some(com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.Systemd)" +
    "; internalPackageDebianSystemd"
)

addCommandAlias(
  "packageRpmSystemV", "; set serverLoading in Rpm := Some(com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.SystemV)" +
    "; internalPackageRpmSystemV"
)

addCommandAlias(
  "packageRpmUpstart", "; set serverLoading in Rpm := Some(com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.Upstart)" +
    "; internalPackageRpmUpstart"
)

addCommandAlias(
  "packageRpmSystemd", "; set serverLoading in Rpm := Some(com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.Systemd)" +
    "; internalPackageRpmSystemd"
)

lazy val internalPackageDebianSystemV = taskKey[File]("creates debian package with systemv")
lazy val internalPackageDebianUpstart = taskKey[File]("creates debian package with upstart")
lazy val internalPackageDebianSystemd = taskKey[File]("creates debian package with systemd")

lazy val internalPackageRpmSystemV = taskKey[File]("creates rpm package with systemv")
lazy val internalPackageRpmUpstart = taskKey[File]("creates rpm package with upstart")
lazy val internalPackageRpmSystemd = taskKey[File]("creates rpm package with systemd")

internalPackageDebianSystemV := {
  val output = baseDirectory.value / "package" / s"wlxjury-systemv-${version.value}.deb"
  val debianFile = (packageBin in Debian).value
  IO.move(debianFile, output)
  output
}

internalPackageDebianUpstart := {
  val output = baseDirectory.value / "package" / s"wlxjury-upstart-${version.value}.deb"
  val debianFile = (packageBin in Debian).value
  IO.move(debianFile, output)
  output
}

internalPackageDebianSystemd := {
  val output = baseDirectory.value / "package" / s"wlxjury-systemd-${version.value}.deb"
  val debianFile = (packageBin in Debian).value
  IO.move(debianFile, output)
  output
}

internalPackageRpmSystemV := {
  val output = baseDirectory.value / "package" / s"wlxjury-systemv-${version.value}.rpm"
  val rpmFile = (packageBin in Rpm).value
  IO.move(rpmFile, output)
  output
}

internalPackageRpmUpstart := {
  val output = baseDirectory.value / "package" / s"wlxjury-upstart-${version.value}.rpm"
  val rpmFile = (packageBin in Rpm).value
  IO.move(rpmFile, output)
  output
}

internalPackageRpmSystemd := {
  val output = baseDirectory.value / "package" / s"wlxjury-systemd-${version.value}.rpm"
  val rpmFile = (packageBin in Rpm).value
  IO.move(rpmFile, output)
  output
}