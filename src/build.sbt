organization := "mera.com"

name := "torre_recomender"

scalaVersion := "2.12.4"

resolvers ++= Seq(
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"
)

val circeVersion = "0.7.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging"	%%  "scala-logging"	          % "3.5.0",
  //"org.scalatest"		            %   "scalatest_2.12"	      % "3.0.0"   % "test",
  "org.reactivemongo"           %%  "reactivemongo"            % "0.12.3",
  "ch.qos.logback"              %   "logback-classic"           % "1.1.3",
  "com.typesafe.akka"           %   "akka-slf4j_2.12"           % "2.4.14",
  "com.typesafe.akka"           %%  "akka-http"               % "10.0.0",
  "de.heikoseeberger"           %%  "akka-http-circe"         % "1.13.0"
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