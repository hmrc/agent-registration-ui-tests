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

package uk.gov.hmrc.ui.specs.ukbased.limited_company.application.agentdetails

import uk.gov.hmrc.ui.domain.BusinessType
import BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow.AgentDetailOption
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.agentdetails.*
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.PasscodeHelper

class AgentDetailsSpec
extends BaseSpec:

  Feature("Complete Business details section"):
    Scenario("User selects existing details", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)
      TaskListPage.assertAgentServicesAccountDetailsStatus("Completed")

    Scenario("User enters all custom values", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingCustomValues
        .runFlow(stubbedSignInData)
      TaskListPage.assertAgentServicesAccountDetailsStatus("Completed")

    Scenario("User mixes provided and custom options", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow.startJourney()
      AgentDetailsFlow.selectBusinessName(AgentDetailOption.Custom("My Custom Limited Company"))
      AgentDetailsFlow.selectTelephoneNumber(AgentDetailOption.HmrcProvided)
      AgentDetailsFlow.selectEmailAddress(AgentDetailOption.Custom("@newtest.com"), Some(stubbedSignInData))
      AgentDetailsFlow.selectCorrespondenceAddress(AgentDetailOption.CompaniesHouseProvided)

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Name shown to clients", "My Custom Limited Company")
      CheckYourAnswersPage.assertSummaryRow("Telephone number", "01234567890")
      CheckYourAnswersPage.clickContinue()

      TaskListPage.assertPageIsDisplayed()
      TaskListPage.assertAgentServicesAccountDetailsStatus("Completed")

    Scenario("Change Business Name from Check Your Answers page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .runToCheckYourAnswers
        .runFlow(LimitedCompany)

      CheckYourAnswersPage.clickChangeFor("Name shown to clients")

      WhatBusinessNamePage.assertPageIsDisplayed()
      WhatBusinessNamePage.assertExistingNameRadioIsSelected()
      WhatBusinessNamePage.selectSomethingElse()
      WhatBusinessNamePage.enterCustomName("Updated Company Name")
      WhatBusinessNamePage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Name shown to clients", "Updated Company Name")

    Scenario("Change Telephone Number from Check Your Answers page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .runToCheckYourAnswers
        .runFlow(LimitedCompany)

      CheckYourAnswersPage.clickChangeFor("Telephone number")

      WhatTelephoneNumberPage.assertPageIsDisplayed()
      WhatTelephoneNumberPage.assertNumberYouProvidedRadioIsSelected()
      WhatTelephoneNumberPage.selectSomethingElse()
      WhatTelephoneNumberPage.enterOtherTelephoneNumber("07777799999")
      WhatTelephoneNumberPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Telephone number", "07777799999")

    Scenario("Change Email Address from Check Your Answers page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .runToCheckYourAnswers
        .runFlow(LimitedCompany)

      CheckYourAnswersPage.clickChangeFor("Email address")

      WhatEmailAddressPage.assertPageIsDisplayed()
      WhatEmailAddressPage.assertEmailYouProvidedRadioIsSelected()
      WhatEmailAddressPage.selectSomethingElse()
      val newEmail = WhatEmailAddressPage.enterEmailAddress("@newtest.com")
      WhatEmailAddressPage.clickContinue()
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

    Scenario("Change Correspondence Address from Check Your Answers page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .runToCheckYourAnswers
        .runFlow(LimitedCompany)

      CheckYourAnswersPage.clickChangeFor("Correspondence address")

      WhatCorrespondenceAddressPage.assertPageIsDisplayed()
      WhatCorrespondenceAddressPage.assertAddressHmrcProvidedRadioIsSelected()
      WhatCorrespondenceAddressPage.selectSomethingElse()
      WhatCorrespondenceAddressPage.clickContinue()

      LookupAddressLookupPage.assertPageIsDisplayed()
      LookupAddressLookupPage.clickAddressManually()

      LookupAddressEditPage.assertPageIsDisplayed()
      LookupAddressEditPage.enterAddressLineOne("4 Privet Drive")
      LookupAddressEditPage.enterPostcode("AA1 1AA")
      LookupAddressEditPage.enterTown("Little Whinging")
      LookupAddressEditPage.clickContinue()

      LookupAddressConfirmPage.assertPageIsDisplayed()
      LookupAddressConfirmPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Correspondence address", "4 Privet Drive\nLittle Whinging\nAA1 1AA\nGB")

      // additional check that manually entered address is cached when returning to WhatCorrespondenceAddressPage
      CheckYourAnswersPage.clickChangeFor("Correspondence address")
      WhatCorrespondenceAddressPage.assertPageIsDisplayed()
      WhatCorrespondenceAddressPage.assertAddressYouProvidedRadioIsSelected()
