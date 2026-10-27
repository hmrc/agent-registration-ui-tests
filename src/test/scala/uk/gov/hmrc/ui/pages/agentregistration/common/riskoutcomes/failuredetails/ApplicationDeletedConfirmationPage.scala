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

object ApplicationDeletedConfirmationPage
  extends BasePage:

    override val path: String = "/agent-registration/application-deleted"
    override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

    inline def assertPageIsDisplayed(): Unit = eventually:
      getCurrentUrl should include(url)

    private val confirmationTitle = By.cssSelector("h1.govuk-panel__title")
    private val whatHappensNextHeading = By.cssSelector("h2.govuk-heading-m")
    private val startNewApplicationLink = By.cssSelector("a.govuk-link[href='/agent-registration/apply']")
    private val finishAndSignOutLink = By.linkText("Finish and sign out")


    def assertConfirmationTitle(expected: String): Unit = eventually:
      getText(confirmationTitle).trim shouldBe expected

    def assertWhatHappensNextHeading(): Unit = eventually:
      getText(whatHappensNextHeading).trim shouldBe "What happens next"  

    def assertStartNewApplicationLinkIsDisplayed(): Unit = eventually:
      findElementBy(startNewApplicationLink) shouldBe defined
      
    def clickStartNewApplicationLink(): Unit = click(startNewApplicationLink)
    def clickFinishAndSignOutLink(): Unit = click(finishAndSignOutLink)






