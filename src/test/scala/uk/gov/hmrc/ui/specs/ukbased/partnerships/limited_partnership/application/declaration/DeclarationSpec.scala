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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.limited_partnership.application.declaration

import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.domain.BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.MembersAndOtherRelevantIndividuals2
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow.listProgress.partial
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.PartnersTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership.ProvidePartnersDetailsFlow
import uk.gov.hmrc.ui.specs.BaseSpec

class DeclarationSpec
extends BaseSpec:

  Feature("Complete declaration section"):
    Scenario(
      "User accepts the declaration link using FF link",
      TagLimitedPartnership
    ):
      FastForwardLinks
        .FastForward
        .runFlow(Declaration, LimitedPartnership)

    Scenario(
      "User accepts the declaration via Partners and other relevant tax advisers(2) journey using FF link",
      TagLimitedPartnership
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(MembersAndOtherRelevantIndividuals2, LimitedPartnership)

      val partnersNames = PartnersTaxAdvisorInformationFlow
        .multiplePartnersFF
        .runFlow()

      val shareLink = ProvidePartnersDetailsFlow.getProvideDetailsLink

      /* Sign in first partner (partial - more partner to come) */
      ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          partial,
          Some(partnersNames.head),
          Some(partnersNames)
        )

      /* Sign in second partner (complete - last partner) - reuse the same link */
      ProvidePartnersDetailsFlow
        .ProvidePartnersDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          complete,
          Some(partnersNames(1)),
          Some(partnersNames)
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(LimitedPartnership, fastForwardUsed = true)
