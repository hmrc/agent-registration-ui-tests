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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.scottish_partnership.application.numberofpartners

import uk.gov.hmrc.ui.domain.BusinessType.ScottishPartnership
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import ProvideIndividualDetailsFlow.listProgress.complete
import ProvideIndividualDetailsFlow.listProgress.partial
import uk.gov.hmrc.ui.flows.common.application.partnerInformation.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.specs.BaseSpec

class ProvideIndividualDetailsSpec
extends BaseSpec:

  Feature("Provide individual details"):
    Scenario("Single partner signs in and approves application", TagGeneralPartnership):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, ScottishPartnership)

      PartnerTaxAdvisorInformationFlow
        .singlePartner
        .runFlow()

      ProvideIndividualDetailsFlow
        .ProvideIndividualDetails
        .runFlow(
          stubbedSignInData,
          complete,
          ScottishPartnership
        )

    Scenario("Multiple partners partially completed list", TagGeneralPartnership):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, ScottishPartnership)

      PartnerTaxAdvisorInformationFlow
        .FiveOrLessPartners
        .runFlow()

      ProvideIndividualDetailsFlow
        .ProvideIndividualDetails
        .runFlow(
          stubbedSignInData,
          partial,
          ScottishPartnership
        )
