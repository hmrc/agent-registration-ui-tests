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

package uk.gov.hmrc.ui.specs.ukbased.limited_company.application.numberOfDirectors

import uk.gov.hmrc.ui.domain.BusinessType.LimitedCompany
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.DirectorTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.ukbased.limited_company.providedetails.ProvideDirectorDetailsFlow.listProgress.partial
import uk.gov.hmrc.ui.specs.BaseSpec

class ProvideDirectorDetailsSpec
extends BaseSpec:

  Feature("Provide individual details"):
    Scenario("Multiple Director signs in and approves application", TagLimitedCompany):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow(Some(2))

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LimitedCompany)

      val directorNames = DirectorTaxAdvisorInformationFlow
        .multipleDirectors
        .runFlow()

      // Get the share link once
      val shareLink = ProvideDirectorDetailsFlow.getProvideDetailsLink

      // Sign in first director (partial - more directors to come)
      ProvideDirectorDetailsFlow
        .ProvideDirectorDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          partial,
          Some(directorNames.head),
          Some(directorNames)
        )

      // Sign in second director (complete - last director) - reuse the same link
      ProvideDirectorDetailsFlow
        .ProvideDirectorDetails
        .runFlowWithLink(
          stubbedSignInData,
          shareLink,
          complete,
          Some(directorNames(1)),
          Some(directorNames)
        )
