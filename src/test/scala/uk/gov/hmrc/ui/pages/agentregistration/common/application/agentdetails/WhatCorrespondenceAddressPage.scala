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

object WhatCorrespondenceAddressPage
extends BasePage:

  override val path: String = "/agent-registration/apply/agent-details/correspondence-address"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url
  
  private val addressCompaniesHouseProvidedRadio = By.xpath(
    s"//div[contains(@class,'govuk-radios__hint')][normalize-space() = 'This is your Companies House registered office address.']" +
      "/ancestor::div[contains(@class,'govuk-radios__item')][1]" +
      "//input[contains(@class,'govuk-radios__input')]"
  )
  private val addressHMRCProvidedRadio = By.xpath(
    s"//div[contains(@class,'govuk-radios__hint')][normalize-space() = 'This is the address HMRC has in your business record.']" +
      "/ancestor::div[contains(@class,'govuk-radios__item')][1]" +
      "//input[contains(@class,'govuk-radios__input')]"
  )
  private val addressYouProvidedRadio = By.xpath(
    s"//div[contains(@class,'govuk-radios__hint')][normalize-space() = 'This is the address you have given us.']" +
      "/ancestor::div[contains(@class,'govuk-radios__item')][1]" +
      "//input[contains(@class,'govuk-radios__input')]"
  )
  private val somethingElseRadio = By.xpath("//label[contains(@class,'govuk-radios__label')][normalize-space()='Something else']")

  def selectAddressCompaniesHouseProvided(): Unit = click(addressCompaniesHouseProvidedRadio)
  def selectAddressHMRCProvided(): Unit = click(addressHMRCProvidedRadio)
  def selectSomethingElse(): Unit = click(somethingElseRadio)
  def assertAddressCompaniesHouseProvidedRadioIsSelected(): Unit = isSelected(addressCompaniesHouseProvidedRadio) shouldBe true
  def assertAddressHmrcProvidedRadioIsSelected(): Unit = isSelected(addressHMRCProvidedRadio) shouldBe true
  def assertAddressYouProvidedRadioIsSelected(): Unit = isSelected(addressYouProvidedRadio) shouldBe true
