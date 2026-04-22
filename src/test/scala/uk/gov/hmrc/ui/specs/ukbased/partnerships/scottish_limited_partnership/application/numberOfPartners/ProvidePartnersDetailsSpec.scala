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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.scottish_limited_partnership.application.numberOfPartners

import uk.gov.hmrc.ui.domain.BusinessType.ScottishLimitedPartnership
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow.listProgress.partial
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.PartnersTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow
import uk.gov.hmrc.ui.specs.BaseSpec

class ProvidePartnersDetailsSpec
extends BaseSpec:

  Feature("Provide individual details"):
    Scenario("Multiple Partners signs in and approves application", TagScottishLimitedPartnership):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow(Some(2))

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(ScottishLimitedPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(ScottishLimitedPartnership)

      val partnersNames = PartnersTaxAdvisorInformationFlow
        .multiplePartners
        .runFlow()

      // Get the share link once
      val shareLink = ProvidePartnersDetailsFlow.getProvideDetailsLink

      println(s"Share link: $shareLink")

      // Sign in first partner (partial - more partner to come)
      ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          partial,
          Some(partnersNames.head),
          Some(partnersNames)
        )

      // Sign in second partner (complete - last partner) - reuse the same link
      ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          complete,
          Some(partnersNames(1)),
          Some(partnersNames)
        )
