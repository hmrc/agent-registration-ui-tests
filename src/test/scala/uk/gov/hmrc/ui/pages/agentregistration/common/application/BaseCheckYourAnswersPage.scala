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

package uk.gov.hmrc.ui.pages.agentregistration.common.application

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage

abstract class BaseCheckYourAnswersPage
extends BasePage:

  // Common helpers
  protected def changeLinkLocatorFor(keyText: String): By = By.xpath(
    "//div[contains(@class,'govuk-summary-list__row')]" +
      s"[normalize-space(.//dt[contains(@class,'govuk-summary-list__key')])='$keyText']" +
      "//dd[contains(@class,'govuk-summary-list__actions')]//a"
  )

  def clickChangeFor(keyText: String): Unit = click(changeLinkLocatorFor(keyText))

  inline def assertPageIsDisplayed(): Unit = eventually {
    getCurrentUrl shouldBe url
  }
