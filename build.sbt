lazy val root = (project in file(".")).settings(projectSettings: _*)

lazy val projectSettings =
  Seq(
    organization := "anonymous",
    version := "1.0",
    scalaVersion := "2.11.7",
    sbtVersion := "0.13.7",
    dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.6",
      "org.seleniumhq.selenium" % "selenium-java" % "2.35.0",
      "com.machinepublishers" % "jbrowserdriver" % "0.14.5",
      "org.codemonkey.simplejavamail" % "simple-java-mail" % "3.1.1"
    )
  )
