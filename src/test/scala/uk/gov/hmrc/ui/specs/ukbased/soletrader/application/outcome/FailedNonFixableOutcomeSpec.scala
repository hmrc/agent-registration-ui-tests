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

package uk.gov.hmrc.ui.specs.ukbased.soletrader.application.outcome

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
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.PageObject
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.AppConfig
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedNonFixableOutcomeSpec
extends BaseSpec:

  Feature("Applicant FailedNonFixable List Page"):
    Scenario(
      "Sole Trader Owner sees FailedNonFixable Outcome Page after sign in",
      TagSoleTrader,
    ):
      /** Step 1: Fast-forward to AgentStandards — same starting point as DeclarationSpec
       */
      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, SoleTrader)

      /** Step 2: Prove identity — populates the individuals array in the Mongo record
       */
      ProvideIndividualDetailsFlow
        .ProvideIndividualDetailsSoleTrader
        .runFlow(stubbedSignInData, listProgress.complete, fastForwardUsed = true)

      /** Step 3: Accept declaration — submits the application, creates record with ReadyForSubmission
       */
      DeclarationFlow.AcceptDeclaration.runFlow(SoleTrader, fastForwardUsed = true)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      /** Step 4: Capture reference and verify initial status */
      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      val submittedDoc = MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))
      MongoHelper.getTopLevelString(submittedDoc, "status") shouldBe "ReadyForSubmission"

      /** Step 5: Simulate the risking service — update status and set failure codess */
      MongoHelper.updateStatusWithFailures(applicationReference, "FailedNonFixable", MongoHelper.nonFixableFailures)

      /** Step 6: Sign in again using the same credentials from step 1.
       Navigate directly to the application-status page — the frontend reads the updated status from Mongo and shows the outcome page. */
      val applicationStatusUrl =
        AppConfig.baseUrlAgentRegistrationFrontend + ApplicationSubmittedPage.path
      val signInUrl =
        AppConfig.baseUrlGovernmentGateway +
          s"/bas-gateway/sign-in?continue_url=$applicationStatusUrl&origin=agent-registration-frontend&affinityGroup=agent"
      PageObject.get(signInUrl)
      GovernmentGatewaySignInPage.assertPageIsDisplayed()
      GovernmentGatewaySignInPage.enterKnownUserId(stubbedSignInData.username)
      GovernmentGatewaySignInPage.enterKnownPlanetId(stubbedSignInData.planetId)
      GovernmentGatewaySignInPage.clickContinue()

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("ST Name ST Lastname")

      /**Step 7: Assert the Mongo record reflects the correct status and failure codes */
      val outcomeDoc = MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))
      MongoHelper.getTopLevelString(outcomeDoc, "status") shouldBe "FailedNonFixable"
      MongoHelper.getTopLevelString(outcomeDoc, "entityType") shouldBe "SoleTrader"
      MongoHelper.getFailures(outcomeDoc) should not be empty
      MongoHelper.getFailures(outcomeDoc).map(f => MongoHelper.getNestedString(f, "type")) should contain allOf (
        "_3._1", "_3._2", "_4._1", "_4._2", "_7", "_8._1", "_8._5", "_8._6", "_8._7"
      )

    Scenario(
      "Sole Trader Non-Owner sees FailedNonFixable Outcome Page after sign in",
      TagSoleTrader,
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

      /** Step 4: Capture reference and verify initial status */
      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      val submittedDoc = MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))
      MongoHelper.getTopLevelString(submittedDoc, "status") shouldBe "ReadyForSubmission"

      /** Step 5: Simulate the risking service — update status and set failure codess */
      MongoHelper.updateStatusWithFailures(applicationReference, "FailedNonFixable", MongoHelper.nonFixableFailures)

      /** Step 6: Sign in again using the same credentials from step 1.
       * Navigate directly to the application-status page — the frontend reads the updated status from Mongo and shows the outcome page. */
      val applicationStatusUrl =
        AppConfig.baseUrlAgentRegistrationFrontend + ApplicationSubmittedPage.path
      val signInUrl =
        AppConfig.baseUrlGovernmentGateway +
          s"/bas-gateway/sign-in?continue_url=$applicationStatusUrl&origin=agent-registration-frontend&affinityGroup=agent"
      PageObject.get(signInUrl)
      GovernmentGatewaySignInPage.assertPageIsDisplayed()
      GovernmentGatewaySignInPage.enterKnownUserId(stubbedSignInData.username)
      GovernmentGatewaySignInPage.enterKnownPlanetId(stubbedSignInData.planetId)
      GovernmentGatewaySignInPage.clickContinue()

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("Test User")

      /**Step 7: Assert the Mongo record reflects the correct status and failure codes */
      val outcomeDoc = MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))
      MongoHelper.getTopLevelString(outcomeDoc, "status") shouldBe "FailedNonFixable"
      MongoHelper.getTopLevelString(outcomeDoc, "entityType") shouldBe "SoleTrader"
      MongoHelper.getFailures(outcomeDoc) should not be empty
      MongoHelper.getFailures(outcomeDoc).map(f => MongoHelper.getNestedString(f, "type")) should contain allOf (
        "_3._1", "_3._2", "_4._1", "_4._2", "_7", "_8._1", "_8._5", "_8._6", "_8._7"
      )


