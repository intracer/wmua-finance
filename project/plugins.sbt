// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers ++= Seq(
//  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.typesafeRepo("releases"),
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Scalaz Bintray Repo"  at "http://dl.bintray.com/scalaz/releases"
)
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

//addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.0.0")
