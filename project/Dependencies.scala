import sbt.*

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "ui-test-runner" % "0.48.0" % Test,
    "org.scalatest"          %% "scalatest"      % "3.2.19" % Test,
    "org.seleniumhq.selenium" % "selenium-java"  % "4.23.0" % Test,
    "com.typesafe"            % "config"         % "1.4.3"
  )

}
