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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.general_partnership.riskingResults.riskOutcome

import uk.gov.hmrc.ui.domain.BusinessType.GeneralPartnership
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.partnerInformation.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.general_partnership.businessdetails.application.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedNonFixableOutcomeForApplicantSpec
extends BaseSpec:

  Feature("Applicant FailedNonFixable List Page"):
    Scenario(
      "General Partnership sees FailedNonFixable Outcome Page when both partners have individual failures",
      TagGeneralPartnership
    ):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(GeneralPartnership)

      PartnerTaxAdvisorInformationFlow
        .singlePartner
        .runFlow()

      ProvideIndividualDetailsFlow
        .ProvideIndividualDetails
        .runFlow(
          stubbedSignInData,
          complete,
          GeneralPartnership
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(GeneralPartnership)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      ApplicationSubmittedPage.assertConfirmationTitle(
        "You’ve applied for an agent services account"
      )

      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      MongoHelper.simulateNonFixableRiskingOutcome(applicationReference, withIndividualFailures = true)

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("Electronicsson Group")

      val outcomeDoc = MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))
      MongoHelper.getTopLevelString(outcomeDoc, "riskingFileName") shouldBe "any-old.txt"

      val entityFailures = MongoHelper.getEntityRiskingFailures(outcomeDoc)
      entityFailures should not be empty
      val entityFailureTypes = entityFailures.map(f => MongoHelper.getNestedString(f, "type"))
      entityFailureTypes should contain allOf ("_7", "_3._1", "_4._1", "_5._1", "_8._1", "_8._4", "_8._5", "_8._6", "_8._7")

      val individuals = MongoHelper.findIndividualsByApplicationReference(applicationReference)
      individuals should have size 1

      individuals.foreach { indDoc =>
        val indFailures = MongoHelper.getIndividualRiskingFailures(indDoc)
        indFailures should not be empty
        val indFailureTypes = indFailures.map(f => MongoHelper.getNestedString(f, "type"))
        indFailureTypes should contain allOf (
          "_4._1",
          "_5._1",
          "_6",
          "_7",
          "_8._1",
          "_8._6",
          "_8._7",
          "_9"
        )
        val fivePointOne = indFailures
          .find(f => MongoHelper.getNestedString(f, "type") == "_5._1")
          .getOrElse(throw new AssertionError("No _5._1 failure found on individual"))
      }

      ApplicationSubmittedPage.assertOutcomeDescriptionContainsAll(
        "our records show that the business is formally insolvent",
        "the business was issued with a relevant anti-avoidance penalty within the last 12 months",
        "the business appears on a published HMRC list of tax avoidance promoter, enablers or suppliers",
        "the business has missing tax returns in their HMRC record",
        "we could not confirm the anti-money laundering supervision for Electronicsson Group based on the information you provided",
        "the business has unpaid tax liabilities",
        "the business has one or more relevant anti-avoidance penalties to pay",
        "one or more relevant individuals linked to the application do not meet the registration conditions",
        "Records indicate that Bobby Boucher:",
        "has one or more overdue liabilities",
        "is actively disqualified on Companies house",
        "is formally insolvent",
        "has a relevant unspent criminal conviction",
        "is subject to a relevant anti-avoidance measure, or has an unpaid liability connected to relevant anti-avoidance",
        "has one or more relevant returns outstanding"
      )
