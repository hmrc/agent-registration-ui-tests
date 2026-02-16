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

package uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails

import uk.gov.hmrc.ui.flows.common.application.StubbedSignInData
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsFlow.JourneyType.DoNotProvideNinoUtrDetails
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsFlow.JourneyType.NinoUtrAlreadyKnown
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsFlow.JourneyType.ProvideNinoUtrDetails
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsFlow.JourneyType.RunToCya
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsFlow.ProvideDetail.No
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsFlow.ProvideDetail.Yes
import uk.gov.hmrc.ui.pages.agentregistration.ProvideDetailsEntryPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.AgentStandardsPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.ApproveApplicantPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.AreTheseYourDetailsPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.ConfirmYourEmailPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.ConfirmationPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.individualEmailAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.IndividualNiNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.IndivdualTelephoneNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.IndividualDateOfBirthPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.IndividualUtrPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsStartPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.WhatIsYourNamePage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage
import uk.gov.hmrc.ui.utils.PasscodeHelper

object ProvideDetailsFlow:

  enum JourneyType:
    case ProvideNinoUtrDetails, DoNotProvideNinoUtrDetails, NinoUtrAlreadyKnown, RunToCya

  enum ProvideDetail:
    case Yes, No

  object ProvideFullIndividualDetails:
    def runFlow(): Unit =
      startJourney()
      val stubData = stubbedSignIn()
      enterName()
      enterTelephoneNumber()
      val email = enterEmailAddress(stubData)
      enterDateOfBirth
      enterNino(Yes)
      enterUtr(Yes)
      approveApplicant()
      agreeStandards()
      checkYourAnswers(email, ProvideNinoUtrDetails)
      confirmationPage()

  object ProvidePartialIndividualDetails:
    def runFlow(): Unit =
      startJourney()
      val stubData = stubbedSignIn()
      enterName()
      enterTelephoneNumber()
      val email = enterEmailAddress(stubData)
      enterDateOfBirth
      enterNino(No)
      enterUtr(No)
      approveApplicant()
      agreeStandards()
      checkYourAnswers(email, DoNotProvideNinoUtrDetails)
      confirmationPage()

  object UtrAndNinoFromHmrc:

    def runFlow(): Unit =
      startJourney()
      val stubData = stubbedSignIn(hasUtr = true)
      enterName()
      enterTelephoneNumber()
      val email = enterEmailAddress(stubData)
      approveApplicant()
      agreeStandards()
      checkYourAnswers(email, NinoUtrAlreadyKnown)
      confirmationPage()

  object RunToCheckYourAnswers:

    def runFlow(): StubbedSignInData =
      startJourney()
      val stubData = stubbedSignIn()
      enterName()
      enterTelephoneNumber()
      val email = enterEmailAddress(stubData)
      enterDateOfBirth
      enterNino(Yes)
      enterUtr(Yes)
      approveApplicant()
      agreeStandards()
      checkYourAnswers(email, RunToCya)
      stubData

  def startJourney(): Unit =
    ProvideDetailsEntryPage.open()
    ProvideDetailsEntryPage.assertPageIsDisplayed()
    ProvideDetailsEntryPage.clickProvideDetailsTestLink()
    ProvideDetailsStartPage.assertPageIsDisplayed()
    ProvideDetailsStartPage.clickContinue()
    GovernmentGatewaySignInPage.assertPageIsDisplayed()

  def stubbedSignIn(hasUtr: Boolean = false): StubbedSignInData =
    if (hasUtr) {
      val stubbedSignInData: StubbedSignInData = StubbedSignInFlow.signInAndDataSetupViaStubsForIndividualWithUtr()
      stubbedSignInData
    }
    else {
      val stubbedSignInData: StubbedSignInData = StubbedSignInFlow.signInAndDataSetupViaStubsForIndividual()
      stubbedSignInData
    }

  def enterName(): Unit =
    WhatIsYourNamePage.assertPageIsDisplayed()
    WhatIsYourNamePage.enterFirstName()
    WhatIsYourNamePage.enterLastName()
    WhatIsYourNamePage.clickContinue()
    AreTheseYourDetailsPage.assertPageIsDisplayed()
    AreTheseYourDetailsPage.selectYes()
    AreTheseYourDetailsPage.clickContinue()

  def enterTelephoneNumber(): Unit =
    IndivdualTelephoneNumberPage.assertPageIsDisplayed()
    IndivdualTelephoneNumberPage.enterTelephoneNumber()
    IndivdualTelephoneNumberPage.clickContinue()

  def enterEmailAddress(stubData: StubbedSignInData): String =
    individualEmailAddressPage.assertPageIsDisplayed()
    val email = individualEmailAddressPage.enterEmailAddress()
    individualEmailAddressPage.clickContinue()
    EmailVerificationTestOnlyPage.assertPageIsDisplayed()
    EmailVerificationTestOnlyPage.clickContinue()
    // confirm email by providing confirmation code
    val passcode = PasscodeHelper.getPasscode(stubData.bearerToken, stubData.sessionId)
    ConfirmYourEmailPage.assertPageIsDisplayed()
    ConfirmYourEmailPage.enterConfirmationCode(passcode)
    ConfirmYourEmailPage.clickContinue()
    email

  def enterDateOfBirth: Unit =
    IndividualDateOfBirthPage.assertPageIsDisplayed()
    IndividualDateOfBirthPage.enterDay()
    IndividualDateOfBirthPage.enterMonth()
    IndividualDateOfBirthPage.enterYear()
    IndividualDateOfBirthPage.clickContinue()

  def enterNino(details: ProvideDetail): Unit =
    IndividualNiNumberPage.assertPageIsDisplayed()
    details match
      case ProvideDetail.Yes =>
        IndividualNiNumberPage.selectYes()
        IndividualNiNumberPage.enterNino()
      case ProvideDetail.No => IndividualNiNumberPage.selectNo()
    IndividualNiNumberPage.clickContinue()

  def enterUtr(details: ProvideDetail): Unit =
    IndividualUtrPage.assertPageIsDisplayed()
    details match
      case ProvideDetail.Yes =>
        IndividualUtrPage.selectYes()
        IndividualUtrPage.enterUtr()
      case ProvideDetail.No => IndividualUtrPage.selectNo()
    IndividualUtrPage.clickContinue()

  def approveApplicant(): Unit =
    ApproveApplicantPage.assertPageIsDisplayed()
    ApproveApplicantPage.selectYes()
    ApproveApplicantPage.clickContinue()

  def agreeStandards(): Unit =
    AgentStandardsPage.assertPageIsDisplayed()
    AgentStandardsPage.clickContinue()

  def checkYourAnswers(
    email: String,
    variant: JourneyType
  ): Unit =
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Name", "SMITH, Jane")
    CheckYourAnswersPage.assertSummaryRow("Telephone number", "(+44) 10794554342")
    CheckYourAnswersPage.assertSummaryRow("Email address", email)

    variant match
      case JourneyType.ProvideNinoUtrDetails =>
        CheckYourAnswersPage.assertSummaryRow("Do you have a National Insurance number?", "Yes")
        CheckYourAnswersPage.assertSummaryRow("National Insurance number", "AB123456C")
        CheckYourAnswersPage.assertSummaryRow("Do you have a Self Assessment Unique Taxpayer Reference?", "Yes")
        CheckYourAnswersPage.assertSummaryRow("Self Assessment Unique Taxpayer Reference", "1234567890")
        CheckYourAnswersPage.clickContinue()

      case JourneyType.DoNotProvideNinoUtrDetails =>
        CheckYourAnswersPage.assertSummaryRow("Do you have a National Insurance number?", "No")
        CheckYourAnswersPage.assertSummaryRow("Do you have a Self Assessment Unique Taxpayer Reference?", "No")
        CheckYourAnswersPage.assertSummaryRowNotPresent("National Insurance number")
        CheckYourAnswersPage.assertSummaryRowNotPresent("Self Assessment Unique Taxpayer Reference")
        CheckYourAnswersPage.clickContinue()

      case JourneyType.NinoUtrAlreadyKnown =>
        CheckYourAnswersPage.assertSummaryRowNotPresent("Do you have a National Insurance number?")
        CheckYourAnswersPage.assertSummaryRowNotPresent("National Insurance number")
        CheckYourAnswersPage.assertSummaryRowNotPresent("Do you have a Self Assessment Unique Taxpayer Reference?")
        CheckYourAnswersPage.assertSummaryRowNotPresent("Self Assessment Unique Taxpayer Reference")
        CheckYourAnswersPage.clickContinue()

      case JourneyType.RunToCya =>
        CheckYourAnswersPage.assertSummaryRow("Do you have a National Insurance number?", "Yes")
        CheckYourAnswersPage.assertSummaryRow("National Insurance number", "AB123456C")
        CheckYourAnswersPage.assertSummaryRow("Do you have a Self Assessment Unique Taxpayer Reference?", "Yes")
        CheckYourAnswersPage.assertSummaryRow("Self Assessment Unique Taxpayer Reference", "1234567890")

  def confirmationPage(): Unit =
    ConfirmationPage.assertPageIsDisplayed()
    ConfirmationPage.verifyConfirmationTitle()
