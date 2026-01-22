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

package uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.contactdetails

import uk.gov.hmrc.ui.flows.common.application.StubbedSignInData
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.ApplicantNamePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.ConfirmYourEmailPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.EmailAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.contactdetails.TelephoneNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.utils.PasscodeHelper

object ContactDetailsFlow {

  def runFlow(stubData: StubbedSignInData): Unit = {
    addContactDetails(stubData)
  }

  private def addContactDetails(
    stubData: StubbedSignInData
  ): Unit = {
    addContactDetailsUntilCyaPage(stubData)
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
  }

  def addContactDetailsUntilCyaPage(
    stubData: StubbedSignInData
  ): Unit = {
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertContactDetailsStatus(expectedStatus = "Incomplete")
    TaskListPage.clickOnApplicantContactDetailsLink()

    ApplicantNamePage.assertPageIsDisplayed()
    ApplicantNamePage.enterFullName("John Ian Tester")
    ApplicantNamePage.clickContinue()

    // enter telephone number
    TelephoneNumberPage.assertPageIsDisplayed()
    TelephoneNumberPage.enterTelephoneNumber()
    TelephoneNumberPage.clickContinue()

    // enter email address
    EmailAddressPage.assertPageIsDisplayed()
    val email = EmailAddressPage.enterEmailAddress()
    EmailAddressPage.clickContinue()

    // get email verification code from test only page
    EmailVerificationTestOnlyPage.assertPageIsDisplayed()
    EmailVerificationTestOnlyPage.clickContinue()

    // confirm email by providing confirmation code
    val passcode = PasscodeHelper.getPasscode(stubData.bearerToken, stubData.sessionId)
    ConfirmYourEmailPage.enterConfirmationCode(passcode)
    ConfirmYourEmailPage.clickContinue()

    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Name", "John Ian Tester")
    CheckYourAnswersPage.assertSummaryRow("Telephone number", "07777777777")
    CheckYourAnswersPage.assertSummaryRow("Email address", email)
  }

}
