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

package uk.gov.hmrc.ui.pages.agentregistration.common.application.agentstandards

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object AgentStandardsPage
extends BasePage:

  override val path: String = "/agent-registration/apply/agent-standard/accept-agent-standard"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  val defaultSoleTraderName: String = "ST Name ST Lastname"

  private def soleTraderAgreement(name: String): String = s"I agree that $name will meet the standard when working on behalf of clients."

  private def soleTraderOwnerAgreement(name: String): String = s"I agree that I, $name, will meet the standard when working on behalf of clients."

  private val partnershipAgreement: String = "I agree that Test Company Name will meet the standard when working on behalf of clients."

  private val generalPartnershipAgreement: String = "I agree that Electronicsson Group will meet the standard when working on behalf of clients."

  private val limitedPartnershipAgreement: String = "I agree that Test Company Name will meet the standard when working on behalf of clients."

  private val limitedCompanyAgreement: String = "I agree that Test Company Ltd will meet the standard when working on behalf of clients."

  private val scottishLimitedPartnershipAgreement: String = "I agree that Test Partnership will meet the standard when working on behalf of clients."

  private val scottishPartnershipAgreement: String = "I agree that Electronicsson Group will meet the standard when working on behalf of clients."

  private val bodyParas: By = By.id("main-content")

  def assertSoleTraderTextDisplayed(
    owner: Boolean,
    name: String = defaultSoleTraderName
  ): Unit =
    if owner
    then getText(bodyParas) should include(soleTraderOwnerAgreement(name))
    else getText(bodyParas) should include(soleTraderAgreement(name))

  def assertPartnershipTextDisplayed(): Unit = getText(bodyParas) should include(partnershipAgreement)

  def assertGeneralPartnershipDisplayed(): Unit = getText(bodyParas) should include(generalPartnershipAgreement)

  def assertLimitedPartnershipDisplayed(): Unit = getText(bodyParas) should include(limitedPartnershipAgreement)

  def assertLimitedCompanyTextDisplayed(): Unit = getText(bodyParas) should include(limitedCompanyAgreement)

  def assertScottishLimitedPartnershipTextDisplayed(): Unit = getText(bodyParas) should include(scottishLimitedPartnershipAgreement)

  def assertScottishPartnershipTextDisplayed(): Unit = getText(bodyParas) should include(scottishPartnershipAgreement)
