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

package uk
  .gov.hmrc.ui.specs.ukbased.partnerships.limited_liability_partnership.application.contactdetails

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.ApplicantNamePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.ConfirmYourEmailPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.EmailAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.TelephoneNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.PasscodeHelper

class ContactDetailsSpec
extends BaseSpec:

  Feature("Complete Contact Details section"):

    Scenario("Change Name from CYA page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()
      ContactDetailsFlow
        .addContactDetailsUntilCyaPage(stubbedSignInData)

      CheckYourAnswersPage.clickChangeFor("Name")

      ApplicantNamePage.assertPageIsDisplayed()
      ApplicantNamePage.enterFullName("John Jones")
      ApplicantNamePage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Name", "John Jones")

    Scenario("Change Email address from CYA page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()
      ContactDetailsFlow
        .addContactDetailsUntilCyaPage(stubbedSignInData)

      CheckYourAnswersPage.clickChangeFor("Email address")

      EmailAddressPage.assertPageIsDisplayed()
      val newEmail = EmailAddressPage.enterEmailAddress("@newtest.com")
      EmailAddressPage.clickContinue()
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

    Scenario("Change Telephone number from CYA page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()
      ContactDetailsFlow
        .addContactDetailsUntilCyaPage(stubbedSignInData)

      CheckYourAnswersPage.clickChangeFor("Telephone number")

      TelephoneNumberPage.assertPageIsDisplayed()
      TelephoneNumberPage.enterTelephoneNumber("08888888888")
      TelephoneNumberPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Telephone number", "08888888888")
