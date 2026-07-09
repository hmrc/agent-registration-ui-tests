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

import scala.jdk.CollectionConverters._

object ConditionsNotYetMetIndividualsPage
extends BasePage:

  override val path: String = "/agent-registration/conditions-not-yet-met/individuals"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  case class ActionRow(
    name: String,
    actions: Seq[String],
    completed: String
  )

  private val individualFailuresLink = By.cssSelector("a[aria-describedby='individualFailures-1-status']")

  private def normalise(text: String): String = text.replaceAll("\\s+", " ").trim

  private def actionsTableRows: Seq[WebElement] = findElementsBy(By.cssSelector("table.govuk-table tbody tr.govuk-table__row"))

  private def rowForIndividual(name: String): WebElement = actionsTableRows
    .find { row =>
      val actualName = normalise(row.findElement(By.cssSelector("th.govuk-table__header")).getText)

      actualName == name
    }
    .getOrElse(fail(s"No actions row found for individual: $name"))

  private def actionRowFrom(row: WebElement): ActionRow =
    val name = normalise(row.findElement(By.cssSelector("th.govuk-table__header")).getText)

    val cells =
      row.findElements(By.cssSelector("td.govuk-table__cell"))
        .asScala
        .toSeq

    val actions =
      cells.head
        .findElements(By.cssSelector("li"))
        .asScala
        .map(action => normalise(action.getText))
        .toSeq

    val completed = normalise(cells(1).getText)

    ActionRow(
      name = name,
      actions = actions,
      completed = completed
    )

  def clickIndividualFailuresLink(): Unit = click(individualFailuresLink)

  def assertActionsRow(expected: ActionRow): Unit =
    val actualRow = actionRowFrom(rowForIndividual(expected.name))

    actualRow shouldBe expected

  def assertActionsRowNotDisplayed(expected: ActionRow): Unit =
    val actualRows = actionsTableRows.map(actionRowFrom)

    actualRows should not contain expected

  def assertIndividualNotDisplayed(name: String): Unit =
    val displayedNames = actionsTableRows.map { row =>
      normalise(row.findElement(By.cssSelector("th.govuk-table__header")).getText)
    }

    displayedNames should not contain name
