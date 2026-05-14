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
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow.listProgress
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedNonFixableOutcomeSpec
extends BaseSpec:

  Feature("Applicant FailedNonFixable List Page"):
    Scenario(
      "Sole Trader Owner sees FailedNonFixable Outcome Page after sign in",
      TagSoleTrader
    ):
      pending // ff links aren't creating risk records so disabling until fixed
      /** Step 1: Fast-forward to AgentStandards — same starting point as DeclarationSpec
        */
      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, SoleTrader)

      /** Step 2: Prove identity — populates the individuals array in the Mongo record
        */
      ProvideIndividualDetailsFlow
        .ProvideIndividualDetailsSoleTrader
        .runFlow(
          stubbedSignInData,
          listProgress.complete,
          fastForwardUsed = true
        )

      /** Step 3: Accept declaration — submits the application, creates record with ReadyForSubmission
        */
      DeclarationFlow.AcceptDeclaration.runFlow(SoleTrader, fastForwardUsed = true)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      /** Step 4: Capture reference and verify document exists */
      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      /** Step 5: Simulate the risking service — set riskingFileName and entityRiskingResult on the application-for-risking record, and individualRiskingResult
        * (empty failures) on each individual-for-risking record.
        */
      MongoHelper.simulateNonFixableRiskingOutcome(applicationReference)

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("ST Name ST Lastname")

      /** Step 7: Assert the application-for-risking record has the risking fields set correctly */
      val outcomeDoc = MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))
      MongoHelper.getTopLevelString(outcomeDoc, "riskingFileName") shouldBe "any-old.txt"

      val failures = MongoHelper.getEntityRiskingFailures(outcomeDoc)
      failures should not be empty

      val failureTypes = failures.map(f => MongoHelper.getNestedString(f, "type"))
      failureTypes should contain allOf ("_7", "_4._1", "_5._1", "_8._1", "_8._4", "_8._5")

      val fivePointOne = failures
        .find(f => MongoHelper.getNestedString(f, "type") == "_5._1")
        .getOrElse(throw new AssertionError("No _5._1 failure found"))
      MongoHelper.getNestedInt(fivePointOne, "value") shouldBe 150

      ApplicationSubmittedPage.assertOutcomeDescriptionContainsAll(
        "our records show that the business is formally insolvent",
        "the business has missing tax returns in their HMRC record",
        "the business has unpaid tax liabilities",
        "the business appears on a published HMRC list of tax avoidance promoter, enablers or suppliers",
        "the business was issued with a relevant anti-avoidance penalty within the last 12 months",
        "the business has one or more relevant anti-avoidance penalties to pay"
      )

    Scenario(
      "Sole Trader Non-Owner sees FailedNonFixable Outcome Page after sign in",
      TagSoleTrader
    ):
      pending // db has been restructured so the mongo interactions need reworking
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

      ProvideIndividualDetailsFlow
        .ProvideIndividualDetailsSoleTraderOwner
        .runFlow(
          stubbedSignInData,
          ProvideIndividualDetailsFlow.listProgress.complete
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(SoleTrader, soleTraderOwner = false)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitle("You’ve applied for an agent services account")

      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      MongoHelper.simulateNonFixableRiskingOutcome(applicationReference)

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("Test User")

      val outcomeDoc = MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))
      MongoHelper.getTopLevelString(outcomeDoc, "riskingFileName") shouldBe "any-old.txt"

      val failures = MongoHelper.getEntityRiskingFailures(outcomeDoc)
      failures should not be empty

      val failureTypes = failures.map(f => MongoHelper.getNestedString(f, "type"))
      failureTypes should contain allOf ("_7", "_4._1", "_5._1", "_8._1", "_8._4", "_8._5")

      val fivePointOne = failures
        .find(f => MongoHelper.getNestedString(f, "type") == "_5._1")
        .getOrElse(throw new AssertionError("No _5._1 failure found"))
      MongoHelper.getNestedInt(fivePointOne, "value") shouldBe 150

      ApplicationSubmittedPage.assertOutcomeDescriptionContainsAll(
        "our records show that the business is formally insolvent",
        "the business has missing tax returns in their HMRC record",
        "the business has unpaid tax liabilities",
        "the business appears on a published HMRC list of tax avoidance promoter, enablers or suppliers",
        "the business was issued with a relevant anti-avoidance penalty within the last 12 months",
        "the business has one or more relevant anti-avoidance penalties to pay"
      )
