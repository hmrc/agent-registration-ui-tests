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

package uk.gov.hmrc.ui.pages.agentregistration.IndividualDetailsPage

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object RelevantIndividualDateOfBirthPage
extends BasePage:

  override val path: String = "/agent-registration/apply/list-details/provide-details/date-of-birth"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val dayField = By.id("applicant-provided.date-of-birth.day")
  private val monthField = By.id("applicant-provided.date-of-birth.month")
  private val yearField = By.id("applicant-provided.date-of-birth.year")

  def fillInDateOfBirth(
    day: String,
    month: String,
    year: String
  ): Unit =
    sendKeys(dayField, day)
    sendKeys(monthField, month)
    sendKeys(yearField, year)
