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

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.StubbedSignInData
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.businessdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.contactdetails.AreTheseYourDetailsPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.contactdetails.AreYouAMemberOfTheLllpPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.contactdetails.ConfirmYourEmailPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.contactdetails.EmailAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.contactdetails.MulitpleNameMatchesPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.contactdetails.TelephoneNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.contactdetails.WhatIsYourNamePage
import uk.gov.hmrc.ui.utils.PasscodeHelper

object ContactDetailsFlow {

  object WhenMultiNameMatch:
    def runFlow(stubData: StubbedSignInData): Unit = addContactDetails(stubData, multiNameMatch = true)
  object WhenOnlyOneNameMatch:

    def runFlow(stubData: StubbedSignInData): Unit = addContactDetails(stubData, multiNameMatch = false)
    def runFlowUntilCyaPage(stubData: StubbedSignInData): Unit = addContactDetailsUntilCyaPage(stubData, multiNameMatch = false)

  private def addContactDetails(
    stubData: StubbedSignInData,
    multiNameMatch: Boolean
  ): Unit = {
    addContactDetailsUntilCyaPage(stubData, multiNameMatch)
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
  }

  private def addContactDetailsUntilCyaPage(
    stubData: StubbedSignInData,
    multiNameMatch: Boolean = false
  ): Unit = {
    // confirm member of llp
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertContactDetailsStatus(expectedStatus = "Incomplete")
    TaskListPage.clickOnApplicantContactDetailsLink()

    AreYouAMemberOfTheLllpPage.assertPageIsDisplayed()
    AreYouAMemberOfTheLllpPage.selectYes()
    AreYouAMemberOfTheLllpPage.clickContinue()

    // enter name
    WhatIsYourNamePage.assertPageIsDisplayed()
    WhatIsYourNamePage.enterFirstName()
    // Enter surname based on multiNameMatch flag
    if (multiNameMatch) {
      WhatIsYourNamePage.enterLastName("Tester")
      WhatIsYourNamePage.clickContinue()

      MulitpleNameMatchesPage.assertPageIsDisplayed()
      MulitpleNameMatchesPage.selectSecondMatch()
      MulitpleNameMatchesPage.clickContinue()
    }
    else {
      WhatIsYourNamePage.enterLastName("Smith")
      WhatIsYourNamePage.clickContinue()

      // confirm name matching result for single match
      AreTheseYourDetailsPage.assertPageIsDisplayed()
      AreTheseYourDetailsPage.selectYes() // Fork - Name with produce 1 match
      AreTheseYourDetailsPage.clickContinue()
    }

    // enter telephone number
    TelephoneNumberPage.assertPageIsDisplayed()
    TelephoneNumberPage.enterTelephoneNumber()
    TelephoneNumberPage.clickContinue()

    // enter email address
    EmailAddressPage.assertPageIsDisplayed()
    EmailAddressPage.enterEmailAddress()
    EmailAddressPage.clickContinue()

    // confirm email by providing confirmation code
    val passcode = PasscodeHelper.getPasscode(stubData.bearerToken, stubData.sessionId)
    ConfirmYourEmailPage.enterConfirmationCode(passcode)
    ConfirmYourEmailPage.clickContinue()

    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Member of the limited liability partnership", "Yes")
    if (!multiNameMatch) {
      CheckYourAnswersPage.assertSummaryRow("Name", "SMITH, Jane")
    }
    else {
      CheckYourAnswersPage.assertSummaryRow("Name", "Tester, John Ian")
    }
    CheckYourAnswersPage.assertSummaryRow("Telephone number", "07777777777")
    CheckYourAnswersPage.assertSummaryRow("Email address", "test@test.com")
  }

}
