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
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.{ApplicationStatusPage, ConditionsNotMetTaskListPage, ConditionsNotYetMetEntityFailureDetailsV41Page}
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper
import uk.gov.hmrc.ui.utils.MongoHelper.IndividualRiskingOutcome

class FailedFixableOutcomeForApplicantFailureDetailsSpec
extends BaseSpec:

  Feature("Applicant Failure Details Page"):
    Scenario(
      "LLP user sees FailedFixable Details Page and click Yes to resolve the Self Assessment returns issue",
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
        fixes = Seq("EntityFix._4._1", "EntityFix._4._3"),
        riskingOutcomeEntityType = "FailedFixable"
      )

      // Insert two individuals. One with all actions confirmed, one with some actions unconfirmed
      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Steve Austin" -> IndividualRiskingOutcome(
            fixes = Seq.empty
          ),
          "Beverly Hills" -> IndividualRiskingOutcome(
            outcomeType = "Approved",
            fixes = Seq.empty
          )
        )
      )

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("Test Partnership")
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotMetTaskListPage.assertPageIsDisplayed()
      ConditionsNotMetTaskListPage.assertSelfAssessmentMissingReturns("Incomplete")
      ConditionsNotMetTaskListPage.clickOnSelfAssessmentReturnsLink()

      ConditionsNotYetMetEntityFailureDetailsV41Page.assertPageIsDisplayed()
      ConditionsNotYetMetEntityFailureDetailsV41Page.assertPageHeadingContains("Self Assessment returns for Test Partnership")
      ConditionsNotYetMetEntityFailureDetailsV41Page.selectYes()
      ConditionsNotYetMetEntityFailureDetailsV41Page.clickContinue()
      ConditionsNotMetTaskListPage.assertPageIsDisplayed()
      ConditionsNotMetTaskListPage.assertSelfAssessmentMissingReturns("Completed")
