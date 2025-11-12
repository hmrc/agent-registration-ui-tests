ThisBuild / scalafmtOnCompile := true

lazy val root = (project in file("."))
  .settings(
    name := "agent-registration-ui-tests",
    version := "0.1.0",
    scalaVersion := "3.3.4",
    libraryDependencies ++= Dependencies.test
  )
