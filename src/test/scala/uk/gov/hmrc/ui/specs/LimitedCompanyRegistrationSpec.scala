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

package uk.gov.hmrc.ui.specs

class LimitedCompanyRegistrationSpec extends BaseSpec {

  Feature("Limited Company registration journey") {
    pending
    Scenario("Applicant is a director") {
      Given("Applicant start the registration journey and is a director")
      And("They provide their business details")
      And("They provide their personal details")
      And("They select their ASA settings")
      And("They provide their ALMS details")
      When("They check their answers and submit")
      Then("Their application is complete")
    }

    Scenario("Applicant is not the business owner") {
      Given("Applicant start the registration journey and is not a director")
      And("They provide their business details")
      And("They provide their personal details")
      And("They select their ASA settings")
      And("They provide their ALMS details")
      When("They check their answers and submit")
      Then("Their application is complete")
    }
  }
}
