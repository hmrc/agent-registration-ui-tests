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

package uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.agentdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig
import uk.gov.hmrc.ui.utils.RandomData

object WhatEmailAddressPage
extends BasePage:

  override val path: String = "/agent-registration/apply/agent-details/email-address"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val emailYouProvidedRadio = By.id("agentEmailAddress")
  private val emailHMRCProvidedRadio = By.id("agentEmailAddress-2")
  private val somethingElseRadio = By.id("agentEmailAddress-4")
  private val enterEmailAddressField = By.id("otherAgentEmailAddress")

  private val saveAndComeBackButton = By.xpath("//button[@value='SaveAndComeBackLater']")

  def selectEmailYouProvided(): Unit = click(emailYouProvidedRadio)
  def selectEmailHMRCProvided(): Unit = click(emailHMRCProvidedRadio)
  def selectSomethingElse(): Unit = click(somethingElseRadio)
  def enterEmailAddress(suffix: String = "@test.com"): String = {
    val value = RandomData.email(suffix)
    sendKeys(enterEmailAddressField, value)
    value
  }
  def assertEmailYouProvidedRadioIsSelected(): Unit = isSelected(emailYouProvidedRadio) shouldBe true

  def saveAndComeBack(): Unit = click(saveAndComeBackButton)
