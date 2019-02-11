organization := "mera.com"

name := "torre_recomender"

scalaVersion := "2.12.4"

resolvers ++= Seq(
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/public"
)

val catsVersion = "0.9.0"
val circeVersion = "0.8.0"
val akkaVersion = "2.5.12"
val akkaHttpCirceVersion = "1.18.1"
val akkaHttpVersion = "10.1.0"
val monixVersion = "2.3.3"


libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback"              %   "logback-classic"           % "1.2.3",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.typelevel" %% "cats" % catsVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-caching" % akkaHttpVersion,
  "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion,
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Xcheckinit"
)

publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishArtifact in Test := false

lazy val commonSettings = Seq(
  version := "1.0",
  organization := "mera.com",
  scalaVersion := "2.12.4",
  test in assembly := {}
)

lazy val torre_recomender = project.
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("mera.com.torre.recomender.Main")
  )

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case "unwanted.txt" => MergeStrategy.discard
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

enablePlugins(JavaAppPackaging)