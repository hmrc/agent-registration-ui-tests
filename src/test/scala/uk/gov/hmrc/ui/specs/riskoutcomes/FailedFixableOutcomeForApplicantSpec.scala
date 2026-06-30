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

import uk.gov.hmrc.ui.domain.BusinessType.GeneralPartnership
import uk.gov.hmrc.ui.domain.BusinessType.SoleTrader
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedFixableOutcomeForApplicantSpec
extends BaseSpec:

  Feature("Applicant FailedFixable List Page"):
    Scenario(
      "Sole Trader Owner sees FailedFixable Outcome Page after sign in",
      TagFixableFailures
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(Declaration, SoleTrader)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      // Insert risking outcome data into the backend agent-application collection
      MongoHelper.insertRiskingOutcomeToAgentApplication(
        applicationReference = applicationReference,
        riskingCompletedDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = Seq("EntityFix._4._2")
      )

      MongoHelper.syncRiskingIndividualsToBackEnd(applicationReference)

      val riskingIndividuals = MongoHelper.findRiskingIndividualsByApplicationReference(applicationReference)
      riskingIndividuals should not be empty

      riskingIndividuals.foreach { riskingIndividual =>
        MongoHelper.insertRiskingOutcomeIndividual(applicationReference, riskingIndividual)
      }

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("ST Name ST Lastname")
      ApplicationSubmittedPage.assertConfirmationTitleHeading("ST Name ST Lastname does not meet the registration conditions yet")

    Scenario(
      "General Partnership sees FailedFixable Outcome Page when partner have individual failures",
      TagFixableFailures
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(Declaration, GeneralPartnership)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      // Insert risking outcome data into the backend agent-application collection
      MongoHelper.insertRiskingOutcomeToAgentApplication(
        applicationReference = applicationReference,
        riskingCompletedDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = Seq("EntityFix._4._1", "EntityFix._4._3")
      )

      MongoHelper.syncRiskingIndividualsToBackEnd(applicationReference)

      val riskingIndividuals = MongoHelper.findRiskingIndividualsByApplicationReference(applicationReference)
      riskingIndividuals should not be empty

      riskingIndividuals.foreach { riskingIndividual =>
        MongoHelper.insertRiskingOutcomeIndividual(applicationReference, riskingIndividual)
      }

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("Electronicsson Group")
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Electronicsson Group does not meet the registration conditions yet")
