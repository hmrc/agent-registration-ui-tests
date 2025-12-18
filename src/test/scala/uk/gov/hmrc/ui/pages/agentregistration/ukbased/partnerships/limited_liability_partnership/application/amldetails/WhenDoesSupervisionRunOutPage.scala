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

package uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.amldetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

import java.time.LocalDate
import scala.language.postfixOps

object WhenDoesSupervisionRunOutPage
extends BasePage:

  override val path: String = "/agent-registration/apply/anti-money-laundering/supervision-runs-out"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val today = LocalDate.now()
  private val dayStr = today.getDayOfMonth.toString
  private val monthStr = today.getMonthValue.toString
  private val yearStr = (today.getYear + 1).toString
  private val expiryDayField = By.id("amlsExpiryDate.day")
  private val expiryMonthField = By.id("amlsExpiryDate.month")
  private val expiryYearField = By.id("amlsExpiryDate.year")

  def enterDay(): Unit = sendKeys(expiryDayField, dayStr)
  def enterMonth(): Unit = sendKeys(expiryMonthField, monthStr)
  def enterYear(): Unit = sendKeys(expiryYearField, yearStr)
