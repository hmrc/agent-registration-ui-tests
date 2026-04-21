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
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.{BusinessDetailsFlow, PartnersTaxAdvisorInformationFlow, ProvidePartnersDetailsFlow}
import uk.gov.hmrc.ui.specs.BaseSpec

class PartnersTaxAdvisorInformationSpec
extends BaseSpec:

  Feature("Complete Scottish Limited Partners and Other Tax Adviser information section"):
    Scenario(
      "Partnership has 2 partners (happy path)",
      TagScottishLimitedPartnership
    ):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

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

      PartnersTaxAdvisorInformationFlow
        .partnersAndOtherTaxAdvisers
        .runFlow()

    Scenario(
      "Partnership has 1 other relevant tax adviser",
      TagScottishLimitedPartnership
    ):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

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

      PartnersTaxAdvisorInformationFlow
        .otherTaxAdvisers
        .runFlowWith1OtherRelevantTaxAdvisers()

    Scenario(
      "Partnership has any more relevant tax advisers",
      TagScottishLimitedPartnership
    ):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

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

      PartnersTaxAdvisorInformationFlow
        .anyMoreTaxAdvisers
        .runFlowWithAnyMoreRelevantTaxAdvisers()


    Scenario(
      "Change other relevant tax advisers from check your answers page",
      TagScottishLimitedPartnership
    ):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

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

      PartnersTaxAdvisorInformationFlow
        .changeNoOtherRelevantTaxAdvisers
        .runFlowWithChangeNoOtherRelevantTaxAdvisers()