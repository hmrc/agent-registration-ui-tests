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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.limited_liability_partnership.application.amlsdetails

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.amldetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.amldetails.WhatRegistrationNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.amldetails.WhatSupervisoryBodyPage
import uk.gov.hmrc.ui.specs.BaseSpec

class AmlsDetailsSpec
extends BaseSpec:

  Feature("Complete Anti-money laundering section"):
    Scenario("User selects HMRC as their Supervisory Body", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()
      TaskListPage.assertAmlsDetailsStatus("Completed")

    Scenario("User selects non-HMRC Supervisory Body", HappyPath):
      pending
      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData)

      AmlsDetailsFlow
        .WhenNonHmrcSupervisoryBody
        .runFlow()
      TaskListPage.assertAmlsDetailsStatus("Completed")

    Scenario("Changes Registration Number from CYA page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData)

      AmlsDetailsFlow
        .RunToCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.clickChangeFor("Registration number")

      WhatRegistrationNumberPage.assertPageIsDisplayed()
      WhatRegistrationNumberPage.enterRegistrationNumber("XAML00000111111")
      WhatRegistrationNumberPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Registration number", "XAML00000111111")

    Scenario("Changes Supervisory Body from CYA page", HappyPath):
      pending
      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData)

      AmlsDetailsFlow
        .RunToCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.clickChangeFor("Supervisory body")

      WhatSupervisoryBodyPage.assertPageIsDisplayed()
      // TODO Steps to select non hmrc supervisory body
