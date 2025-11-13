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
import org.openqa.selenium.By
import org.scalactic.source.Position
import org.scalatest.Documenting
import org.scalatest.Outcome
import org.scalatest.TestSuite
import org.scalatest.TestSuiteMixin
import uk.gov.hmrc.selenium.component.PageObject

import scala.util.Try

trait PageDebugInfoOnFailure
extends TestSuiteMixin,
  Documenting,
  LazyLogging { this: TestSuite =>

  override abstract def withFixture(test: NoArgTest): Outcome =
    val testOutcome: Outcome = super.withFixture(test)
    showDebugInfoOnFailure(testOutcome)
    testOutcome

  def showDebugInfoOnFailure(testOutcome: Outcome): Unit =
    val pos = Position.here
    val showPageSource: Boolean = false

    if (testOutcome.isExceptional || testOutcome.isFailed) {
      new PageObject:
        val debugInfoOnFailure =
          s"""
             |
             |TEST FAILED, PAGE DEBUG INFO:
             |
             |>>> url was: $getCurrentUrl
             |>>> title was: $getTitle
             |>>> page text was (first 1000 characters):
             |${Try(getText(By.tagName("html")).stripSpaces.take(1000))
              .fold[String](e => s"Could not get html text: ${e.getMessage}", identity)}
             |>>> page source was:
             |${if (showPageSource)
              getPageSource
            else
              s"not shown, configure at (${pos.fileName}:${pos.lineNumber})"}
             |""".stripMargin

        logger.error(debugInfoOnFailure)
    }

  extension (s: String)
    /** Transforms string so it's easier to inspect visually
      */
    def stripSpaces: String = s
      .replaceAll("[^\\S\\r\\n]+", " ") // replace many consecutive white-spaces (but not new lines) with one space
      .replaceAll("[\r\n]+", "\n") // replace many consecutive new lines with one new line
      .split("\n")
      .map(_.trim) // trim each line
      .filterNot(_ == "") // remove any empty lines
      .mkString("\n")

}
