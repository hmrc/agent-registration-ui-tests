import sbt.*

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "ui-test-runner"              % "0.52.0" % Test,
    "org.playframework" %% "play-test"                   % "3.0.10" % Test,
    "org.mongodb.scala"  % "mongo-scala-driver_2.13"     % "5.6.4" % Test
  )

}
