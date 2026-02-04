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

package uk.gov.hmrc.ui.flows.common.application.agentdetails

import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.domain.BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInData
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.agentdetails.*
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.utils.PasscodeHelper

object AgentDetailsFlow:

  sealed trait AgentDetailOption
  object AgentDetailOption:

    case object YouProvided
    extends AgentDetailOption
    case object HmrcProvided
    extends AgentDetailOption
    case object CompaniesHouseProvided
    extends AgentDetailOption
    case object SomethingElse
    extends AgentDetailOption
    case class Custom(value: String)
    extends AgentDetailOption

  object WhenUsingProvidedOptions:
    def runFlow(
      stubData: StubbedSignInData,
      businessType: BusinessType
    ): Unit =
      startJourney()
      selectBusinessName(AgentDetailOption.HmrcProvided)
      selectTelephoneNumber(AgentDetailOption.YouProvided)
      selectEmailAddress(AgentDetailOption.YouProvided)
      selectCorrespondenceAddress(AgentDetailOption.HmrcProvided)
      businessType match
        case SoleTrader => verifyCheckYourAnswers(expectedName = "Test User", expectedNumber = "07777777777")
        case LLP => verifyCheckYourAnswers(expectedName = "Test Partnership", expectedNumber = "07777777777")
        case GeneralPartnership => verifyCheckYourAnswers(expectedName = "Electronicsson Group", expectedNumber = "07777777777")
      completeCheckYourAnswers()

  object WhenUsingCustomValues:
    def runFlow(stubData: StubbedSignInData): Unit =
      startJourney()
      selectBusinessName(AgentDetailOption.Custom("My Custom LLP"))
      selectTelephoneNumber(AgentDetailOption.Custom("07777788888"))
      selectEmailAddress(AgentDetailOption.Custom("@newtest.com"), Some(stubData))
      selectCorrespondenceAddress(AgentDetailOption.Custom(""))
      verifyCheckYourAnswers(expectedName = "My Custom LLP", expectedNumber = "07777788888")
      completeCheckYourAnswers()

  object runToCheckYourAnswers:
    def runFlow(
      stubData: StubbedSignInData,
      businessType: BusinessType
    ): Unit =
      startJourney()
      selectBusinessName(AgentDetailOption.HmrcProvided)
      selectTelephoneNumber(AgentDetailOption.YouProvided)
      selectEmailAddress(AgentDetailOption.YouProvided)
      selectCorrespondenceAddress(AgentDetailOption.HmrcProvided)
      businessType match
        case SoleTrader => verifyCheckYourAnswers(expectedName = "Test User", expectedNumber = "07777777777")
        case LLP => verifyCheckYourAnswers(expectedName = "Test Partnership", expectedNumber = "07777777777")
        case GeneralPartnership => verifyCheckYourAnswers(expectedName = "Electronicsson Group", expectedNumber = "07777777777")

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertAgentServicesAccountDetailsStatus("Incomplete")
    TaskListPage.clickOnAgentServicesAccountDetailsLink()

  def selectBusinessName(option: AgentDetailOption): Unit =
    WhatBusinessNamePage.assertPageIsDisplayed()
    option match
      case AgentDetailOption.HmrcProvided => WhatBusinessNamePage.selectExistingName()
      case AgentDetailOption.Custom(value) =>
        WhatBusinessNamePage.selectSomethingElse()
        WhatBusinessNamePage.enterCustomName(value)
      case _ => throw new IllegalArgumentException("Unsupported option for business name")
    WhatBusinessNamePage.clickContinue()

  def selectTelephoneNumber(option: AgentDetailOption): Unit =
    WhatTelephoneNumberPage.assertPageIsDisplayed()
    option match
      case AgentDetailOption.YouProvided => WhatTelephoneNumberPage.selectNumberYouProvided()
      case AgentDetailOption.HmrcProvided => WhatTelephoneNumberPage.selectNumberHmrcProvided()
      case AgentDetailOption.Custom(value) =>
        WhatTelephoneNumberPage.selectSomethingElse()
        WhatTelephoneNumberPage.enterOtherTelephoneNumber(value)
      case _ => throw new IllegalArgumentException("Unsupported option for telephone number")
    WhatTelephoneNumberPage.clickContinue()

  def selectEmailAddress(
    option: AgentDetailOption,
    stubData: Option[StubbedSignInData] = None
  ): Unit =
    WhatEmailAddressPage.assertPageIsDisplayed()
    option match
      case AgentDetailOption.YouProvided =>
        WhatEmailAddressPage.selectEmailYouProvided()
        WhatEmailAddressPage.clickContinue()
      case AgentDetailOption.HmrcProvided =>
        WhatEmailAddressPage.selectEmailHMRCProvided()
        WhatEmailAddressPage.clickContinue()
      case AgentDetailOption.Custom(value) =>
        WhatEmailAddressPage.selectSomethingElse()
        val newEmail = WhatEmailAddressPage.enterEmailAddress(value)
        WhatEmailAddressPage.clickContinue()
        EmailVerificationTestOnlyPage.assertPageIsDisplayed()
        EmailVerificationTestOnlyPage.clickContinue()

        val data = stubData.getOrElse(
          throw new IllegalArgumentException("stubData is required for Custom email flow")
        )

        val passcode = PasscodeHelper.getPasscode(
          data.bearerToken,
          data.sessionId,
          Some(newEmail)
        )

        ConfirmYourEmailPage.assertPageIsDisplayed()
        ConfirmYourEmailPage.enterConfirmationCode(passcode)
        ConfirmYourEmailPage.clickContinue()
      case _ => throw new IllegalArgumentException("Unsupported option for email address")

  def selectCorrespondenceAddress(option: AgentDetailOption): Unit =
    WhatCorrespondenceAddressPage.assertPageIsDisplayed()
    option match
      case AgentDetailOption.CompaniesHouseProvided =>
        WhatCorrespondenceAddressPage.selectAddressCompaniesHouseProvided()
        WhatCorrespondenceAddressPage.clickContinue()
      case AgentDetailOption.HmrcProvided =>
        WhatCorrespondenceAddressPage.selectAddressHMRCProvided()
        WhatCorrespondenceAddressPage.clickContinue()
      case AgentDetailOption.Custom(value) =>
        WhatCorrespondenceAddressPage.selectSomethingElse()
        WhatCorrespondenceAddressPage.clickContinue()
        LookupAddressLookupPage.assertPageIsDisplayed()
        LookupAddressLookupPage.enterPostcode()
        LookupAddressLookupPage.clickContinue()
        LookupAddressSelectPage.assertPageIsDisplayed()
        LookupAddressSelectPage.selectAddress()
        LookupAddressSelectPage.clickContinue()
        LookupAddressConfirmPage.assertPageIsDisplayed()
        LookupAddressConfirmPage.clickContinue()
      case _ => throw new IllegalArgumentException("Unsupported option for correspondence address")

  private def verifyCheckYourAnswers(
    expectedName: String,
    expectedNumber: String
  ): Unit =
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Name shown to clients", expectedName)
    CheckYourAnswersPage.assertSummaryRow("Telephone number", expectedNumber)
    // TODO Update check your answers with email and correspondence address

  private def completeCheckYourAnswers(): Unit =
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
