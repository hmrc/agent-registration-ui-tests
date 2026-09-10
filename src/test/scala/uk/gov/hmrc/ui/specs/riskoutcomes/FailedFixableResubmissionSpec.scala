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
import uk.gov.hmrc.ui.domain.BusinessType.SoleTrader
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ViewApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ApplicationStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantDeclarationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantTaskListPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedFixableResubmissionSpec
extends BaseSpec:

  Feature("Individual FailedFixable Tasklist"):
    Scenario(
      "General Partnership Applicant signs declaration and confirms resubmission",
      TagFullSuite,
      TagRisking
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          Seq(
            "3.1",
            "4.1"
          ),
          Map(
            "Steve Austin" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1")),
            "Beverly Hills" -> SetRiskingOutcomesFlow.Failures(Seq("4.1", "5.1"))
          )
        )

      MongoHelper.confirmRiskingOutcomeApplicantFixes(
        applicationReference = applicationReference,
        fixTypesToConfirm = Seq(
          "EntityFix._3.AmlsFix",
          "EntityFix._4._1"
        )
      )

      MongoHelper.confirmRiskingOutcomeIndividualFixes(
        applicationReference = applicationReference,
        fixTypesByIndividualName = Map(
          "Steve Austin" -> Seq("IndividualFix._4._1", "IndividualFix._5._1"),
          "Beverly Hills" -> Seq("IndividualFix._4._1", "IndividualFix._5._1")
        )
      )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

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

    Scenario(
      "Sole Trader (Non Owner) Applicant signs declaration and confirms resubmission",
      TagFullSuite,
      TagRisking
    ):

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

      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          Seq(
            "3.1",
            "4.1"
          ),
          Seq(
            SetRiskingOutcomesFlow.Failures(Seq(
              "4.1",
              "5.1"
            ))
          )
        )

      MongoHelper.confirmRiskingOutcomeApplicantFixes(
        applicationReference = applicationReference,
        fixTypesToConfirm = Seq(
          "EntityFix._3.AmlsFix",
          "EntityFix._4._1"
        )
      )

      MongoHelper.confirmRiskingOutcomeIndividualFixes(
        applicationReference = applicationReference,
        fixTypesByIndividualName = Map(
          "ST Name ST Lastname" -> Seq("IndividualFix._4._1", "IndividualFix._5._1")
        )
      )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

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

    Scenario(
      "Sole Trader (Owner) Applicant signs declaration and confirms resubmission",
      TagFullSuite,
      TagRisking
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, SoleTrader)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          Seq(
            "3.1",
            "4.1",
            "4.3"
          ),
          Seq("8.7", "4.1")
        )

      MongoHelper.confirmRiskingOutcomeApplicantFixes(
        applicationReference = applicationReference,
        fixTypesToConfirm = Seq(
          "EntityFix._3.AmlsFix",
          "EntityFix._4._1",
          "EntityFix._4._3"
        )
      )

      MongoHelper.confirmRiskingOutcomeIndividualFixes(
        applicationReference = applicationReference,
        fixTypesByIndividualName = Map(
          "Steve Austin" -> Seq("IndividualFix._8._7", "IndividualFix._4._1")
        )
      )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

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
