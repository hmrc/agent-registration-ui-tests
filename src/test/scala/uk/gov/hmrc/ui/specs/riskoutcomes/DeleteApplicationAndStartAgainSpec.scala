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

import uk.gov.hmrc.ui.domain.BusinessType.{GeneralPartnership, LLP}
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.pages.PageObject.getCurrentUrl
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.IsYourAgentBusinessBasedInTheUKPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ApplicationStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.{ApplicationDeletedConfirmationPage, DeleteApplicationPage, SignedOutPage}
import uk.gov.hmrc.ui.specs.BaseSpec

class DeleteApplicationAndStartAgainSpec
extends BaseSpec:

  Feature("Delete application and start again after a failed risking outcome"):
    Scenario(
      "Applicant deletes the application from the link on the FailedNonFixable Outcome Page ",
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
      ApplicationStatusPage.assertDeleteApplicationLinkIsDisplayed()
      ApplicationStatusPage.clickDeleteApplicationLink()

      DeleteApplicationPage.assertPageIsDisplayed()
      DeleteApplicationPage.assertPageHeading("Delete your application")
      DeleteApplicationPage.assertYesNoRadiosAreDisplayed()
      DeleteApplicationPage.selectYes()
      DeleteApplicationPage.clickContinue()

      ApplicationDeletedConfirmationPage.assertPageIsDisplayed()
      ApplicationDeletedConfirmationPage.assertConfirmationTitle("We have deleted the application")
      ApplicationDeletedConfirmationPage.assertWhatHappensNextHeading()
      ApplicationDeletedConfirmationPage.assertStartNewApplicationLinkIsDisplayed()
      ApplicationDeletedConfirmationPage.clickStartNewApplicationLink()
      IsYourAgentBusinessBasedInTheUKPage.assertPageIsDisplayed()

    Scenario(
        "Applicant deletes the application from the link on the FailedFixable Outcome Page ",
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
              "7",
              "8.1"
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
        ApplicationStatusPage.assertDeleteApplicationAndApplyAgainLinkIsDisplayed()
        ApplicationStatusPage.clickDeleteApplicationAndApplyAgainLink()

        DeleteApplicationPage.assertPageIsDisplayed()
        DeleteApplicationPage.assertPageHeading("Delete your application")
        DeleteApplicationPage.assertYesNoRadiosAreDisplayed()
        DeleteApplicationPage.selectNo()
        DeleteApplicationPage.clickContinue()

        ApplicationStatusPage.assertPageIsDisplayed()
        ApplicationStatusPage.assertDeleteApplicationAndApplyAgainLinkIsDisplayed()
        ApplicationStatusPage.clickDeleteApplicationAndApplyAgainLink()

        DeleteApplicationPage.assertPageIsDisplayed()
        DeleteApplicationPage.assertPageHeading("Delete your application")
        DeleteApplicationPage.assertYesNoRadiosAreDisplayed()
        DeleteApplicationPage.selectYes()
        DeleteApplicationPage.clickContinue()

        ApplicationDeletedConfirmationPage.assertPageIsDisplayed()
        ApplicationDeletedConfirmationPage.assertConfirmationTitle("We have deleted the application")
        ApplicationDeletedConfirmationPage.assertWhatHappensNextHeading()
        ApplicationDeletedConfirmationPage.clickFinishAndSignOutLink()
        
        SignedOutPage.assertPageIsDisplayed()
        SignedOutPage.assertPageHeading()
        SignedOutPage.assertSignInAgainLinkIsDisplayed()





