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
import uk.gov.hmrc.ui.domain.BusinessType.SoleTrader
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.partnerInformation.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ViewApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ApplicationStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantDeclarationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantTaskListPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper
import uk.gov.hmrc.ui.utils.MongoHelper.EntityFix
import uk.gov.hmrc.ui.utils.MongoHelper.IndividualFix
import uk.gov.hmrc.ui.utils.MongoHelper.IndividualRiskingOutcome

class FailedFixableResubmissionSpec
extends BaseSpec:

  Feature("Individual FailedFixable Tasklist"):
    Scenario("LLP Applicant signs declaration and confirms resubmission", TagFixableFailures):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, GeneralPartnership)

      PartnerTaxAdvisorInformationFlow
        .singlePartner
        .runFlow()

      ProvideIndividualDetailsFlow
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

      // insert failed fixable data into agent-application collection
      MongoHelper.insertRiskingOutcomeToAgentApplication(
        applicationReference = applicationReference,
        actualDecisionDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = Seq(EntityFix("EntityFix._4._2", isConfirmed = true))
      )

      // Insert one individual. They have confirmed their fixes and signed the declaration
      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Bobby Boucher" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3", isConfirmed = true),
              IndividualFix("IndividualFix._8._7", isConfirmed = true)
            ),
            declarationAgreed = true
          )
        )
      )

      // Delete risking data from application-for-risking and individual-for-risking collections
      // This simulates what happens when the archiving process runs once a risking outcome is determined
      MongoHelper.deleteAllRiskingData(applicationReference)

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(
          stubbedSignInData
        )

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.clickViewActionLink()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Declare and submit")
      ConditionsNotYetMetApplicantDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantDeclarationPage.clickContinue()
      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.assertConfirmationTitle("You have resubmitted your application for an agent services account")
      ApplicationStatusPage.clickViewOrPrintLink()
      ViewApplicationPage.assertPageIsDisplayed()

    Scenario("Sole Trader (Non Owner) Applicant signs declaration and confirms resubmission", TagFixableFailures):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, SoleTrader)

      ProvideIndividualDetailsFlow
        .ProvideIndividualDetailsSoleTrader
        .runFlow(
          stubbedSignInData,
          ProvideIndividualDetailsFlow.listProgress.complete,
          fastForwardUsed = true
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(SoleTrader)

      ApplicationStatusPage.assertPageIsDisplayed()

      ApplicationStatusPage.assertConfirmationTitle(
        "You’ve applied for an agent services account"
      )

      val applicationReference = ApplicationStatusPage.getApplicationReference

      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      // insert failed fixable data into agent-application collection
      MongoHelper.insertRiskingOutcomeToAgentApplication(
        applicationReference = applicationReference,
        actualDecisionDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = Seq(EntityFix("EntityFix._4._2", isConfirmed = true))
      )

      // Insert one individual. They have confirmed their fixes and signed the declaration
      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "ST Name ST Lastname" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3", isConfirmed = true),
              IndividualFix("IndividualFix._8._7", isConfirmed = true)
            ),
            declarationAgreed = true
          )
        )
      )

      // Delete risking data from application-for-risking and individual-for-risking collections
      // This simulates what happens when the archiving process runs once a risking outcome is determined
      MongoHelper.deleteAllRiskingData(applicationReference)

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(
          stubbedSignInData
        )

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.clickViewActionLink()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Declare and submit")
      ConditionsNotYetMetApplicantDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantDeclarationPage.clickContinue()
      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.assertConfirmationTitle("You have resubmitted your application for an agent services account")
      ApplicationStatusPage.clickViewOrPrintLink()
      ViewApplicationPage.assertPageIsDisplayed()

    Scenario("Sole Trader (Owner) Applicant signs declaration and confirms resubmission", TagFixableFailures):

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

      ApplicationStatusPage.assertPageIsDisplayed()

      ApplicationStatusPage.assertConfirmationTitle(
        "You’ve applied for an agent services account"
      )

      val applicationReference = ApplicationStatusPage.getApplicationReference

      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      // insert failed fixable data into agent-application collection
      MongoHelper.insertRiskingOutcomeToAgentApplication(
        applicationReference = applicationReference,
        actualDecisionDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = Seq(EntityFix("EntityFix._4._2", isConfirmed = true))
      )

      // Insert one individual. They have confirmed their fixes and signed the declaration
      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Test User" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3", isConfirmed = true),
              IndividualFix("IndividualFix._8._7", isConfirmed = true)
            ),
            declarationAgreed = true
          )
        )
      )

      // Delete risking data from application-for-risking and individual-for-risking collections
      // This simulates what happens when the archiving process runs once a risking outcome is determined
      MongoHelper.deleteAllRiskingData(applicationReference)

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(
          stubbedSignInData
        )

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.clickViewActionLink()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Declare and submit")
      ConditionsNotYetMetApplicantDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantDeclarationPage.clickContinue()
      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.assertConfirmationTitle("You have resubmitted your application for an agent services account")
      ApplicationStatusPage.clickViewOrPrintLink()
      ViewApplicationPage.assertPageIsDisplayed()
