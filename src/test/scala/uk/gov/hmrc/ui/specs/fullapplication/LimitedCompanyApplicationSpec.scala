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
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.flows.common.application.viewapplication.ViewApplicationFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.DirectorTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow.listProgress.partial
import uk.gov.hmrc.ui.pages.PageObject.getCurrentUrl
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ViewApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualsPage.ActionRow
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualFix_4_1Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ApplicationStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsCheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsEntityFailureV31Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantDeclarationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantTaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetConfirmationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualDeclarationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualTaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualsPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class LimitedCompanyApplicationSpec
extends BaseSpec:

  Feature("View application after first stage"):
    Scenario(
      "User resubmits application following entity and individual risking failures",
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
      val firstDirectorUsername: String = ProvideDirectorDetailsFlow
        .ProvideDirectorDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          partial,
          Some(directorNames.head),
          Some(directorNames)
        )

      /* Sign in second director (complete - last director) - reuse the same link */
      val secondDirectorUsername: String = ProvideDirectorDetailsFlow
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

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

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

      // Initial risk result: Entity Failures + Individual Failures
      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          Seq("3.1"),
          Map(
            directorNames.head -> SetRiskingOutcomesFlow.Failures(Seq("4.1")),
            directorNames(1) -> SetRiskingOutcomesFlow.Failures(Seq("4.1"))
          )
        )

      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Company Ltd does not meet the registration conditions yet")
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Company Ltd has not met the registration conditions")

      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "We are awaiting information from these people",
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

      ConditionsNotYetMetApplicantTaskListPage.clickIndividualFailuresLink()
      ConditionsNotYetMetIndividualsPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualsPage.assertActionsRow(
        ActionRow(
          name = "Steve Austin",
          actions = Seq("File one or more relevant returns"),
          completed = "No"
        )
      )
      ConditionsNotYetMetIndividualsPage.assertActionsRow(
        ActionRow(
          name = "Beverly Hills",
          actions = Seq("File one or more relevant returns"),
          completed = "No"
        )
      )
      ConditionsNotYetMetIndividualsPage.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus("Declare and submit", "Cannot start yet")
      ConditionsNotYetMetApplicantTaskListPage.clickSignOutLink()

      val linkId: String = MongoHelper.getLinkIdByApplicationReference(applicationReference)

      // Each director signs in and completes their fixable failures
      Seq(firstDirectorUsername, secondDirectorUsername).foreach { directorUsername =>
        RiskingOutcomeFlow
          .viewIndividualTaskListPage
          .runFlow(
            stubbedSignInData,
            linkId,
            directorUsername
          )

        ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
          "File your missing Self Assessment returns",
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
      }
      RiskingOutcomeFlow.SignInAsApplicantAfterRiskingOutcome.runFlow(stubbedSignInData)

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus("Declare and submit", "Incomplete")
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Declare and submit")
      ConditionsNotYetMetApplicantDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantDeclarationPage.clickContinue()
      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.assertConfirmationTitle("You have resubmitted your application for an agent services account")
