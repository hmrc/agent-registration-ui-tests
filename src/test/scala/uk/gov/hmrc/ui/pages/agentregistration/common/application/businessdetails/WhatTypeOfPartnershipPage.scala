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

package uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object WhatTypeOfPartnershipPage
extends BasePage {

  override val path: String = "/agent-registration/apply/about-your-business/partnership-type"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val generalPartnershipRadio = By.id("partnershipType")
  private val limitedLiabilityPartnershipRadio = By.id("partnershipType-2")
  private val limitedPartnershipRadio = By.id("partnershipType-3")
  private val scottishLiabilityPartnershipRadio = By.id("partnershipType-4")
  private val scottishPartnershipRadio = By.id("partnershipType-5")

  def selectGeneralPartnership(): Unit = click(generalPartnershipRadio)
  def selectLimitedLiabilityPartnership(): Unit = click(limitedLiabilityPartnershipRadio)
  def selectLimitedPartnership(): Unit = click(limitedPartnershipRadio)
  def selectScottishLimitedLiabilityPartnership(): Unit = click(scottishLiabilityPartnershipRadio)
  def selectScottishPartnership(): Unit = click(scottishPartnershipRadio)

  def submit(): Unit = clickContinue()

}
