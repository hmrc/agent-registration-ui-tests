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

import uk.gov.hmrc.ui.pages.AgentRegistrationPage
import uk.gov.hmrc.ui.utils.Tags

class AgentRegistrationSpec extends BaseSpec {

  Feature("Agent Registration Page") {

    Scenario("Launch the page and verify the title", Tags.Smoke) {

      Given("I navigate to the agent-registration page")
      AgentRegistrationPage.open()

      When("the page loads")
      val actualTitle = AgentRegistrationPage.title

      Then("the page title should be displayed correctly")
      actualTitle shouldBe "Sign in"
    }
  }
}
