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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.limited_liability_partnership.application.viewapplication

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.viewapplication.ViewApplicationFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ViewApplicationPage
import uk.gov.hmrc.ui.specs.BaseSpec

class ViewApplicationSpec
extends BaseSpec:

  Feature("View application after first stage"):
    Scenario("User reviews application details", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow()

      DeclarationFlow
        .AcceptDeclaration
        .runFlow()
      ApplicationSubmittedPage.clickViewOrPrintLink()

      ViewApplicationFlow
        .ViewApplication
        .runFlow()
      ViewApplicationPage.assertSummaryRow("UK-based agent", "Yes")
      ViewApplicationPage.assertSummaryRow("Business type", "Limited liability partnership")
      ViewApplicationPage.assertSummaryRow("Are you a member of the limited liability partnership?", "No, but I’m authorised by them to set up this account")
      ViewApplicationPage.assertSummaryRow("Company name", "Test Partnership")
      ViewApplicationPage.assertSummaryRow("Name", "John Ian Tester")
      ViewApplicationPage.assertSummaryRow("Telephone number", "07777777777")
      ViewApplicationPage.assertSummaryRow("Name shown to clients", "Test Partnership")
      ViewApplicationPage.assertSummaryRow("Telephone number", "07777777777")
      ViewApplicationPage.assertSummaryRow("Correspondence address", "23 Great Portland Street\nLondon\nW1 1AQ\nUnited Kingdom")
      ViewApplicationPage.assertSummaryRow("Supervisory body", "HM Revenue and Customs (HMRC)")
      ViewApplicationPage.assertSummaryRow("Registration number", "XAML00000123456")
      ViewApplicationPage.assertSummaryRow("Agreed to meet the HMRC standard for agents", "Yes")
