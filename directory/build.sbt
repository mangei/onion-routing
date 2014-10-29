import play.PlayJava

name := """directory"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "commons-validator" % "commons-validator" % "1.4.0",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.51"
)
