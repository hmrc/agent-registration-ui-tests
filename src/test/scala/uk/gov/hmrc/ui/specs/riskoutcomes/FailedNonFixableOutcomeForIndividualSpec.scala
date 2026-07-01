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
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ProvideDetailsStatusPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedNonFixableOutcomeForIndividualSpec
extends BaseSpec:

  Feature("Individual FailedNonFixable List Page"):
    Scenario(
      "Risking outcomes for Non Fixable Individual list page for General Partnership",
      TagSmokeTests,
      TagFullSuite
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

      val username = ProvideIndividualDetailsFlow
        .ProvideIndividualDetails
        .runFlowWithUsername(
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

      val application = MongoHelper
        .findBackEndApplicationByApplicationReference(applicationReference)
        .getOrElse(
          throw new AssertionError(
            s"No Mongo record found for reference: $applicationReference"
          )
        )

      val linkId: String = application.get("linkId")
        .map(_.asString().getValue)
        .getOrElse(
          throw new AssertionError(
            s"No linkId found in Mongo record for reference: $applicationReference"
          )
        )

      ApplicationSubmittedPage.clickSignOutLink()

      MongoHelper.simulateNonFixableRiskingOutcome(
        applicationReference,
        withIndividualFailures = true
      )

      RiskingOutcomeFlow
        .signInAsPreviouslyUsedIndividual
        .runFlow(
          stubbedSignInData,
          linkId,
          username
        )

      ProvideDetailsStatusPage.assertPageIsDisplayed()
      ProvideDetailsStatusPage.assertPageHeadingContainsForIndividual()

      val outcomeDoc = MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(
          throw new AssertionError(
            s"No Mongo record found for reference: $applicationReference"
          )
        )

      MongoHelper.getTopLevelString(
        outcomeDoc,
        "riskingFileName"
      ) shouldBe "any-old.txt"

      val individuals = MongoHelper.findIndividualsByApplicationReference(
        applicationReference
      )

      individuals should have size 1

      individuals.foreach { indDoc =>
        val indFailures = MongoHelper.getIndividualRiskingFailures(indDoc)

        indFailures should not be empty

        val indFailureTypes = indFailures.map(f =>
          MongoHelper.getNestedString(f, "type")
        )

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
          .find(f =>
            MongoHelper.getNestedString(f, "type") == "_5._1"
          )
          .getOrElse(
            throw new AssertionError(
              "No _5._1 failure found on individual"
            )
          )
      }

      ApplicationSubmittedPage.assertOutcomeDescriptionContainsAll(
        "you have a relevant unspent criminal conviction",
        "you are on a published HMRC list of tax avoidance promoters, enablers or suppliers",
        "you have an overdue Self Assessment liability",
        "you have one or more Self Assessment returns outstanding",
        "you are actively disqualified on Companies House",
        "you are formally insolvent",
        "you have a relevant unpaid anti-avoidance penalty liability",
        "a relevant anti-avoidance penalty has been issued to you within the last 12 months"
      )
