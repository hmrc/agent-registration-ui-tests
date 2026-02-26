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

import uk.gov.hmrc.ui.flows.common.application.StubbedSignInData
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.AreTheseYourDetailsPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.ConfirmYourEmailPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.individualEmailAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.IndividualNiNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.IndivdualTelephoneNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.IndividualUtrPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails.WhatIsYourNamePage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.PasscodeHelper

class ProvideDetailsSpec
extends BaseSpec:

  Feature("Complete provide individual details section"):
    Scenario(
      "User provides individual details with Nino and Utr",
      TagProvideDetails
    ):
      pending
      ProvideDetailsFlow
        .ProvideFullIndividualDetails
        .runFlow()

    Scenario(
      "User provides individual details WITHOUT Nino and Utr",
      TagProvideDetails
    ):
      pending
      ProvideDetailsFlow
        .ProvidePartialIndividualDetails
        .runFlow()

    Scenario(
      "Nino and Utr details retrieved from HMRC",
      TagProvideDetails
    ):
      pending
      ProvideDetailsFlow
        .UtrAndNinoFromHmrc
        .runFlow()

    Scenario(
      "Locked email",
      TagProvideDetails
    ):
      pending
      ProvideDetailsFlow.startJourney()
      val stubData = ProvideDetailsFlow.stubbedSignIn(hasUtr = true)
      ProvideDetailsFlow.enterName()
      ProvideDetailsFlow.enterTelephoneNumber()
      individualEmailAddressPage.assertPageIsDisplayed()
      individualEmailAddressPage.enterEmailAddress()
      individualEmailAddressPage.clickContinue()
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

    Scenario(
      "Change details from CYA",
      TagProvideDetails
    ):
      pending
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
      IndivdualTelephoneNumberPage.assertPageIsDisplayed()
      IndivdualTelephoneNumberPage.enterTelephoneNumber("07888888888")
      IndivdualTelephoneNumberPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Telephone number", "07888888888")

      // change Email address
      CheckYourAnswersPage.clickChangeFor("Email address")
      individualEmailAddressPage.assertPageIsDisplayed()
      val newEmail = individualEmailAddressPage.enterEmailAddress("@newtest.com")
      individualEmailAddressPage.clickContinue()
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
      IndividualNiNumberPage.assertPageIsDisplayed()
      IndividualNiNumberPage.enterNino("AA000000A")
      IndividualNiNumberPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("National Insurance number", "AA000000A")

      // remove National Insurance number
      CheckYourAnswersPage.clickChangeFor("Do you have a National Insurance number?")
      IndividualNiNumberPage.assertPageIsDisplayed()
      IndividualNiNumberPage.selectNo()
      IndividualNiNumberPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Do you have a National Insurance number?", "No")
      CheckYourAnswersPage.assertSummaryRowNotPresent("National Insurance number")

      // change Self Assessment Unique Taxpayer Reference number
      CheckYourAnswersPage.clickChangeFor("Do you have a Self Assessment Unique Taxpayer Reference?")
      IndividualUtrPage.assertPageIsDisplayed()
      IndividualUtrPage.enterUtr("0987654321")
      IndividualUtrPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Self Assessment Unique Taxpayer Reference", "0987654321")

      // remove Self Assessment Unique Taxpayer Reference number
      CheckYourAnswersPage.clickChangeFor("Self Assessment Unique Taxpayer Reference")
      IndividualUtrPage.assertPageIsDisplayed()
      IndividualUtrPage.selectNo()
      IndividualUtrPage.clickContinue()
      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Do you have a Self Assessment Unique Taxpayer Reference?", "No")
      CheckYourAnswersPage.assertSummaryRowNotPresent("Self Assessment Unique Taxpayer Reference")
