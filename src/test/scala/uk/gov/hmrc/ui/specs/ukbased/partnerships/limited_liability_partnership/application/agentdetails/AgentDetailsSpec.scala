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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.limited_liability_partnership.application.agentdetails

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.agentdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.agentdetails.WhatBusinessNamePage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.agentdetails.WhatTelephoneNumberPage
import uk.gov.hmrc.ui.specs.BaseSpec

class AgentDetailsSpec
extends BaseSpec:

  Feature("Complete Business details section"):
    Scenario("User selects existing details", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow()
      TaskListPage.assertAgentServicesAccountDetailsStatus("Completed") // TODO change to complete once email and correspondence addresses added

    Scenario("User enters all custom values", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingCustomValues
        .runFlow()
      TaskListPage.assertAgentServicesAccountDetailsStatus("Completed") // TODO change to complete once email and correspondence addresses added

    Scenario("User mixes provided and custom options", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)

      AgentDetailsFlow.startJourney()
      AgentDetailsFlow.selectBusinessName("My Custom LLP")
      AgentDetailsFlow.selectTelephoneNumber("hmrc provided")
      AgentDetailsFlow.selectEmailAddress("hmrc provided")
      AgentDetailsFlow.selectCorrespondenceAddress("hmrc provided")

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Name shown to clients", "My Custom LLP")
      CheckYourAnswersPage.assertSummaryRow("Telephone number", "01234567890")
      CheckYourAnswersPage.clickContinue()

      TaskListPage.assertPageIsDisplayed()
      TaskListPage.assertAgentServicesAccountDetailsStatus("Completed") // TODO change to complete once email and correspondence addresses added

    Scenario("Change Business Name from Check Your Answers page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .runToCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.clickChangeFor("Name shown to clients")

      WhatBusinessNamePage.assertPageIsDisplayed()
      WhatBusinessNamePage.selectSomethingElse()
      WhatBusinessNamePage.enterCustomName("Updated LLP Name")
      WhatBusinessNamePage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Name shown to clients", "Updated LLP Name")

    Scenario("Change Telephone Number from Check Your Answers page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .runToCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.clickChangeFor("Telephone number")

      WhatTelephoneNumberPage.assertPageIsDisplayed()
      WhatTelephoneNumberPage.selectSomethingElse()
      WhatTelephoneNumberPage.enterOtherTelephoneNumber("07777799999")
      WhatTelephoneNumberPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Telephone number", "07777799999")
