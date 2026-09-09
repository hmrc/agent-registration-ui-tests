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
import uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes.SetRiskingOutcomesFlow
import uk.gov.hmrc.ui.pages.PageObject.getCurrentUrl
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsCheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsEntityFailureV31Page
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsEvidencePage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsEvidenceUploadResultPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsRegistrationNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetAmlsSupervisorNamePage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotYetMetApplicantTaskListPage
import uk.gov.hmrc.ui.specs.BaseSpec

class FailedFixableAmlsCheckYourAnswersSpec
extends BaseSpec:

  Feature("Check and confirm AMLS details after AMLS failure"):
    Scenario(
      "SoleTraderOwner reviews and confirms AMLS details with reason code 3.1 successfully",
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
          Seq("3.1"),
          Seq(SetRiskingOutcomesFlow.Approved)
        )

      ShowAgentApplicationPage.assertPageIsDisplayed()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.clickViewActionLink()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()

      ConditionsNotYetMetApplicantTaskListPage.clickOnProvideYourSupervisionDetailsLink()

      ConditionsNotYetMetAmlsEntityFailureV31Page.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsEntityFailureV31Page.clickContinue()

      ConditionsNotYetMetAmlsCheckYourAnswersPage.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsCheckYourAnswersPage.assertSummaryRowPresent("Supervisory body")
      ConditionsNotYetMetAmlsCheckYourAnswersPage.assertSummaryRowPresent("Registration number")

      ConditionsNotYetMetAmlsCheckYourAnswersPage.clickChangeFor("Supervisory body")
      ConditionsNotYetMetAmlsSupervisorNamePage.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsSupervisorNamePage.enterSupervisor("Association of Chartered Certified Accountants (ACCA)")
      ConditionsNotYetMetAmlsSupervisorNamePage.clickContinue()

      ConditionsNotYetMetAmlsRegistrationNumberPage.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsRegistrationNumberPage.enterNonHMRCRegistrationNumber()
      ConditionsNotYetMetAmlsRegistrationNumberPage.clickContinue()

      ConditionsNotYetMetAmlsEvidencePage.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsEvidencePage.uploadFileFromResources("Aml-Evidence.docx")
      ConditionsNotYetMetAmlsEvidencePage.clickContinue()

      ConditionsNotYetMetAmlsEvidenceUploadResultPage.assertPageIsDisplayed()
      ConditionsNotYetMetAmlsEvidenceUploadResultPage.clickContinue()

      ConditionsNotYetMetAmlsCheckYourAnswersPage.clickContinue()

      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertAmlsDetailsLinkText("Provide your supervision details again")
      ConditionsNotYetMetApplicantTaskListPage.assertAmlsDetailsStatus("Completed")

  Scenario(
    "SoleTraderOwner changes registration number from CYA with prefilled value and returns to CYA",
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
        Seq("3.1"),
        Seq(SetRiskingOutcomesFlow.Approved)
      )

    ShowAgentApplicationPage.assertPageIsDisplayed()
    ShowAgentApplicationPage.clickLogInAsApplicantLink()
    ShowAgentApplicationPage.clickGoToTaskListLink()

    ApplicationSubmittedPage.assertPageIsDisplayed()
    ApplicationSubmittedPage.clickViewActionLink()

    ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
    ConditionsNotYetMetApplicantTaskListPage.clickOnProvideYourSupervisionDetailsLink()

    ConditionsNotYetMetAmlsEntityFailureV31Page.assertPageIsDisplayed()
    ConditionsNotYetMetAmlsEntityFailureV31Page.clickContinue()

    ConditionsNotYetMetAmlsCheckYourAnswersPage.assertPageIsDisplayed()
    ConditionsNotYetMetAmlsCheckYourAnswersPage.clickChangeFor("Registration number")

    ConditionsNotYetMetAmlsRegistrationNumberPage.assertPageIsDisplayed()
    ConditionsNotYetMetAmlsRegistrationNumberPage.assertRegistrationNumberPrefilled("XAML00000123456")
    ConditionsNotYetMetAmlsRegistrationNumberPage.enterRegistrationNumber("XAML00000111111")
    ConditionsNotYetMetAmlsRegistrationNumberPage.clickContinue()

    ConditionsNotYetMetAmlsCheckYourAnswersPage.assertPageIsDisplayed()
    ConditionsNotYetMetAmlsCheckYourAnswersPage.assertSummaryRow("Registration number", "XAML00000111111")
