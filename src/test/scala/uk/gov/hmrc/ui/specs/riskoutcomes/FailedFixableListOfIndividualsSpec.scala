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

import uk.gov.hmrc.ui.domain.BusinessType.LLP
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantTaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualsPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualsPage.ActionRow
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedFixableListOfIndividualsSpec
extends BaseSpec:

  Feature("Applicant FailedFixable List of Individuals Page"):
    Scenario(
      "Applicant views Actions to be completed list",
      TagFullSuite,
      TagRisking
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      SetRiskingOutcomesFlow.runFlow(
        applicationReference,
        SetRiskingOutcomesFlow.ApplicantApproved,
        Map(
          "Steve Austin" -> SetRiskingOutcomesFlow.Failures(Seq("4.3", "8.7")),
          "Beverly Hills" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1"))
        )
      )

      MongoHelper.confirmRiskingOutcomeIndividualFixes(
        applicationReference = applicationReference,
        fixTypesByIndividualName = Map(
          "Steve Austin" -> Seq("IndividualFix._4._3"),
          "Beverly Hills" -> Seq("IndividualFix._4._1", "IndividualFix._5._1")
        )
      )

      RiskingOutcomeFlow
        .viewListOfIndividualActionsViaStub
        .runFlow(applicationReference)

      ConditionsNotYetMetIndividualsPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualsPage.assertActionsRow(
        ActionRow(
          name = "Steve Austin",
          actions = Seq(
            "File one or more relevant returns",
            "Pay a liability connected to relevant anti-avoidance"
          ),
          completed = "No"
        )
      )
      // Proves Completed status is Yes once all actions are confirmed by individual
      ConditionsNotYetMetIndividualsPage.assertActionsRow(
        ActionRow(
          name = "Beverly Hills",
          actions = Seq(
            "File one or more relevant returns",
            "Pay one or more overdue liabilities"
          ),
          completed = "Yes"
        )
      )

      // Proves return to task list button works and returns to the task list page
      ConditionsNotYetMetIndividualsPage.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

    Scenario(
      "Applicant provided some of the individuals details",
      TagFullSuite,
      TagRisking
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      SetRiskingOutcomesFlow.runFlow(
        applicationReference,
        SetRiskingOutcomesFlow.ApplicantApproved,
        Map(
          "Steve Austin" -> SetRiskingOutcomesFlow.Failures(Seq("4.3", "8.7")),
          "Beverly Hills" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1"))
        )
      )

      MongoHelper.confirmRiskingOutcomeIndividualFixes(
        applicationReference = applicationReference,
        fixTypesByIndividualName = Map(
          "Steve Austin" -> Seq("IndividualFix._8._7"),
          "Beverly Hills" -> Seq("IndividualFix._4._1", "IndividualFix._5._1")
        )
      )

      MongoHelper.setProvidedByApplicantForIndividual(
        applicationReference = applicationReference,
        individualName = "Beverly Hills"
      )

      RiskingOutcomeFlow
        .viewListOfIndividualActionsViaStub
        .runFlow(applicationReference)

      // Proves fixable failure for individual with providedByApplicant = false displays
      ConditionsNotYetMetIndividualsPage.assertActionsRow(
        ActionRow(
          name = "Steve Austin",
          actions = Seq(
            "File one or more relevant returns",
            "Pay a liability connected to relevant anti-avoidance"
          ),
          completed = "No"
        )
      )
      // Proves fixable failure for individual with providedByApplicant = true doesn't display
      ConditionsNotYetMetIndividualsPage.assertIndividualNotDisplayed("Beverly Hills")
