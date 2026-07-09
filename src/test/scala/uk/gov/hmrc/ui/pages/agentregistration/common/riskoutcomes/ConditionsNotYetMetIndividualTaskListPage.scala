/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object ConditionsNotYetMetIndividualTaskListPage
extends BasePage:

  override val path: String = "/agent-registration/provide-details/conditions-not-yet-met/task-list"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  case class FixableActionRow(
    action: String,
    completed: String
  )

  private def normalise(text: String): String = text.replaceAll("\\s+", " ").trim

  private def actionRows: Seq[WebElement] = findElementsBy(By.cssSelector("ul.govuk-task-list li.govuk-task-list__item"))

  private def actionTextFrom(row: WebElement): String =
    val links = row.findElements(By.cssSelector("a.govuk-task-list__link"))

    if links.isEmpty then
      normalise(row.findElement(By.cssSelector(".govuk-task-list__name-and-hint")).getText)
    else
      normalise(links.get(0).getText)

  private def rowForAction(action: String): WebElement = actionRows
    .find { row =>
      val actualAction = actionTextFrom(row)

      actualAction == action
    }
    .getOrElse(fail(s"No action row found for action: $action"))

  private def actionRowFrom(row: WebElement): FixableActionRow =
    val action = actionTextFrom(row)

    val completed = normalise(row.findElement(By.cssSelector(".govuk-task-list__status")).getText)

    FixableActionRow(
      action = action,
      completed = completed
    )

  def clickActionLink(action: String): Unit =
    val row = rowForAction(action)
    val links = row.findElements(By.cssSelector("a.govuk-task-list__link"))

    if links.isEmpty then
      fail(s"No clickable action link found for action: $action")
    else
      links.get(0).click()

  def assertActionRow(expected: FixableActionRow): Unit =
    val actualRow = actionRowFrom(rowForAction(expected.action))

    actualRow shouldBe expected

  def assertActionStatus(
    action: String,
    expectedStatus: String
  ): Unit =
    val actualRow = actionRowFrom(rowForAction(action))

    actualRow.completed shouldBe expectedStatus

  def assertActionRowNotDisplayed(expected: FixableActionRow): Unit =
    val actualRows = actionRows.map(actionRowFrom)

    actualRows should not contain expected

  def assertActionNotDisplayed(action: String): Unit =
    val displayedActions = actionRows.map(actionTextFrom)

    displayedActions should not contain action
