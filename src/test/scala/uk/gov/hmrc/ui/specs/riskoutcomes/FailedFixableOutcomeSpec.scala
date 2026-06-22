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

package uk.gov.hmrc.ui.specs.ukbased.soletrader.riskingresults.riskoutcome

import uk.gov.hmrc.ui.domain.BusinessType.SoleTrader
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow.listProgress
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedFixableOutcomeSpec
extends BaseSpec:

  Feature("Applicant FailedFixable List Page"):
    Scenario(
      "Sole Trader Owner sees FailedFixable Outcome Page after sign in",
      TagSoleTrader
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, SoleTrader)

      ProvideIndividualDetailsFlow
        .ProvideIndividualDetailsSoleTrader
        .runFlow(
          stubbedSignInData,
          listProgress.complete,
          fastForwardUsed = true
        )

      DeclarationFlow.AcceptDeclaration.runFlow(SoleTrader, fastForwardUsed = true)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      MongoHelper.simulateFixableRiskingOutcome(applicationReference)

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("ST Name ST Lastname")

      val outcomeDoc = MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))
      MongoHelper.getTopLevelString(outcomeDoc, "riskingFileName") shouldBe "any-old.txt"

      val failures = MongoHelper.getEntityRiskingFailures(outcomeDoc)
      println(s"Failures: $failures")
      failures should not be empty

      val failureTypes = failures.map(f => MongoHelper.getNestedString(f, "type"))
      failureTypes should contain allOf ("_4._1", "_4._3")

      ApplicationSubmittedPage.assertOutcomeDescriptionContainsAll(
        "ST Name ST Lastname will not be given an agent services account on this occasion",
        "the application will be deleted on 3 August 2026 to comply with our data retention policy"
      )
