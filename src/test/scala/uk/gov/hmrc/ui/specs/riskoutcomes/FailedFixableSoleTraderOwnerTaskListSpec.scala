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


import uk.gov.hmrc.ui.domain.BusinessType.SoleTrader
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.pages.PageObject.getCurrentUrl
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.SelectEntityFailurePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.SelectIndividualFailurePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.*
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.ConditionsNotYetMetSoleTraderCheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.SoleTraderFailureDetailsEntityFix_4_1Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.SoleTraderFailureDetailsEntityFix_4_3Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.SoleTraderFailureDetailsIndividualFix_8_7Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.SoleTraderIdentityPage
import uk.gov.hmrc.ui.specs.BaseSpec

class FailedFixableSoleTraderOwnerTaskListSpec
extends BaseSpec:

  Feature("Sole Trader Owner FailedFixable Tasklist"):
    Scenario(
      "Complete FailedFixable actions and resubmit without individual link sign in",
      TagFixableFailures,
      TagFixableFailures
    ):

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, SoleTrader)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      val applicationReference = ApplicationSubmittedPage.getApplicationReference

      ShowAgentApplicationPage.openForApplicationReference(applicationReference)
      ShowAgentApplicationPage.assertPageIsDisplayed()

      ShowAgentApplicationPage.clickRunRiskingLink()

      ShowAgentApplicationPage.clickChooseEntityFailuresLink()
      SelectEntityFailurePage.assertPageIsDisplayed()
      SelectEntityFailurePage.selectFailureCode("3.1")
      SelectEntityFailurePage.selectFailureCode("4.1")
      SelectEntityFailurePage.selectFailureCode("4.3")
      SelectEntityFailurePage.selectFailureCode("8.7")
      SelectEntityFailurePage.clickSubmitButton()
      ShowAgentApplicationPage.assertPageIsDisplayed()

      ShowAgentApplicationPage.clickChooseIndividualFailuresLink()
      SelectIndividualFailurePage.assertPageIsDisplayed()
      SelectIndividualFailurePage.selectFailureCode("8.7")
      SelectIndividualFailurePage.selectFailureCode("10.1")
      SelectIndividualFailurePage.clickSubmitButton()

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickRunResultsFileProcessingLink()
      ShowAgentApplicationPage.assertPageIsDisplayed()

      ShowAgentApplicationPage.clickLogInLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationStatusPage.assertPageIsDisplayed()
      ApplicationStatusPage.clickViewActionsToTakeButton()

      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Provide your supervision details again")
      ConditionsNotYetMetAmlsEntityFailureV31Page.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsEntityFailureV31Page.clickContinue()
      ConditionsNotYetMetAmlsCheckYourAnswersPage.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsCheckYourAnswersPage.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("File your missing VAT returns")
      SoleTraderFailureDetailsEntityFix_4_3Page.assertPageIsDisplayed()
      SoleTraderFailureDetailsEntityFix_4_3Page.selectYes()
      SoleTraderFailureDetailsEntityFix_4_3Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Pay your relevant anti-avoidance penalty liability")
      SoleTraderFailureDetailsIndividualFix_8_7Page.assertPageIsDisplayed()
      SoleTraderFailureDetailsIndividualFix_8_7Page.selectYes()
      SoleTraderFailureDetailsIndividualFix_8_7Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Provide more details to prove your identity")
      SoleTraderIdentityPage.assertPageIsDisplayed()
      SoleTraderIdentityPage.clickContinue()
      ConditionsNotYetMetSoleTraderCheckYourAnswersPage.assertPageIsDisplayed()
      ConditionsNotYetMetSoleTraderCheckYourAnswersPage.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("File your missing Self Assessment returns")
      SoleTraderFailureDetailsEntityFix_4_1Page.assertPageIsDisplayed()
      SoleTraderFailureDetailsEntityFix_4_1Page.selectYes()
      SoleTraderFailureDetailsEntityFix_4_1Page.clickContinue()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus("Declare and submit", "Incomplete")
      ConditionsNotYetMetApplicantTaskListPage.clickActionLink("Declare and submit")
      ConditionsNotYetMetApplicantDeclarationPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantDeclarationPage.clickContinue()

        ApplicationStatusPage.assertPageIsDisplayed()





