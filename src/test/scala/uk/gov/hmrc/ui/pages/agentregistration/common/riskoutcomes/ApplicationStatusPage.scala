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

import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig
import org.openqa.selenium.By

object ApplicationStatusPage
extends BasePage:

  override val path: String = "/agent-registration/application-status"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val viewActionsToTakeButton = By.cssSelector("a.govuk-button--start[href='/agent-registration/conditions-not-yet-met/task-list']")
  private val viewOrPrintLink = By.linkText("View or print your application")
  private val panelBody = By.cssSelector("div.govuk-panel__body")
  private val viewActionsLink = By.linkText("View actions to take")
  private val pageTitle = By.cssSelector("h1.govuk-panel__title")

  def clickViewActionsToTakeButton(): Unit = click(viewActionsToTakeButton)
  def clickViewOrPrintLink(): Unit = click(viewOrPrintLink)
  def getApplicationReference: String = eventually {
    getText(panelBody).trim.split(":").last.trim
  }
  def clickViewActionLink(): Unit = click(viewActionsLink)
  def assertConfirmationTitle(expected: String): Unit = getText(pageTitle) shouldBe (expected)
