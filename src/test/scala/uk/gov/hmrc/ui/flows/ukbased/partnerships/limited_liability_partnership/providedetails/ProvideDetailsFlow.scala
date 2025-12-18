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

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.StubbedSignInData
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.StubbedSignInFlow
import uk.gov.hmrc.ui.pages.agentregistration.ProvideDetailsEntryPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.ApproveApplicantPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.AreTheseYourDetailsPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.ConfirmYourEmailPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.MemberEmailAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.MemberNiNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.MemberTelephoneNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.MemberUtrPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsStartPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.WhatIsYourNamePage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage
import uk.gov.hmrc.ui.utils.PasscodeHelper

object ProvideDetailsFlow:

  enum JourneyType:
    case WithDetails, WithoutDetails

  object ProvideFullMemberDetails:
    def runFlow(): Unit =
      startJourney()
      val stubData = stubbedSignIn()
      enterName()
      enterTelephoneNumber()
      enterEmailAddress(stubData)
      enterNino(JourneyType.WithDetails)
      enterUtr(JourneyType.WithDetails)
      approveApplicant()

  object ProvidePartialMemberDetails:
    def runFlow(): Unit =
      startJourney()
      val stubData = stubbedSignIn()
      enterName()
      enterTelephoneNumber()
      enterEmailAddress(stubData)
      enterNino(JourneyType.WithoutDetails)
      enterUtr(JourneyType.WithoutDetails)
      approveApplicant()

  object UtrAndNinoFromHmrc:

    def runFlow(): Unit =
      startJourney()
      val stubData = stubbedSignIn(hasUtr = true)
      enterName()
      enterTelephoneNumber()
      enterEmailAddress(stubData)
      approveApplicant()

  def startJourney(): Unit =
    ProvideDetailsEntryPage.open()
    ProvideDetailsEntryPage.assertPageIsDisplayed()
    ProvideDetailsEntryPage.clickProvideDetailsTestLink()
    ProvideDetailsStartPage.assertPageIsDisplayed()
    ProvideDetailsStartPage.clickContinue()
    GovernmentGatewaySignInPage.assertPageIsDisplayed()
//    val stubbedSignInData: StubbedSignInData = StubbedSignInFlow.signInAndDataSetupViaStubsForIndividual()
//    stubbedSignInData

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
    MemberTelephoneNumberPage.assertPageIsDisplayed()
    MemberTelephoneNumberPage.enterTelephoneNumber()
    MemberTelephoneNumberPage.clickContinue()

  def enterEmailAddress(stubData: StubbedSignInData): Unit =
    MemberEmailAddressPage.assertPageIsDisplayed()
    val email = MemberEmailAddressPage.enterEmailAddress()
    MemberEmailAddressPage.clickContinue()
    EmailVerificationTestOnlyPage.assertPageIsDisplayed()
    EmailVerificationTestOnlyPage.clickContinue()
    // confirm email by providing confirmation code
    val passcode = PasscodeHelper.getPasscode(stubData.bearerToken, stubData.sessionId)
    ConfirmYourEmailPage.enterConfirmationCode(passcode)
    ConfirmYourEmailPage.clickContinue()

  def enterNino(journey: JourneyType): Unit =
    MemberNiNumberPage.assertPageIsDisplayed()
    journey match
      case JourneyType.WithDetails =>
        MemberNiNumberPage.selectYes()
        MemberNiNumberPage.enterNino()
      case JourneyType.WithoutDetails => MemberNiNumberPage.selectNo()
    MemberNiNumberPage.clickContinue()

  def enterUtr(journey: JourneyType): Unit =
    MemberUtrPage.assertPageIsDisplayed()
    journey match
      case JourneyType.WithDetails =>
        MemberUtrPage.selectYes()
        MemberUtrPage.enterUtr()
      case JourneyType.WithoutDetails => MemberUtrPage.selectNo()
    MemberUtrPage.clickContinue()

  def approveApplicant(): Unit = ApproveApplicantPage.assertPageIsDisplayed()
