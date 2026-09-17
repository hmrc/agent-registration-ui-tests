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

import org.scalactic.Prettifier.default
import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.domain.BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.flows.common.application.viewapplication.ViewApplicationFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.PartnersTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow.listProgress.partial
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ViewApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.EntityFix_4_1Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ApplicationStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsCheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsEntityFailureV31Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantDeclarationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantTaskListPage
import uk.gov.hmrc.ui.specs.BaseSpec

class LimitedLiabilityPartnershipApplicationSpec
extends BaseSpec:

  Feature("View application after first stage"):
    Scenario(
      "User reviews application details and successfully resubmits after entity failures",
      TagFullSuite,
      TagRisking,
      TagSmokeTests
    ):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LLP)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LLP)

      val partnersNames = PartnersTaxAdvisorInformationFlow
        .multiplePartners
        .runFlowForLLP()

      val shareLink = ProvidePartnersDetailsFlow.getProvideDetailsLink

      /* Sign in first partner (partial - more partner to come) */
      ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          partial,
          Some(partnersNames.head),
          Some(partnersNames),
          hasUtr = true
        )

      /* Sign in second partner (complete - last partner) - reuse the same link */
      ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          complete,
          Some(partnersNames(1)),
          Some(partnersNames),
          hasUtr = true
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(LLP)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      ApplicationSubmittedPage.clickViewOrPrintLink()

      ViewApplicationFlow
        .ViewApplication
        .runFlow()
      ViewApplicationPage.assertSummaryRow("UK-based agent", "Yes")
      ViewApplicationPage.assertSummaryRow("Business type", "Limited liability partnership")
      ViewApplicationPage.assertSummaryRow("Are you a member of the limited liability partnership?", "No, but I’m authorised by them to set up this account")
      ViewApplicationPage.assertSummaryRow("Company name", "Test Partnership")
      ViewApplicationPage.assertSummaryRow("Name", "John Ian Tester")
      ViewApplicationPage.assertSummaryRow("Telephone number", "(+44) 10794554342")
      ViewApplicationPage.assertSummaryRow("Name shown to clients", "Test Partnership")
      ViewApplicationPage.assertSummaryRow("Telephone number", "(+44) 10794554342")
      ViewApplicationPage.assertSummaryRow("Correspondence address", "1 Test Street\nTest Area\nAA1 1AA\nGB")
      ViewApplicationPage.assertSummaryRow("Supervisory body", "HM Revenue and Customs (HMRC)")
      ViewApplicationPage.assertSummaryRow("Registration number", "XAML00000123456")
      ViewApplicationPage.assertSummaryRow("Agreed to meet the HMRC standard for agents", "Yes")

      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          Seq("3.1", "4.1"),
          Map(
            partnersNames.head -> SetRiskingOutcomesFlow.Approved,
            partnersNames(1) -> SetRiskingOutcomesFlow.Approved
          )
        )

      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Partnership has not met the registration conditions")

      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Declare and submit",
        "Cannot start yet"
      )

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Provide your supervision details again")
      ConditionsNotYetMetAmlsEntityFailureV31Page.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsEntityFailureV31Page.clickContinue()
      ConditionsNotYetMetAmlsCheckYourAnswersPage.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsCheckYourAnswersPage.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Self Assessment - missing returns")
      EntityFix_4_1Page.assertPageIsDisplayed()
      EntityFix_4_1Page.selectYes()
      EntityFix_4_1Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Declare and submit")
      ConditionsNotYetMetApplicantDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantDeclarationPage.clickContinue()

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.assertConfirmationTitle(
        "You have resubmitted your application for an agent services account"
      )
