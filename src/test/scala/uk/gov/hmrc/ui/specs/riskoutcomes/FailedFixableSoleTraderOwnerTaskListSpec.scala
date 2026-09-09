/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.ui.specs.riskoutcomes

import uk.gov.hmrc.ui.domain.BusinessType.SoleTrader
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.PageObject.getCurrentUrl
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.*
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualsPage.ActionRow
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.ConditionsNotYetMetSoleTraderCheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.EntityFix_4_1Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualFix_8_7Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.SoleTraderFailureDetailsEntityFix_4_1Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.SoleTraderFailureDetailsEntityFix_4_3Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.SoleTraderFailureDetailsIndividualFix_8_7Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.SoleTraderIdentityPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedFixableSoleTraderOwnerTaskListSpec
extends BaseSpec:

  Feature("Sole Trader Owner FailedFixable Tasklist - Resubmission and Non-Owner Access Restrictions"):
    Scenario(
      "Complete FailedFixable actions and resubmit without individual link sign in",
      TagFullSuite,
      TagRisking
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, SoleTrader)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          Seq(
            "3.1",
            "4.1",
            "4.3"
          ),
          Seq("8.7", "10.1")
        )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.clickViewActionsToTakeButton()

      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Provide your supervision details again")
      ConditionsNotYetMetAmlsEntityFailureV31Page.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsEntityFailureV31Page.clickContinue()
      ConditionsNotYetMetAmlsCheckYourAnswersPage.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsCheckYourAnswersPage.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("File your missing VAT returns")
      SoleTraderFailureDetailsEntityFix_4_3Page.assertPageIsDisplayed()
      SoleTraderFailureDetailsEntityFix_4_3Page.selectYes()
      SoleTraderFailureDetailsEntityFix_4_3Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Pay your relevant anti-avoidance penalty liability")
      SoleTraderFailureDetailsIndividualFix_8_7Page.assertPageIsDisplayed()
      SoleTraderFailureDetailsIndividualFix_8_7Page.selectYes()
      SoleTraderFailureDetailsIndividualFix_8_7Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Provide more details to prove your identity")
      SoleTraderIdentityPage.assertPageIsDisplayed()
      SoleTraderIdentityPage.clickContinue()
      ConditionsNotYetMetSoleTraderCheckYourAnswersPage.assertPageIsDisplayed()
      ConditionsNotYetMetSoleTraderCheckYourAnswersPage.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("File your missing Self Assessment returns")
      SoleTraderFailureDetailsEntityFix_4_1Page.assertPageIsDisplayed()
      SoleTraderFailureDetailsEntityFix_4_1Page.selectYes()
      SoleTraderFailureDetailsEntityFix_4_1Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus("Declare and submit", "Incomplete")
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Declare and submit")
      ConditionsNotYetMetApplicantDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantDeclarationPage.clickContinue()

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.assertConfirmationTitle("You have resubmitted your application for an agent services account")

    Scenario(
      "Sole Trader applicant who is not the Owner cannot confirm individual fixable failures",
      TagFullSuite,
      TagRisking
    ):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow(false)

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingCustomValues
        .runFlow(stubbedSignInData)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(
          SoleTrader,
          false,
          "Test User"
        )

      val (_, individualUsername) = ProvideIndividualDetailsFlow
        .ProvideIndividualDetailsSoleTraderOwner
        .runFlowWithUsername(
          stubbedSignInData,
          ProvideIndividualDetailsFlow.listProgress.complete
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(SoleTrader, soleTraderOwner = false)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          Seq("4.1"),
          Seq("8.7")
        )

      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Self Assessment - missing returns")
      EntityFix_4_1Page.assertPageIsDisplayed()
      EntityFix_4_1Page.selectYes()
      EntityFix_4_1Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      // Non-owner applicant must not be able to confirm individual action directly
      ConditionsNotYetMetApplicantTaskListPage.assertActionNotDisplayed("Pay a liability connected to relevant anti-avoidance")
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus("Declare and submit", "Cannot start yet")

      // They can only view individual outstanding actions
      ConditionsNotYetMetApplicantTaskListPage.clickIndividualFailuresLink()
      ConditionsNotYetMetIndividualsPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualsPage.assertActionsRow(
        ActionRow(
          name = "Test User",
          actions = Seq("Pay a liability connected to relevant anti-avoidance"),
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

      ConditionsNotYetMetIndividualTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Pay your relevant anti-avoidance penalty liability",
        "Incomplete"
      )

      ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
        "Pay your relevant anti-avoidance penalty liability"
      )
      IndividualFix_8_7Page.assertPageIsDisplayed()
      IndividualFix_8_7Page.selectYes()
      IndividualFix_8_7Page.clickContinue()

      ConditionsNotYetMetIndividualTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Pay your relevant anti-avoidance penalty liability",
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
      ConditionsNotYetMetConfirmationPage.assertConfirmationTitle("You have finished this process")

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
