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

object CheckYourAnswersPage extends BasePage {
  override val path: String    = "/agent-registration/apply/applicant/check-your-answers"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  
  // Key labels used on this page (reusable across journeys)
//  val memberOfLlpKey: String = "Member of the limited liability partnership"
//  val nameKey: String        = "Name"
//  val emailKey: String       = "Email address"           // example
//  val phoneKey: String       = "Telephone number"        // example

  /** Low-level helper: locator for the value cell for a given key text */
  private def valueLocatorFor(keyText: String): By =
    By.xpath(
      "//div[contains(@class,'govuk-summary-list__row')]" +
        s"[normalize-space(.//dt[contains(@class,'govuk-summary-list__key')])='$keyText']" +
        "//dd[contains(@class,'govuk-summary-list__value')]"
    )

  /** Generic getter */
  def getSummaryValueFor(keyText: String): String =
    getText(valueLocatorFor(keyText)).trim

  /** Generic assertion that works for any journey / key */
  def assertSummaryRow(key: String, expectedValue: String): Unit = {
    val actual = getSummaryValueFor(key)
    withClue(s"For summary key '$key': ") {
      actual shouldBe expectedValue
    }
  }
}
