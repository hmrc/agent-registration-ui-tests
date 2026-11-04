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

package uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.BaseCheckYourAnswersPage
import uk.gov.hmrc.ui.utils.AppConfig

object DeleteApplicationPage
extends BasePage:

  override val path: String = "/agent-registration/apply/delete-application"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url


  private val pageHeading = By.cssSelector("h1")
  private val yesRadio = By.id("confirmDeleteApplication")
  private val noRadio = By.id("confirmDeleteApplication-2")

  def assertPageHeading(expected: String): Unit = eventually:
    getText(pageHeading).trim shouldBe expected

  def assertYesNoRadiosAreDisplayed(): Unit = eventually:
    findElementBy(yesRadio) shouldBe defined
    findElementBy(noRadio) shouldBe defined

  def selectYes(): Unit = click(yesRadio)
  def selectNo(): Unit = click(noRadio)
