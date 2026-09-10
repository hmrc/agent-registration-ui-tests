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

import uk.gov.hmrc.ui.domain.BusinessType.LLP
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ApplicationStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantTaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetEntityFailureDetailsV41Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.SaveAndComeBackLaterPage
import uk.gov.hmrc.ui.specs.BaseSpec

class FailedFixableOutcomeForApplicantFailureDetailsSpec
extends BaseSpec:

  Feature("Applicant Failure Details Page"):
    Scenario(
      "LLP user sees FailedFixable Details Page and click Yes/No variety for Self Assessment returns issue",
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
      ApplicationSubmittedPage.assertPageHeadingContains("Test Partnership")
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.clickOnSelfAssessmentReturnsLink()

      // Click Yes and check that the status is now Completed
      ConditionsNotYetMetEntityFailureDetailsV41Page.assertPageIsDisplayed()
      ConditionsNotYetMetEntityFailureDetailsV41Page.assertPageHeadingContains("Self Assessment returns for Test Partnership")
      ConditionsNotYetMetEntityFailureDetailsV41Page.selectYes()
      ConditionsNotYetMetEntityFailureDetailsV41Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Completed"
      )

      // Click Save and come back later, then retrun to the tasklist and confirm progress was saved
      ConditionsNotYetMetApplicantTaskListPage.clickSaveAndComeBackLaterButton()
      SaveAndComeBackLaterPage.assertPageIsDisplayed()
      SaveAndComeBackLaterPage.clickOnContinueWithApplicationLink()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Completed"
      )

      // Click No and check that the status is still Incomplete
      ConditionsNotYetMetApplicantTaskListPage.clickOnSelfAssessmentReturnsLink()
      ConditionsNotYetMetEntityFailureDetailsV41Page.assertPageIsDisplayed()
      ConditionsNotYetMetEntityFailureDetailsV41Page.selectNo()
      ConditionsNotYetMetEntityFailureDetailsV41Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Incomplete"
      )
