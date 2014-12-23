name := "tsv-serializer"

organization := "com.spingo"

sbtVersion := "0.13"

version := "0.1.1"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq("org.scala-lang" % "scala-compiler" % scalaVersion.value)

publishMavenStyle := true

publishTo := {
  val repo = if (version.value.trim.endsWith("SNAPSHOT")) "snapshots" else "releases"
  Some(repo at s"http://nexus.vpn.spingineering.net:8081/nexus/content/repositories/$repo")
}

homepage := Some(url("http://git.spingineering.net/spingo/tsv-serializer"))
