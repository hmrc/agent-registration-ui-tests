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
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.specs.BaseSpec

class FailedNonFixableOutcomeForApplicantSpec
extends BaseSpec:

  Feature("Applicant FailedNonFixable List Page"):
    Scenario(
      "Sole Trader Owner sees FailedNonFixable Outcome Page after sign in",
      TagRisking,
      TagFullSuite
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
            "7",
            "4.1",
            "5.1",
            "8.1",
            "8.4",
            "8.5"
          ),
          Seq(SetRiskingOutcomesFlow.Approved)
        )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

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
      TagRisking,
      TagFullSuite
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

      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          Seq(
            "7",
            "4.1",
            "5.1",
            "8.1",
            "8.4",
            "8.5"
          ),
          Seq(SetRiskingOutcomesFlow.Approved)
        )

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("Test User")

      ApplicationSubmittedPage.assertOutcomeDescriptionContainsAll(
        "our records show that the business is formally insolvent",
        "the business has missing tax returns in their HMRC record",
        "the business has unpaid tax liabilities",
        "the business appears on a published HMRC list of tax avoidance promoter, enablers or suppliers",
        "the business was issued with a relevant anti-avoidance penalty within the last 12 months",
        "the business has one or more relevant anti-avoidance penalties to pay"
      )

    Scenario(
      "General Partnership sees FailedNonFixable Outcome Page when both partners have individual failures",
      TagRisking,
      TagFullSuite
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, GeneralPartnership)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      SetRiskingOutcomesFlow
        .runFlow(
          applicationReference,
          Seq(
            "7",
            "3.1",
            "4.1",
            "5.1",
            "8.1",
            "8.4",
            "8.5",
            "8.6",
            "8.7"
          ),
          Map(
            "Steve Austin" -> SetRiskingOutcomesFlow.Failures(
              Seq(
                "4.1",
                "5.1",
                "6",
                "7",
                "8.1",
                "8.6",
                "8.7",
                "9"
              )
            ),
            "Beverly Hills" -> SetRiskingOutcomesFlow.Approved
          )
        )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationSubmittedPage.assertOutcomeDescriptionContainsAll(
        "our records show that the business is formally insolvent",
        "the business was issued with a relevant anti-avoidance penalty within the last 12 months",
        "the business appears on a published HMRC list of tax avoidance promoter, enablers or suppliers",
        "the business has missing tax returns in their HMRC record",
        "we could not confirm the anti-money laundering supervision for Electronicsson Group based on the information you provided",
        "the business has unpaid tax liabilities",
        "the business has one or more relevant anti-avoidance penalties to pay",
        "one or more relevant individuals linked to the application do not meet the registration conditions",
        "Records indicate that Steve Austin:",
        "has one or more overdue liabilities",
        "is actively disqualified on Companies house",
        "is formally insolvent",
        "has a relevant unspent criminal conviction",
        "is subject to a relevant anti-avoidance measure, or has an unpaid liability connected to relevant anti-avoidance",
        "has one or more relevant returns outstanding"
      )
