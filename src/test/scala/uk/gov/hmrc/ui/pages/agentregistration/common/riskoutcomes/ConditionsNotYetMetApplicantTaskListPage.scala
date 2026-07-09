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

object ConditionsNotYetMetApplicantTaskListPage
extends BasePage:

  override val path: String = "/agent-registration/conditions-not-yet-met/task-list"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val individualFailuresLink = By.cssSelector("a[aria-describedby='individualFailures-1-status']")
  private val selfAssessmentReturnsLink = By.cssSelector("a[aria-describedby='entityFailures-1-status']")
  private val selfAssessmentMissingReturns = By.id("entityFailures-1-status")

  def clickIndividualFailuresLink(): Unit = click(individualFailuresLink)

  private val amlsDetailsLink = By.cssSelector("a[aria-describedby='amlsDetails-1-status']")

  private val amlsDetailsStatus = By.id("amlsDetails-1-status")

  def assertAmlsDetailsStatus(expectedStatus: String): Unit = eventually:
    getText(amlsDetailsStatus) shouldBe expectedStatus

  def assertAmlsDetailsLinkText(expectedText: String): Unit = eventually:
    getText(amlsDetailsLink) shouldBe expectedText

  def clickOnProvideYourSupervisionDetailsLink(): Unit = click(amlsDetailsLink)

  def clickOnSelfAssessmentReturnsLink(): Unit = click(selfAssessmentReturnsLink)

  def assertSelfAssessmentMissingReturns(expectedStatus: String): Unit = getText(selfAssessmentMissingReturns) shouldBe expectedStatus
