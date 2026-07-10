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

import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.domain.BusinessType.GeneralPartnership
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.partnerInformation.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetConfirmationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetDeclarationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetIndividualTaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualFixIdentityPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualFix_4_1Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualFix_4_3Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualFix_5_1Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualFix_8_7Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.IndividualIdentityFixCheckYourAnswersPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper
import uk.gov.hmrc.ui.utils.MongoHelper.IndividualFix
import uk.gov.hmrc.ui.utils.MongoHelper.IndividualRiskingOutcome

class FailedFixableIndividualTasklistSpec
extends BaseSpec:

  Feature("Individual FailedFixable Tasklist"):
    Scenario("Individual views Actions to be completed list", TagFixableFailures):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, GeneralPartnership)

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
      val linkId: String = MongoHelper.getLinkIdByApplicationReference(applicationReference)

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
          "Bobby Boucher" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3"),
              IndividualFix("IndividualFix._8._7"),
              IndividualFix("IndividualFix._4._1"),
              IndividualFix("IndividualFix._5._1")
            )
          )
        )
      )

      RiskingOutcomeFlow
        .viewIndividualTaskListPage
        .runFlow(
          stubbedSignInData,
          linkId,
          username
        )

      // verify actions and their incomplete status
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "File your missing VAT returns",
        "Incomplete"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Pay your relevant anti-avoidance penalty liability",
        "Incomplete"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "File your missing Self Assessment returns",
        "Incomplete"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Pay your Self Assessment liability",
        "Incomplete"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Confirm your responses are final",
        "Cannot start yet"
      )

      // view an action, set it to completed and verify the status is updated
      ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
        "File your missing VAT returns"
      )
      IndividualFix_4_3Page.assertPageIsDisplayed()
      IndividualFix_4_3Page.selectYes()
      IndividualFix_4_3Page.clickContinue()
      ConditionsNotYetMetIndividualTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "File your missing VAT returns",
        "Completed"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Confirm your responses are final",
        "Cannot start yet"
      )

      // view each remaining action, set it to completed, verify the status is updated and verify the final action is now available to start
      ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
        "Pay your relevant anti-avoidance penalty liability"
      )
      IndividualFix_8_7Page.assertPageIsDisplayed()
      IndividualFix_8_7Page.selectYes()
      IndividualFix_8_7Page.clickContinue()
      ConditionsNotYetMetIndividualTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Pay your relevant anti-avoidance penalty liability",
        "Completed"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Confirm your responses are final",
        "Cannot start yet"
      )
      ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
        "File your missing Self Assessment returns"
      )
      IndividualFix_4_1Page.assertPageIsDisplayed()
      IndividualFix_4_1Page.selectYes()
      IndividualFix_4_1Page.clickContinue()
      ConditionsNotYetMetIndividualTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "File your missing Self Assessment returns",
        "Completed"
      )
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Confirm your responses are final",
        "Cannot start yet"
      )
      ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
        "Pay your Self Assessment liability"
      )
      IndividualFix_5_1Page.assertPageIsDisplayed()
      IndividualFix_5_1Page.selectYes()
      IndividualFix_5_1Page.clickContinue()
      ConditionsNotYetMetIndividualTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Pay your Self Assessment liability",
        "Completed"
      )

      // verify status of final action is now available to start
      ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
        "Confirm your responses are final",
        "Incomplete"
      )

      // Confirm responses
      ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
        "Confirm your responses are final"
      )
      ConditionsNotYetMetDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetDeclarationPage.clickContinue()
      ConditionsNotYetMetConfirmationPage.assertPageIsDisplayed()
      ConditionsNotYetMetConfirmationPage.assertConfirmationTitle("You have finished this process")

  Scenario("Unknown Individual failure", TagFixableFailures):

    val stubbedSignInData = FastForwardLinks
      .FastForward
      .runFlow(AgentStandards, GeneralPartnership)

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
    val linkId: String = MongoHelper.getLinkIdByApplicationReference(applicationReference)

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
        "Bobby Boucher" -> IndividualRiskingOutcome(
          outcomeType = "FailedFixable",
          fixes = Seq(
            IndividualFix(
              fixType = "IndividualFix._10.IndividualDetailsFix",
              dateOfBirth = Some("1990-01-01"),
              nino = Some("AA111111B"),
              saUtr = Some("123456789")
            )
          )
        )
      )
    )

    RiskingOutcomeFlow
      .viewIndividualTaskListPage
      .runFlow(
        stubbedSignInData,
        linkId,
        username
      )

    // verify action is present and has an incomplete status
    ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
      "Provide more details to prove your identity",
      "Incomplete"
    )
    ConditionsNotYetMetIndividualTaskListPage.assertActionStatus(
      "Confirm your responses are final",
      "Cannot start yet"
    )

    // view an action, set it to completed and verify the status is updated
    ConditionsNotYetMetIndividualTaskListPage.clickActionLink(
      "Provide more details to prove your identity"
    )
    IndividualFixIdentityPage.assertPageIsDisplayed()
    IndividualFixIdentityPage.clickContinue()
    IndividualIdentityFixCheckYourAnswersPage.assertPageIsDisplayed()
    IndividualIdentityFixCheckYourAnswersPage.assertSummaryRow("Date of birth", "1 January 1990")
    IndividualIdentityFixCheckYourAnswersPage.assertSummaryRow("Do you have a National Insurance number?", "Yes")
    IndividualIdentityFixCheckYourAnswersPage.assertSummaryRow("National Insurance number", "AA111111B")
    IndividualIdentityFixCheckYourAnswersPage.assertSummaryRow("Do you have a Self Assessment Unique Taxpayer Reference?", "Yes")
    IndividualIdentityFixCheckYourAnswersPage.assertSummaryRow("Self Assessment Unique Taxpayer Reference", "123456789")
