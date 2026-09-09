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
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ProvideDetailsOutcomeStatusPage
import uk.gov.hmrc.ui.specs.BaseSpec

class FailedNonFixableOutcomeForIndividualSpec
extends BaseSpec:

  Feature("Individual FailedNonFixable List Page"):
    Scenario(
      "Risking outcomes for Non Fixable Individual list page for General Partnership",
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
          SetRiskingOutcomesFlow.ApplicantApproved,
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

      RiskingOutcomeFlow
        .viewIndividualOutcomeStatusPageViaStub
        .runFlow(
          applicationReference,
          "Steve Austin"
        )

      ProvideDetailsOutcomeStatusPage.assertPageIsDisplayed()
      ProvideDetailsOutcomeStatusPage.assertOutcomeDescriptionContainsAll(
        "you have a relevant unspent criminal conviction",
        "you are on a published HMRC list of tax avoidance promoters, enablers or suppliers",
        "you have an overdue Self Assessment liability",
        "you have one or more Self Assessment returns outstanding",
        "you are actively disqualified on Companies House",
        "you are formally insolvent",
        "you have a relevant unpaid anti-avoidance penalty liability",
        "a relevant anti-avoidance penalty has been issued to you within the last 12 months"
      )
