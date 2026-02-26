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

package uk.gov.hmrc.ui.specs.ukbased.soletrader.application.viewapplication

import uk.gov.hmrc.ui.domain.BusinessType
import BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.viewapplication.ViewApplicationFlow
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ViewApplicationPage
import uk.gov.hmrc.ui.specs.BaseSpec

class ViewApplicationSpec
extends BaseSpec:

  Feature("View application after first stage"):
    Scenario(
      "User reviews application details",
      TagSoleTrader
    ):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(SoleTrader)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(SoleTrader)

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(BusinessType.SoleTrader)
      ApplicationSubmittedPage.clickViewOrPrintLink()

      ViewApplicationFlow
        .ViewApplication
        .runFlow()
      ViewApplicationPage.assertSummaryRow("UK-based agent", "Yes")
      ViewApplicationPage.assertSummaryRow("Business type", "Sole trader")
      ViewApplicationPage.assertSummaryRow("Are you the owner of the business?", "Yes")
      ViewApplicationPage.assertSummaryRow("Company name", "Test User")
      ViewApplicationPage.assertSummaryRowPresent("Unique taxpayer reference")
      ViewApplicationPage.assertSummaryRow("Name", "John Ian Tester")
      ViewApplicationPage.assertSummaryRow("Telephone number", "(+44) 10794554342")
      ViewApplicationPage.assertSummaryRow("Name shown to clients", "Test User")
      ViewApplicationPage.assertSummaryRow("Telephone number", "(+44) 10794554342")
      ViewApplicationPage.assertSummaryRow("Correspondence address", "1 Test Street\nTest Area\nTE1 1ST\nGB")
      ViewApplicationPage.assertSummaryRow("Supervisory body", "HM Revenue and Customs (HMRC)")
      ViewApplicationPage.assertSummaryRow("Registration number", "XAML00000123456")
      ViewApplicationPage.assertSummaryRow("Agreed to meet the HMRC standard for agents", "Yes")
