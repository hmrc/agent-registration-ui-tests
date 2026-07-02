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

package uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.{BasePage, PageObject}
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.WhatRegistrationNumberPage.{registrationNumberField, sendKeys}
import uk.gov.hmrc.ui.utils.AppConfig

object ConditionsNotYetMetAmlsRegistrationNumberPage
extends BasePage:

  override val path: String = "/agent-registration/conditions-not-yet-met/anti-money-laundering/registration-number"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  private val registrationNumberField = By.id("amlsRegistrationNumber")

  private def registrationNumberValue: String =
    Option(getElementBy(registrationNumberField).getAttribute("value")).getOrElse("")

  def enterRegistrationNumber(regNum: String = "XAML00000123456"): Unit = sendKeys(registrationNumberField, regNum)

  def enterNonHMRCRegistrationNumber(regNum: String = "12345"): Unit = sendKeys(registrationNumberField, regNum)

  def assertRegistrationNumberPrefilled(expectedValue: String): Unit = eventually:
    withClue(s"Expected prefilled registration number to be '$expectedValue' but was '$registrationNumberValue'") {
      registrationNumberValue shouldBe expectedValue
    }

