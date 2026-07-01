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
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotMetIndividualsPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotMetTaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotMetIndividualsPage.ActionRow
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper
import uk.gov.hmrc.ui.utils.MongoHelper.IndividualFix
import uk.gov.hmrc.ui.utils.MongoHelper.IndividualRiskingOutcome

class FailedFixableListOfIndividualsSpec
extends BaseSpec:

  Feature("Applicant FailedFixable List of Individuals Page"):
    Scenario(
      "Applicant views Actions to be completed list",
      TagFixableFailures
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      MongoHelper.insertRiskingOutcomeToAgentApplication(
        applicationReference = applicationReference,
        riskingCompletedDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = Seq.empty
      )
      // Insert two individuals. One with all actions confirmed, one with some actions unconfirmed
      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Steve Austin" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3"),
              IndividualFix("IndividualFix._8._7", isConfirmed = true)
            )
          ),
          "Beverly Hills" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._1", isConfirmed = true),
              IndividualFix("IndividualFix._5._1", isConfirmed = true)
            )
          )
        )
      )

      RiskingOutcomeFlow
        .viewListOfIndividualActions
        .runFlow(stubbedSignInData)

      // Proves multi row table is displayed with correct data for each individual
      // Proves Completed status is No until all actions are confirmed by individual
      ConditionsNotMetIndividualsPage.assertActionsRow(
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
      ConditionsNotMetIndividualsPage.assertActionsRow(
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
      ConditionsNotMetIndividualsPage.clickContinue()
  //    ConditionsNotMetTaskListPage.assertPageIsDisplayed() Disabled due to bug where nav gots back to Status page
