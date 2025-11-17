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
import org.scalatest.BeforeAndAfterEachTestData
import org.scalatest.TestSuite
import uk.gov.hmrc.selenium.webdriver.Browser
import uk.gov.hmrc.selenium.webdriver.Driver
import uk.gov.hmrc.ui.utils.RichMatchers.eventually

trait BrowserStart
extends BeforeAndAfterEachTestData,
  LazyLogging { self: TestSuite =>

  override def beforeEach(testData: org.scalatest.TestData): Unit =
    super.beforeEach(testData)
    new Browser:
      logger.info(s"Trying to starting browser for test ${testData.name} (current Driver.instance = ${Driver.instance})")

      // For sbt / run-tests.sh runs, make sure we don't reuse a stale driver
      if !SystemPropertiesHelper.isTestRunFromIdea && Driver.instance != null then
        logger.info("Driver.instance was non-null before test; clearing stale reference so a fresh browser can be started")
        Driver.instance = null

      eventually:
        logger.info(s"...starting browser for test '${testData.name}' (current Driver.instance = ${Driver.instance}")
        if Driver.instance == null then startBrowser()

}
