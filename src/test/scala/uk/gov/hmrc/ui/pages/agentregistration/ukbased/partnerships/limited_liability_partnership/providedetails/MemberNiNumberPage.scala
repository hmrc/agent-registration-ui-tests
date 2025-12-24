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

package uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object MemberNiNumberPage
extends BasePage:

  override val path: String = "/agent-registration/provide-details/nino"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val yesRadio = By.id("memberNino.hasNino")
  private val noRadio = By.id("memberNino.hasNino-2")
  private val ninoField = By.id("memberNino.nino")

  def selectYes(): Unit = click(yesRadio)
  def selectNo(): Unit = click(noRadio)
  def enterNino(nino: String = "AB123456C"): Unit = sendKeys(ninoField, nino)
