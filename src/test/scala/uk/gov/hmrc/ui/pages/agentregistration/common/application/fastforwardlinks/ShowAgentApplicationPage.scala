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

package uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.EntryPage
import uk.gov.hmrc.ui.pages.PageObject.click
import uk.gov.hmrc.ui.pages.PageObject.getCurrentUrl
import uk.gov.hmrc.ui.pages.PageObject.getText
import uk.gov.hmrc.ui.utils.RichMatchers.*

object ShowAgentApplicationPage
extends EntryPage:

  override val path: String = "/agent-registration/test-only/show-agent-application-tile"
  override val baseUrl: String = FastForwardLinksPage.baseUrl

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  private val goToTaskListLink = By.id("goto-task-list-page")
  private val logInLink = By.linkText("login")
  private val gotToExternalStubLink = By.id("goto-agents-external-stubs")

  private val internalUserIdValue = By.xpath(
    "//dt[normalize-space()='Internal user id']/following-sibling::dd/span"
  )

  def clickGoToTaskListLink(): Unit = click(goToTaskListLink)
  def clickLogInLink(): Unit = click(logInLink)
  def clickGoToExternalStubLink(): Unit = click(gotToExternalStubLink)
  def getInternalUserDetails: (String, String) =
    val rawValue = getText(internalUserIdValue).trim
    val parts = rawValue.split("@")
    require(parts.length == 2, s"Unexpected format for Internal user id: $rawValue")
    val username = parts(0)
    val planetId = parts(1)
    (username, planetId)
