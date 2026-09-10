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

package uk.gov.hmrc.ui.specs.riskoutcomes

import uk.gov.hmrc.ui.domain.BusinessType.LLP
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ApplicationStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantTaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.SaveAndComeBackLaterPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedFixableOutcomeForApplicantFailureTasklistSpec
extends BaseSpec:

  Feature("Applicant Task List Page"):
    Scenario(
      "Applicant actions are incomplete on task list",
      TagFullSuite,
      TagRisking
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

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
          Seq(
            SetRiskingOutcomesFlow.Approved,
            SetRiskingOutcomesFlow.Approved
          )
        )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      // Click the "View actions to take" button to navigate to the Conditions Not Met Task List page
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Partnership has not met the registration conditions")

      // verify actions and their status
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "VAT - missing returns",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Declare and submit",
        "Cannot start yet"
      )

      // Click the "Save and Come Back Later" button to navigate to the Save and Come Back Later page
      ConditionsNotYetMetApplicantTaskListPage.clickSaveAndComeBackLaterButton()
      SaveAndComeBackLaterPage.assertPageIsDisplayed()

    Scenario(
      "Applicant actions are complete, but individual action is incomplete on task list",
      TagFullSuite,
      TagRisking
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

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
          Map(
            "Steve Austin" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1")),
            "Beverly Hills" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1"))
          )
        )

      MongoHelper.confirmRiskingOutcomeApplicantFixes(
        applicationReference = applicationReference,
        fixTypesToConfirm = Seq(
          "EntityFix._3.AmlsFix",
          "EntityFix._4._1",
          "EntityFix._4._3"
        )
      )

      MongoHelper.confirmRiskingOutcomeIndividualFixes(
        applicationReference = applicationReference,
        fixTypesByIndividualName = Map(
          "Steve Austin" -> Seq("IndividualFix._4._1"),
          "Beverly Hills" -> Seq("IndividualFix._4._1", "IndividualFix._5._1")
        )
      )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      // Click the "View actions to take" button to navigate to the Conditions Not Met Task List page
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Partnership has not met the registration conditions")

      // verify actions and their status
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "VAT - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "We are awaiting information from these people",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Declare and submit",
        "Cannot start yet"
      )

    Scenario(
      "Applicant action is incomplete, but individual action is complete on task list",
      TagFullSuite,
      TagRisking
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

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
          Map(
            "Steve Austin" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1")),
            "Beverly Hills" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1"))
          )
        )

      MongoHelper.confirmRiskingOutcomeApplicantFixes(
        applicationReference = applicationReference,
        fixTypesToConfirm = Seq(
          "EntityFix._3.AmlsFix",
          "EntityFix._4._3"
        )
      )

      MongoHelper.confirmRiskingOutcomeIndividualFixes(
        applicationReference = applicationReference,
        fixTypesByIndividualName = Map(
          "Steve Austin" -> Seq("IndividualFix._4._1", "IndividualFix._5._1"),
          "Beverly Hills" -> Seq("IndividualFix._4._1", "IndividualFix._5._1")
        )
      )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      // Click the "View actions to take" button to navigate to the Conditions Not Met Task List page
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Partnership has not met the registration conditions")

      // verify actions and their status
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "VAT - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "We are awaiting information from these people",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Declare and submit",
        "Cannot start yet"
      )

    Scenario(
      "Both applicant and individual actions are complete on task list",
      TagFullSuite,
      TagRisking
    ):
      FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

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
          Map(
            "Steve Austin" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1")),
            "Beverly Hills" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1"))
          )
        )

      MongoHelper.confirmRiskingOutcomeApplicantFixes(
        applicationReference = applicationReference,
        fixTypesToConfirm = Seq(
          "EntityFix._3.AmlsFix",
          "EntityFix._4._1",
          "EntityFix._4._3"
        )
      )

      MongoHelper.confirmRiskingOutcomeIndividualFixes(
        applicationReference = applicationReference,
        fixTypesByIndividualName = Map(
          "Steve Austin" -> Seq("IndividualFix._4._1", "IndividualFix._5._1"),
          "Beverly Hills" -> Seq("IndividualFix._4._1", "IndividualFix._5._1")
        )
      )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      // Click the "View actions to take" button to navigate to the Conditions Not Met Task List page
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Partnership has not met the registration conditions")

      // verify actions and their status
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "VAT - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "We are awaiting information from these people",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Declare and submit",
        "Incomplete"
      )
