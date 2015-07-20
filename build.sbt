

val commonSettings = Seq(
  organization := "com.spingo",
  sbtVersion := "0.13",
  version := "0.1.1",
  scalaVersion := "2.11.7"
)

val core = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "tsv-serializer",
    homepage := Some(url("http://git.spingineering.net/spingo/tsv-serializer")),
    publishMavenStyle := true,
    libraryDependencies ++= Seq("org.scala-lang" % "scala-compiler" % scalaVersion.value),
    publishTo := {
      val repo = if (version.value.trim.endsWith("SNAPSHOT")) "snapshots" else "releases"
      Some(repo at s"s3://spingo-oss/repositories/$repo")})

val `tests` = (project in file("./tests/")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.0" % "test")).
  dependsOn(core)

