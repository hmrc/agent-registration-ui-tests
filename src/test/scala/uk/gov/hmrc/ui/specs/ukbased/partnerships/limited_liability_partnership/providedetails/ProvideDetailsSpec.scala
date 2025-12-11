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

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.providedetails.ProvideDetailsFlow
import uk.gov.hmrc.ui.specs.BaseSpec

class ProvideDetailsSpec
extends BaseSpec:

  Feature("Complete provide member details section"):
    Scenario("User provides member details with Nino and Utr", HappyPath):

      ProvideDetailsFlow
        .ProvideFullMemberDetails
        .runFlow()

    Scenario("User provides member details WITHOUT Nino and Utr", HappyPath):

      ProvideDetailsFlow
        .ProvidePartialMemberDetails
        .runFlow()
