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

package uk.gov.hmrc.ui.specs.applicationexpiry

import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.domain.BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.IsYourAgentBusinessBasedInTheUKPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.UnsubmittedApplicationDeletedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.UnsubmittedApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.FastForwardLinksPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ProvideDetailsSignOutPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.SignInAndConfirmDetailsPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.failuredetails.SignedOutPage
import uk.gov.hmrc.ui.specs.BaseSpec

class ApplicationExpirySpec
extends BaseSpec:

  Feature("Accessing an unsubmitted expired application"):
    Scenario(
      "Applicant views their expired, unsubmitted application",
      TagFullSuite
    ):

      FastForwardLinks.startJourneyDirect()
      FastForwardLinksPage.clickContactDetailsLink(GeneralPartnership)
      ShowAgentApplicationPage.clickExpireApplicationNow()
      ShowAgentApplicationPage.clickRunExpirySchedulerLink()
      ShowAgentApplicationPage.assertApplicationIsExpired()
      ShowAgentApplicationPage.clickLogInAsApplicantLink()
      ShowAgentApplicationPage.clickGoToTaskListLink()

      UnsubmittedApplicationPage.assertPageIsDisplayed()
      UnsubmittedApplicationPage.assertPageHeading()
      UnsubmittedApplicationPage.clickDeleteAndStartNewApplication()

      UnsubmittedApplicationDeletedPage.assertPageIsDisplayed()
      UnsubmittedApplicationDeletedPage.assertPageHeading()
      UnsubmittedApplicationDeletedPage.assertStartNewApplicationLinkIsDisplayed()
      UnsubmittedApplicationDeletedPage.clickStartNewApplicationLink()

      IsYourAgentBusinessBasedInTheUKPage.assertPageIsDisplayed()

    Scenario(
      "Individual views their expired, unsubmitted application",
      TagFullSuite
    ):

      FastForwardLinks.startJourneyDirect()
      FastForwardLinksPage.clickMembersAndOtherRelevantIndividuals2Link(LLP)
      ShowAgentApplicationPage.clickExpireApplicationNow()
      ShowAgentApplicationPage.clickRunExpirySchedulerLink()
      ShowAgentApplicationPage.assertApplicationIsExpired()
      ShowAgentApplicationPage.clickLoginAsIndividualLink()
      ShowAgentApplicationPage.clickProvideDetailsLink()

      SignInAndConfirmDetailsPage.assertPageIsDisplayed()
      SignInAndConfirmDetailsPage.assertPageHeading()
      SignInAndConfirmDetailsPage.clickFinishAndSignOut()

      ProvideDetailsSignOutPage.assertPageIsDisplayed()
      ProvideDetailsSignOutPage.assertPageHeading()
      ProvideDetailsSignOutPage.assertSignInAgainLinkIsDisplayed()
