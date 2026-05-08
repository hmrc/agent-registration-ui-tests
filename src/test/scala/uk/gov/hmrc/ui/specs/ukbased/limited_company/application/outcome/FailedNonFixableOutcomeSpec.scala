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

package uk.gov.hmrc.ui.specs.ukbased.limited_company.application.outcome

import uk.gov.hmrc.ui.domain.BusinessType.LimitedCompany
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.CheckProvidedDetails
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.pages.PageObject
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.AppConfig
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedNonFixableOutcomeSpec
extends BaseSpec:

  Feature("Applicant FailedNonFixable List Page"):
    Scenario(
      "Limited Company Director sees FailedNonFixable Outcome Page after sign in",
      TagLimitedCompany
    ):
      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(CheckProvidedDetails, LimitedCompany)