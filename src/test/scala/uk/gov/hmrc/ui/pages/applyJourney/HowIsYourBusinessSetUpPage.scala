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

package uk.gov.hmrc.ui.pages.applyJourney

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object HowIsYourBusinessSetUpPage extends BasePage {
  override val path: String    = "/agent-registration/apply/about-your-business/business-type"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val soleTraderRadio    = By.id("businessType")
  private val limitedCompany     = By.id("businessType-2")
  private val aTypeOfPartnership = By.id("businessType-3")
  private val somethingElse      = By.id("businessType-4")

  def selectSoleTrader(): Unit         = click(soleTraderRadio)
  def selectLimitedCompany(): Unit     = click(limitedCompany)
  def selectATypeOfPartnership(): Unit = click(aTypeOfPartnership)
  def selectSomethingElse(): Unit      = click(somethingElse)
}
