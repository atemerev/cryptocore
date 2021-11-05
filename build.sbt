ThisBuild / scalaVersion := "3.1.0"
ThisBuild / organization := "ai.reactivity"

lazy val root = project
  .in(file("."))
  .settings(
    name := "cryptocore",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.10" % Test
    )
  )
