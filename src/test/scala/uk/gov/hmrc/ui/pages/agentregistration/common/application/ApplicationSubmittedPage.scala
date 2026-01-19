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

package uk.gov.hmrc.ui.pages.agentregistration.common.application

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object ApplicationSubmittedPage
extends BasePage:

  override val path: String = "/agent-registration/application-submitted"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  private val copyLinkButton = By.cssSelector("button.govuk-button.govuk-button--secondary.copy-to-clipboard")
  private val viewOrPrintLink = By.linkText("View or print your application")
  def getCopyLinkButtonText: String = getText(copyLinkButton).trim

  def clickCopyLinkButton(): Unit = click(copyLinkButton)

  def clickViewOrPrintLink(): Unit = click(viewOrPrintLink)

  def assertCopyLinkButtonText(expected: String): Unit = eventually {
    getText(copyLinkButton).trim shouldEqual expected
  }
