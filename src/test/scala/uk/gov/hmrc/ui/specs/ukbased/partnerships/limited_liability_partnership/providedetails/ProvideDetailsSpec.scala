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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.limited_liability_partnership.providedetails

import uk.gov.hmrc.ui.domain.BusinessType.LLP
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.PartnersTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow.listProgress.partial
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.*
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.PasscodeHelper

class ProvideDetailsSpec
extends BaseSpec:

  Feature("Complete provide individual details section"):
    Scenario(
      "User provides individual details WITHOUT Utr",
      TagProvideDetails
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, LLP)

      val partnersNames = PartnersTaxAdvisorInformationFlow
        .multiplePartners
        .runFlowForLLP()

      val shareLink = ProvidePartnersDetailsFlow.getProvideDetailsLink

      ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          partial,
          Some(partnersNames.head),
          Some(partnersNames),
          hasUtr = false
        )

    Scenario(
      "Utr details retrieved from HMRC",
      TagProvideDetails
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, LLP)

      val partnersNames = PartnersTaxAdvisorInformationFlow
        .multiplePartners
        .runFlowForLLP()

      val shareLink = ProvidePartnersDetailsFlow.getProvideDetailsLink

      ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowWithUtrDetailsFromHmrc(
          stubbedSignInData,
          shareLink,
          Some(partnersNames.head)
        )

    Scenario(
      "Locked email",
      TagProvideDetails
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, LLP)

      val partnersNames = PartnersTaxAdvisorInformationFlow
        .multiplePartners
        .runFlowForLLP()

      val shareLink = ProvidePartnersDetailsFlow.getProvideDetailsLink

      ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowWithLinkForLockedEmail(
          stubbedSignInData,
          shareLink,
          partial,
          Some(partnersNames.head),
          Some(partnersNames),
          hasUtr = false
        )

    Scenario(
      "Change details from CYA",
      TagProvideDetails
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, LLP)

      val partnersNames = PartnersTaxAdvisorInformationFlow
        .multiplePartners
        .runFlowForLLP()

      val shareLink = ProvidePartnersDetailsFlow.getProvideDetailsLink

      val partnerStubData = ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowToChangeDetailsCheckYourAnswers(
          stubbedSignInData,
          shareLink,
          partial,
          Some(partnersNames.head),
          Some(partnersNames),
          hasUtr = true
        )
      // change Telephone number
      ProvideDetailsCheckYourAnswersPage.clickChangeFor("Telephone number")
      ProvideDetailsTelephoneNumberPage.assertPageIsDisplayed()
      ProvideDetailsTelephoneNumberPage.enterTelephoneNumber("07888888888")
      ProvideDetailsTelephoneNumberPage.clickContinue()
      ProvideDetailsCheckYourAnswersPage.assertPageDisplayed()
      ProvideDetailsCheckYourAnswersPage.assertSummaryRow("Telephone number", "07888888888")

      // change Email address
      ProvideDetailsCheckYourAnswersPage.clickChangeFor("Email address")
      ProvideDetailsEmailAddressPage.assertPageIsDisplayed()
      ProvideDetailsEmailAddressPage.enterEmailAddress("newtest@test.com")
      ProvideDetailsEmailAddressPage.clickContinue()
      EmailVerificationTestOnlyPage.assertPageIsDisplayed()

      EmailVerificationTestOnlyPage.clickContinue()
      val passcode = PasscodeHelper.getPasscode(partnerStubData.bearerToken, partnerStubData.sessionId)
      ProvideDetailsConfirmEmailPage.enterConfirmationCode(passcode)
      ProvideDetailsConfirmEmailPage.clickContinue()

      // change Self Assessment Unique Taxpayer Reference number
      ProvideDetailsCheckYourAnswersPage.clickChangeFor("Self Assessment Unique Taxpayer Reference")
      ProvideDetailsUtrPage.assertPageIsDisplayed()
      ProvideDetailsUtrPage.enterUtr("0987654321")
      ProvideDetailsUtrPage.clickContinue()
      ProvideDetailsCheckYourAnswersPage.assertPageDisplayed()
      ProvideDetailsCheckYourAnswersPage.assertSummaryRow("Self Assessment Unique Taxpayer Reference", "0987654321")

      // remove Self Assessment Unique Taxpayer Reference number
      ProvideDetailsCheckYourAnswersPage.clickChangeFor("Self Assessment Unique Taxpayer Reference")
      ProvideDetailsUtrPage.assertPageIsDisplayed()
      ProvideDetailsUtrPage.selectNo()
      ProvideDetailsUtrPage.clickContinue()
      ProvideDetailsCheckYourAnswersPage.assertPageDisplayed()
      ProvideDetailsCheckYourAnswersPage.assertSummaryRow("Do you have a Self Assessment Unique Taxpayer Reference?", "No")
      ProvideDetailsCheckYourAnswersPage.assertSummaryRowNotPresent("Self Assessment Unique Taxpayer Reference")
