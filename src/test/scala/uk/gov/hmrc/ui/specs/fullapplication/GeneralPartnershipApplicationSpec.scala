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
import uk.gov.hmrc.ui.flows.common.application.partnerInformation.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.flows.common.application.viewapplication.ViewApplicationFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.general_partnership.businessdetails.application.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ViewApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualsPage.ActionRow
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualFix_4_1Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualFix_5_1Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ApplicationStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantDeclarationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantTaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetConfirmationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualDeclarationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualTaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualsPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class GeneralPartnershipApplicationSpec
extends BaseSpec:

  Feature("View application after first stage"):
    Scenario(
      "General Partnership applicant reviews application and resubmits after individual completes fixable failures",
      TagSmokeTests,
      TagFullSuite,
      TagRisking
    ):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(GeneralPartnership)

      PartnerTaxAdvisorInformationFlow
        .singlePartner
        .runFlow()

      val individualUsername: String = ProvideIndividualDetailsFlow
        .ProvideIndividualDetails
        .runFlowWithUsername(
          stubbedSignInData,
          complete,
          GeneralPartnership
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(GeneralPartnership)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      ApplicationSubmittedPage.clickViewOrPrintLink()

      ViewApplicationFlow
        .ViewApplication
        .runFlow()
      ViewApplicationPage.assertSummaryRow("UK-based agent", "Yes")
      ViewApplicationPage.assertSummaryRow("Business type", "General partnership")
      ViewApplicationPage.assertSummaryRow("Are you a partner in the business?", "No, but I’m authorised by them to set up this account")
      ViewApplicationPage.assertSummaryRow("Partnership name", "Electronicsson Group")
      ViewApplicationPage.assertSummaryRow("Name", "John Ian Tester")
      ViewApplicationPage.assertSummaryRow("Telephone number", "(+44) 10794554342")
      ViewApplicationPage.assertSummaryRow("Name shown to clients", "Electronicsson Group")
      ViewApplicationPage.assertSummaryRow("Telephone number", "(+44) 10794554342")
      ViewApplicationPage.assertSummaryRow("Correspondence address", "1 Test Street\nTest Area\nAA1 1AA\nGB")
      ViewApplicationPage.assertSummaryRow("Supervisory body", "HM Revenue and Customs (HMRC)")
      ViewApplicationPage.assertSummaryRow("Registration number", "XAML00000123456")
      ViewApplicationPage.assertSummaryRow("Agreed to meet the HMRC standard for agents", "Yes")

      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          SetRiskingOutcomesFlow.ApplicantApproved,
          Map(
            "Bobby Boucher" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1"))
          )
        )

      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.clickIndividualFailuresLink()

      ConditionsNotYetMetIndividualsPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualsPage.assertActionsRow(
        ActionRow(
          name = "Bobby Boucher",
          actions = Seq(
            "File one or more relevant returns",
            "Pay one or more overdue liabilities"
          ),
          completed = "No"
        )
      )
      ConditionsNotYetMetIndividualsPage.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus("Declare and submit", "Cannot start yet")

      val linkId: String = MongoHelper.getLinkIdByApplicationReference(applicationReference)

      RiskingOutcomeFlow
        .viewIndividualTaskListPage
        .runFlow(
          stubbedSignInData,
          linkId,
          individualUsername
        )

      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "File your missing Self Assessment returns",
        "Incomplete"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Pay your Self Assessment liability",
        "Incomplete"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Confirm your responses are final",
        "Cannot start yet"
      )

      ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
        "File your missing Self Assessment returns"
      )
      IndividualFix_4_1Page.assertPageIsDisplayed()
      IndividualFix_4_1Page.selectYes()
      IndividualFix_4_1Page.clickContinue()

      ConditionsNotYetMetIndividualTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "File your missing Self Assessment returns",
        "Completed"
      )

      ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
        "Pay your Self Assessment liability"
      )
      IndividualFix_5_1Page.assertPageIsDisplayed()
      IndividualFix_5_1Page.selectYes()
      IndividualFix_5_1Page.clickContinue()

      ConditionsNotYetMetIndividualTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Pay your Self Assessment liability",
        "Completed"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Confirm your responses are final",
        "Incomplete"
      )

      ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
        "Confirm your responses are final"
      )
      ConditionsNotYetMetIndividualDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualDeclarationPage.clickContinue()
      ConditionsNotYetMetConfirmationPage.assertPageIsDisplayed()
      ConditionsNotYetMetConfirmationPage.assertConfirmationTitle(
        "You have finished this process"
      )

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus("Declare and submit", "Incomplete")
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Declare and submit")
      ConditionsNotYetMetApplicantDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantDeclarationPage.clickContinue()
      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.assertConfirmationTitle("You have resubmitted your application for an agent services account")
