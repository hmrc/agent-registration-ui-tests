/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ui.utils

import com.typesafe.scalalogging.LazyLogging
import org.scalactic.source.Position
import org.scalatest.Outcome
import org.scalatest.TestSuite
import org.scalatest.TestSuiteMixin
import uk.gov.hmrc.selenium.webdriver.Browser
import uk.gov.hmrc.selenium.webdriver.Driver

import scala.util.Failure
import scala.util.Try
import scala.util.Success

trait BrowserQuit
extends TestSuiteMixin,
  LazyLogging { self: TestSuite =>

  override abstract def withFixture(test: NoArgTest): Outcome =
    Try(super.withFixture(test)) match
      case Failure(ex) =>
        quitBrowserIfNeeded(test, ex)
        throw ex
      case Success(testOutcome) if testOutcome.isNoSuccess =>
        quitBrowserIfNeeded(test, testOutcome)
        testOutcome
      case Success(testOutcome) =>
        quitBrowserForTest(test)
        testOutcome

  def quitBrowserIfNeeded(
    test: NoArgTest,
    outcomeOrException: Outcome | Throwable
  )(using Position): Unit =
    // allow forcing browser to stay open on failure if a system property is set
    val keepBrowserOnFailure: Boolean = Option(System.getProperty("keepBrowserOnFailure")).exists(_.equalsIgnoreCase("true"))
    if SystemPropertiesHelper.isTestRunFromIdea || keepBrowserOnFailure
    then
      logger.info(
        s"Test run from IntelliJ (or keepBrowserOnFailure property set) " +
          s"and test failed; skipping browser quit so it's easier to debug"
      )

      Try: // change size of the window and move it the the right to reflect the test has ended
        val screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize()
        val windowWidth = screenSize.width / 2
        val windowHeight = screenSize.height / 2
        Driver.instance.manage().window().setSize(new org.openqa.selenium.Dimension(
          windowWidth,
          windowHeight
        ))
        TestFailureOverlay.renderTestFailureOverlay(test.name, outcomeOrException)

      Driver.instance = null
    else quitBrowserForTest(test)

  private def quitBrowserForTest(test: NoArgTest): Unit =
    logger.info(s"Quitting browser after test '${test.name}'")
    new Browser:
      Try:
        quitBrowser()
        Driver.instance = null

  extension (o: Outcome)
    def isNoSuccess: Boolean = o.isFailed || o.isExceptional

}
