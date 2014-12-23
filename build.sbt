name := "tsv-serializer-harness"

organization := "com.spingo"

version := "0.0"

sbtVersion := "0.13"

scalaVersion := "2.11.1"

resolvers ++= Seq()

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.0" % "test"
)

publishTo := {
  Some("snapshots" at s"http://dont-publish")
}

lazy val classMacros = project.in( file("tsv-serializer/") )

lazy val root = project.in( file(".") ).dependsOn(classMacros)
