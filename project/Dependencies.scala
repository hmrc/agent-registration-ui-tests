import sbt.*

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "ui-test-runner" % "0.50.0" % Test,
    "org.playframework" %% "play-test"      % "3.0.9"  % Test
  )

}
