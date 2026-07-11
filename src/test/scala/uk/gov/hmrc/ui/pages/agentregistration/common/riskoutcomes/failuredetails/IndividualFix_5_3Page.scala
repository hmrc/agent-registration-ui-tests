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
import uk.gov.hmrc.ui.utils.AppConfig

object IndividualFix_5_3Page
extends BasePage:

  override val path: String = "/agent-registration/provide-details/conditions-not-yet-met/failure-details/IndividualFix.5.3"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  private val yesRadio = By.cssSelector("input[name='isFixed'][value='Yes']")
  private val howToPayVatLink = By.cssSelector("a[href*='gov.uk/government/collections/paying-hmrc-detailed-information#vat']")
  private val howToPayVatUrl = "https://www.gov.uk/government/collections/paying-hmrc-detailed-information#vat"

  def selectYes(): Unit = click(yesRadio)

  def clickSelfAssessmentTaxReturnsLinkAndAssertUrl(): Unit = clickLinkAndAssertUrlInNewTab(howToPayVatLink, howToPayVatUrl)
