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

package uk.gov.hmrc.ui.pages.agentregistration.common.application.agentdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object LookupAddressLookupPage
extends BasePage:

  override val path: String = "/lookup-address"
  override val baseUrl: String = AppConfig.baseUrlCountryPicker

  inline def assertPageIsDisplayed(): Unit = eventually:
    val currentUrl = getCurrentUrl
    currentUrl should include(url)
    currentUrl should include("/lookup")

  private val postcodeField = By.id("postcode")
  private val enterAddressManuallyLink = By.id("manualAddress")

  def enterPostcode(): Unit = sendKeys(postcodeField, "ZZ9Z 9TT")
  def clickAddressManually(): Unit = click(enterAddressManuallyLink)
