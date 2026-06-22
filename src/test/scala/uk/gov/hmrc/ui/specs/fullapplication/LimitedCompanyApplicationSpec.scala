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

package uk.gov.hmrc.ui.specs.fullapplication

import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.domain.BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.viewapplication.ViewApplicationFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.DirectorTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow.listProgress.partial
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ViewApplicationPage
import uk.gov.hmrc.ui.specs.BaseSpec

class LimitedCompanyApplicationSpec
extends BaseSpec:

  Feature("View application after first stage"):
    Scenario(
      "User reviews application details",
      TagSmokeTests
    ):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LimitedCompany)

      val directorNames = DirectorTaxAdvisorInformationFlow
        .multipleDirectors
        .runFlow()

      /* Get the share link once */
      val shareLink = ProvideDirectorDetailsFlow.getProvideDetailsLink

      /* Sign in first director (partial - more directors to come) */
      ProvideDirectorDetailsFlow
        .ProvideDirectorDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          partial,
          Some(directorNames.head),
          Some(directorNames)
        )

      /* Sign in second director (complete - last director) - reuse the same link */
      ProvideDirectorDetailsFlow
        .ProvideDirectorDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          complete,
          Some(directorNames(1)),
          Some(directorNames)
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(LimitedCompany)
      ApplicationSubmittedPage.clickViewOrPrintLink()

      ViewApplicationFlow
        .ViewApplication
        .runFlow()
      ViewApplicationPage.assertSummaryRow("UK-based agent", "Yes")
      ViewApplicationPage.assertSummaryRow("Business type", "Limited company")
      ViewApplicationPage.assertSummaryRow("Are you a director of the limited company?", "Yes, I’m a current officer in Companies House")
      ViewApplicationPage.assertSummaryRow("Company name", "Test Company Ltd")
      ViewApplicationPage.assertSummaryRow("Name", "John Ian Tester")
      ViewApplicationPage.assertSummaryRow("Telephone number", "(+44) 10794554342")
      ViewApplicationPage.assertSummaryRow("Name shown to clients", "Test Company Ltd")
      ViewApplicationPage.assertSummaryRow("Telephone number", "(+44) 10794554342")
      ViewApplicationPage.assertSummaryRow("Correspondence address", "1 Test Street\nTest Area\nTE1 1ST\nGB")
      ViewApplicationPage.assertSummaryRow("Supervisory body", "HM Revenue and Customs (HMRC)")
      ViewApplicationPage.assertSummaryRow("Registration number", "XAML00000123456")
      ViewApplicationPage.assertSummaryRow("Agreed to meet the HMRC standard for agents", "Yes")
