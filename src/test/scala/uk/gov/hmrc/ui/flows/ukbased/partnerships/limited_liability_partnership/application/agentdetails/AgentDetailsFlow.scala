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

package uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.agentdetails

import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.agentdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.agentdetails.WhatBusinessNamePage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.agentdetails.WhatCorrespondenceAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.agentdetails.WhatEmailAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.agentdetails.WhatTelephoneNumberPage

object AgentDetailsFlow {

  object WhenUsingProvidedOptions:
    def runFlow(): Unit =
      startJourney()
      selectBusinessName("existing")
      selectTelephoneNumber("you provided")
      selectEmailAddress("you provided")
      selectCorrespondenceAddress("companies house provided")
      verifyCheckYourAnswers(expectedName = "Test Partnership LLP", expectedNumber = "07777777777")
      completeCheckYourAnswers()

  object WhenUsingCustomValues:
    def runFlow(): Unit =
      startJourney()
      selectBusinessName("My Custom LLP")
      selectTelephoneNumber("07777788888")
      selectEmailAddress("you provided")
      selectCorrespondenceAddress("companies house provided")
      verifyCheckYourAnswers(expectedName = "My Custom LLP", expectedNumber = "07777788888")
      completeCheckYourAnswers()

  object runToCheckYourAnswers:
    def runFlow(): Unit =
      startJourney()
      selectBusinessName("existing")
      selectTelephoneNumber("you provided")
      selectEmailAddress("you provided")
      selectCorrespondenceAddress("companies house provided")
      verifyCheckYourAnswers(expectedName = "Test Partnership LLP", expectedNumber = "07777777777")

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertAgentServicesAccountDetailsStatus("Incomplete")
    TaskListPage.clickOnAgentServicesAccountDetailsLink()

  def selectBusinessName(option: String): Unit =
    WhatBusinessNamePage.assertPageIsDisplayed()
    option match
      case "existing" => WhatBusinessNamePage.selectExistingName()
      case custom =>
        WhatBusinessNamePage.selectSomethingElse()
        WhatBusinessNamePage.enterCustomName(custom)
    WhatBusinessNamePage.clickContinue()

  def selectTelephoneNumber(option: String): Unit =
    WhatTelephoneNumberPage.assertPageIsDisplayed()
    option match
      case "you provided" => WhatTelephoneNumberPage.selectNumberYouProvided()
      case "hmrc provided" => WhatTelephoneNumberPage.selectNumberHmrcProvided()
      case custom =>
        WhatTelephoneNumberPage.selectSomethingElse()
        WhatTelephoneNumberPage.enterOtherTelephoneNumber(custom)
    WhatTelephoneNumberPage.clickContinue()

  def selectEmailAddress(option: String): Unit =
    WhatEmailAddressPage.assertPageIsDisplayed()
    option match
      case "you provided" => WhatEmailAddressPage.selectEmailYouProvided()
      case "hmrc provided" => WhatEmailAddressPage.selectEmailHMRCProvided()
      case custom =>
        WhatEmailAddressPage.selectSomethingElse()
        WhatEmailAddressPage.enterEmailAddress()
    WhatEmailAddressPage.clickContinue()

  // TODO step to verify email address

  def selectCorrespondenceAddress(option: String): Unit =
    WhatCorrespondenceAddressPage.assertPageIsDisplayed()
    option match
      case "companies house provided" => WhatCorrespondenceAddressPage.selectAddressCompaniesHouseProvided()
      case "hmrc provided" => WhatCorrespondenceAddressPage.selectAddressHMRCProvided()
      case "something else" => WhatCorrespondenceAddressPage.selectSomethingElse()
    WhatCorrespondenceAddressPage.clickContinue()

  // TODO add pages and steps for adding a new address

  private def verifyCheckYourAnswers(
    expectedName: String,
    expectedNumber: String
  ): Unit =
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Name shown to clients", expectedName)
    CheckYourAnswersPage.assertSummaryRow("Telephone number", expectedNumber)

  private def completeCheckYourAnswers(): Unit =
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()

}
