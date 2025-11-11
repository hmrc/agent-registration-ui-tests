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

import scala.util.chaining.scalaUtilChainingOps

/** Sets default values for system properties if they are not already set.
  */
object EnsureSystemProperties extends LazyLogging:

  def apply(): Unit =
    logger.info("Ensuring system properties are set")
    "browser"
      .pipe: p =>
        if System.getProperty(p) == null
        then
          val defaultValue: String = "chrome"
          logger.info(s"'$p' property not set, defaulting it to '$defaultValue'")
          System.setProperty(p, "chrome")

    "environment"
      .pipe: p =>
        if (System.getProperty(p) == null) {
          val defaultValue: String = "local"
          logger.info(s"'$p' property not set, defaulting it to '$defaultValue'")
          System.setProperty(p, defaultValue)
        }

    // show browser when running tests from intellij idea
    if (SystemPropertiesHelper.isTestRunFromIdea) {
      logger.info(s"Running tests from intellij idea...")
      "browser.option.headless"
        .pipe: p =>
          if (System.getProperty(p) == null)
            val defaultValue = "false"
            logger.info(
              s"  '$p' property not set, defaulting it to '$defaultValue' (as test is running from intellij idea)"
            )
            System.setProperty(p, defaultValue)
    }
