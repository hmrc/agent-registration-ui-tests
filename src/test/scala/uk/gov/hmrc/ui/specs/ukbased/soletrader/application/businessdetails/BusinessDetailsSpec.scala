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

package uk.gov.hmrc.ui.specs.ukbased.soletrader.application.businessdetails

import uk.gov.hmrc.ui.domain.BusinessType
import BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.BusinessDetails
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.IsYourAgentBusinessBasedInTheUKPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.StartAgainConfirmationPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.soletrader.businessdetails.CannotConfirmIdentityPage
import uk.gov.hmrc.ui.specs.BaseSpec

class BusinessDetailsSpec
extends BaseSpec:

  Feature("Complete BusinessDetails"):
    Scenario(
      "When user has no online agent account",
      TagSoleTrader
    ):
      pending // issue with deceased check
      BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()
      TaskListPage.assertPageIsDisplayed()
      TaskListPage.assertBusinessDetailsStatus("Completed")

    Scenario(
      "When company has a blocking status",
      TagSoleTrader
    ):
      pending // issue with deceased check
      BusinessDetailsFlow
        .IsDeceased
        .runFlow()
      CannotConfirmIdentityPage.assertPageIsDisplayed()
      CannotConfirmIdentityPage.assertHeaderText("Get in touch to confirm your details")

    Scenario(
      "An Applicant completes GRS journey and reviews Business details via enhanced CYA",
      TagSoleTrader
    ):

      FastForwardLinks
        .FastForward
        .runFlow(BusinessDetails, SoleTrader)

      TaskListPage.assertPageIsDisplayed()
      TaskListPage.assertBusinessDetailsStatus("Completed")
      TaskListPage.clickOnAboutYourBusinessLink()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertChangeLink("Business details")
      CheckYourAnswersPage.assertSummaryRow("UK-based agent", "Yes")
      CheckYourAnswersPage.assertSummaryRow("Business type", "Sole trader")
      CheckYourAnswersPage.assertSummaryRow("Are you the owner of the business?", "Yes")
      CheckYourAnswersPage.assertSummaryRow("Sole trader name", "ST Name ST Lastname")
      CheckYourAnswersPage.assertSummaryRow("Unique taxpayer reference", "1234567895")
      CheckYourAnswersPage.clickOnAssertChangeLink("Business details")

      StartAgainConfirmationPage.assertPageIsDisplayed()
      StartAgainConfirmationPage.clickContinue()
      IsYourAgentBusinessBasedInTheUKPage.assertPageIsDisplayed()
