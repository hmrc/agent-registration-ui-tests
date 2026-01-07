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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.limited_liability_partnership.providedetails

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.StubbedSignInData
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.AreTheseYourDetailsPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.ConfirmYourEmailPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.MemberEmailAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.MemberNiNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.MemberTelephoneNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.MemberUtrPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.WhatIsYourNamePage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.PasscodeHelper

class ProvideDetailsSpec
extends BaseSpec:

  Feature("Complete provide member details section"):
    Scenario("User provides member details with Nino and Utr", HappyPath):

      ProvideDetailsFlow
        .ProvideFullMemberDetails
        .runFlow()

    Scenario("User provides member details WITHOUT Nino and Utr", HappyPath):

      ProvideDetailsFlow
        .ProvidePartialMemberDetails
        .runFlow()

    Scenario("Nino and Utr details retrieved from HMRC", HappyPath):

      ProvideDetailsFlow
        .UtrAndNinoFromHmrc
        .runFlow()

    Scenario("Locked email", HappyPath):

      ProvideDetailsFlow.startJourney()
      val stubData = ProvideDetailsFlow.stubbedSignIn(hasUtr = true)
      ProvideDetailsFlow.enterName()
      ProvideDetailsFlow.enterTelephoneNumber()
      MemberEmailAddressPage.assertPageIsDisplayed()
      MemberEmailAddressPage.enterEmailAddress()
      MemberEmailAddressPage.clickContinue()
      EmailVerificationTestOnlyPage.assertPageIsDisplayed()
      EmailVerificationTestOnlyPage.clickContinue()
      ConfirmYourEmailPage.assertPageIsDisplayed()
      ConfirmYourEmailPage.forceInvalidAttempts("XXXXXX", attempts = 5)
      ConfirmYourEmailPage.enterConfirmationCode("XXXXXX")
      ConfirmYourEmailPage.clickContinue()
      ConfirmYourEmailPage.assertPageHeading("We could not confirm your identity")
      ConfirmYourEmailPage.clickChangeEmailAddress()
      ProvideDetailsFlow.enterEmailAddress(stubData: StubbedSignInData)
      ProvideDetailsFlow.approveApplicant()

    Scenario("Change details from CYA", HappyPath):

      val stubbedSignInData = ProvideDetailsFlow
        .RunToCheckYourAnswers
        .runFlow()

      // change Name
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.clickChangeFor("Name")
      WhatIsYourNamePage.assertPageIsDisplayed()
      WhatIsYourNamePage.enterFirstName("Jane")
      WhatIsYourNamePage.enterLastName("Lasso")
      WhatIsYourNamePage.clickContinue()
      AreTheseYourDetailsPage.assertPageIsDisplayed()
      AreTheseYourDetailsPage.selectYes()
      AreTheseYourDetailsPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Name", "LASSO, Jane")

      // change Telephone number
      CheckYourAnswersPage.clickChangeFor("Telephone number")
      MemberTelephoneNumberPage.assertPageIsDisplayed()
      MemberTelephoneNumberPage.enterTelephoneNumber("07888888888")
      MemberTelephoneNumberPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Telephone number", "07888888888")

      // change Email address
      CheckYourAnswersPage.clickChangeFor("Email address")
      MemberEmailAddressPage.assertPageIsDisplayed()
      val newEmail = MemberEmailAddressPage.enterEmailAddress("@newtest.com")
      MemberEmailAddressPage.clickContinue()
      EmailVerificationTestOnlyPage.assertPageIsDisplayed()
      EmailVerificationTestOnlyPage.clickContinue()
      // Get a fresh passcode using the SAME session
      val passcode = PasscodeHelper.getPasscode(
        stubbedSignInData.bearerToken,
        stubbedSignInData.sessionId,
        expectedEmail = Some(newEmail)
      )
      ConfirmYourEmailPage.assertPageIsDisplayed()
      ConfirmYourEmailPage.enterConfirmationCode(passcode)
      ConfirmYourEmailPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Email address", newEmail)

      // change National Insurance number
      CheckYourAnswersPage.clickChangeFor("National Insurance number")
      MemberNiNumberPage.assertPageIsDisplayed()
      MemberNiNumberPage.enterNino("AA000000A")
      MemberNiNumberPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("National Insurance number", "AA000000A")

      // remove National Insurance number
      CheckYourAnswersPage.clickChangeFor("Do you have a National Insurance number?")
      MemberNiNumberPage.assertPageIsDisplayed()
      MemberNiNumberPage.selectNo()
      MemberNiNumberPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Do you have a National Insurance number?", "No")
      CheckYourAnswersPage.assertSummaryRowNotPresent("National Insurance number")

      // change Self Assessment Unique Taxpayer Reference number
      CheckYourAnswersPage.clickChangeFor("Do you have a Self Assessment Unique Taxpayer Reference?")
      MemberUtrPage.assertPageIsDisplayed()
      MemberUtrPage.enterUtr("0987654321")
      MemberUtrPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Self Assessment Unique Taxpayer Reference", "0987654321")

      // remove Self Assessment Unique Taxpayer Reference number
      CheckYourAnswersPage.clickChangeFor("Self Assessment Unique Taxpayer Reference")
      MemberUtrPage.assertPageIsDisplayed()
      MemberUtrPage.selectNo()
      MemberUtrPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Do you have a Self Assessment Unique Taxpayer Reference?", "No")
      CheckYourAnswersPage.assertSummaryRowNotPresent("Self Assessment Unique Taxpayer Reference")
