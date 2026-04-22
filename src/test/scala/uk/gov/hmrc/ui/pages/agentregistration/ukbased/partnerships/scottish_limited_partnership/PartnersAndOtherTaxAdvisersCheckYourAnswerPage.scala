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

package uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.scottish_limited_partnership

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.pages.PageObject.click
import uk.gov.hmrc.ui.utils.AppConfig

object PartnersAndOtherTaxAdvisersCheckYourAnswerPage
extends BasePage:

  override val path: String = "/agent-registration/apply/list-details/check-your-answers"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(path)

  private def changeCompaniesHouseListOfPartnersCorrectLink: By = By.xpath("a[href='/agent-registration/apply/list-details/companies-house-officer']")
  private def changeOtherRelevantTaxAdvisersLink: By = By.cssSelector("a[href='/agent-registration/apply/list-details/how-many-other-individuals']")
  private def changeOtherIndividualTaxAdvisersLink: By = By.cssSelector(
    "a[href='/agent-registration/apply/list-details/other-relevant-individuals/check-your-answers']"
  )

  def clickChangeCompaniesHouseListOfPartnersCorrect(): Unit = click(changeCompaniesHouseListOfPartnersCorrectLink)

  def clickChangeOtherRelevantTaxAdvisers(): Unit = click(changeOtherRelevantTaxAdvisersLink)

  def clickChangeOtherIndividualTaxAdvisers(): Unit = click(changeOtherIndividualTaxAdvisersLink)
