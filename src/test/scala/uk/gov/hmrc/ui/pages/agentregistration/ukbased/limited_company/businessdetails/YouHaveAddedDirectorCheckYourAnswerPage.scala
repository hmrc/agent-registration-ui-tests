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

package uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.pages.PageObject.click
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.CheckYourAnswersKeyIndividualsPage.getText
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.CheckYourAnswersKeyIndividualsPage.include
import uk.gov.hmrc.ui.utils.AppConfig

object YouHaveAddedDirectorCheckYourAnswerPage
extends BasePage:

  override val path: String = "/agent-registration/apply/list-details/companies-house-officers/check-your-answers"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(path)

  private def changeNumberOfDirectorsLink: By = By.cssSelector("#main-content > div > div > p:nth-child(3) > a")

  private def changeLinkForToAddDirector(name: String): By = By.xpath(
    s"""//ul[@class='govuk-summary-list__actions-list']/li[contains(normalize-space(.), '$name')]//a[contains(@href, 'change')]"""
  )

  private def changeLinkToRemoveDirector(name: String): By = By.xpath(
    s"""//ul[@class='govuk-summary-list__actions-list']/li[contains(normalize-space(.), '$name')]//a[contains(@href, 'remove')]"""
  )

  private def warningText = By.xpath(
    "//*[@id='main-content']//*[contains(normalize-space(), 'You need to tell us about') and contains(normalize-space(), 'director')]"
  )

  def changeDirectorName(name: String): Unit = click(changeLinkForToAddDirector(name))

  def clickChangeNumberOfDirectors(): Unit = click(changeNumberOfDirectorsLink)

  def assertWarningTextIsDisplayed(expected: String): Unit = getText(warningText) should include(expected)

  def removeDirector(name: String): Unit = click(changeLinkToRemoveDirector(name))
