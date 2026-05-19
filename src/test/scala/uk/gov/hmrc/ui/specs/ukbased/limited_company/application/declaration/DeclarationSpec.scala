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

package uk.gov.hmrc.ui.specs.ukbased.limited_company.application.declaration

import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.domain.BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.MembersAndOtherRelevantIndividuals2
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.DirectorTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow.listProgress.partial
import uk.gov.hmrc.ui.specs.BaseSpec

class DeclarationSpec
extends BaseSpec:

  Feature("Complete declaration section"):
    Scenario(
      "User accepts the declaration link using FF link",
      TagLimitedCompany
    ):
      /* Bug raised APB-11452 for an issue with the FF links whereby can't complete an application */
      pending

      FastForwardLinks
        .FastForward
        .runFlow(Declaration, LimitedCompany)

    Scenario(
      "User accepts the declaration via Members and other relevant individuals(2) journey using FF link",
      TagLimitedCompany
    ):
      /* Bug raised APB-11452 for an issue with the FF links whereby can't complete an application */
      pending

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(MembersAndOtherRelevantIndividuals2, LimitedCompany)

      val directorNames = DirectorTaxAdvisorInformationFlow
        .multipleDirectorsFF
        .runFlow()

      /* Get the share link once */
      val shareLink = ProvideDirectorDetailsFlow.getProvideDetailsLink

      /* Sign in first director (partial - more directors to come) */
      ProvideDirectorDetailsFlow
        .ProvideDirectorDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          partial,
          Some(directorNames.head),
          Some(directorNames)
        )

      /* Sign in second director (complete - last director) - reuse the same link */
      ProvideDirectorDetailsFlow
        .ProvideDirectorDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          complete,
          Some(directorNames(1)),
          Some(directorNames)
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(LimitedCompany, fastForwardUsed = true)
