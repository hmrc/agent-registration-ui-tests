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
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.specs.BaseSpec

class FailedFixableOutcomeForApplicantSpec
extends BaseSpec:

  Feature("Applicant FailedFixable List Page"):
    Scenario(
      "Sole Trader Owner sees FailedFixable Outcome Page after sign in",
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
          Map(
            "Steve Austin" -> SetRiskingOutcomesFlow.Failures(Seq("8.7", "10.1"))
          )
        )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("You do not meet the registration conditions yet")

    Scenario(
      "General Partnership sees FailedFixable Outcome Page when partner have individual failures",
      TagFullSuite,
      TagRisking
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
            "4.1",
            "4.3"
          ),
          Seq(
            SetRiskingOutcomesFlow.Approved,
            SetRiskingOutcomesFlow.Approved
          )
        )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("Electronicsson Group")
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Electronicsson Group does not meet the registration conditions yet")
