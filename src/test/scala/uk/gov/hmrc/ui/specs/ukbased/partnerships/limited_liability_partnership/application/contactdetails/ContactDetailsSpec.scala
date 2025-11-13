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

import uk.gov.hmrc.ui.flows
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.pages
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.businessdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.specs.BaseSpec

class ContactDetailsSpec
extends BaseSpec:

  Feature("Complete Contact Details section"):
    Scenario("The Companies House responds with multiple name matches for the applicant's name query", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)
      TaskListPage.assertContactDetailsStatus("Completed")

    Scenario("The Companies House responds with one exact match for the applicant's name query", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      ContactDetailsFlow
        .WhenMultiNameMatch
        .runFlow(stubbedSignInData)
      TaskListPage.assertContactDetailsStatus("Completed")

    Scenario("The Companies House responds with no matches for the applicant's name query", HappyPath):
      pending
      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()

      // TODO acutal test here

      TaskListPage.assertContactDetailsStatus("Completed")

    Scenario("Change X from CYA page ", HappyPath):
      pending
      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()
      ContactDetailsFlow
        .WhenOnlyOneNameMatch
        .runFlowUntilCyaPage(stubbedSignInData)

      CheckYourAnswersPage.assertPageIsDisplayed()
//      CheckYourAnswersPage.clickChangeX(...) TODO

      TaskListPage.assertContactDetailsStatus("Completed")

    Scenario("Change Y from CYA page ", HappyPath):
      pending
      val stubbedSignInData = BusinessDetailsFlow
        .WhenHasNoOnlineAgentAccount
        .runFlow()
      ContactDetailsFlow
        .WhenOnlyOneNameMatch
        .runFlowUntilCyaPage(stubbedSignInData)

      CheckYourAnswersPage.assertPageIsDisplayed()
//      CheckYourAnswersPage.clickChangeY(...) TODO

      TaskListPage.assertContactDetailsStatus("Completed")
