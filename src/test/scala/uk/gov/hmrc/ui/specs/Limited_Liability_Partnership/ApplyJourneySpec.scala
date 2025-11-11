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

package uk.gov.hmrc.ui.specs.Limited_Liability_Partnership

import uk.gov.hmrc.ui.flows.StubbedSignInData
import uk.gov.hmrc.ui.pages.applyJourney.TaskListPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.specs.Limited_Liability_Partnership.sections.{BusinessDetails, ContactDetails}
import uk.gov.hmrc.ui.utils.Tags.HappyPath

class ApplyJourneySpec extends BaseSpec {

  Feature("LLP Application Journey") {

    Scenario("UK-based business -> LLP -> existing online agents account", HappyPath) {

      info(
        "Covers: User providing business detail and completing contact details, " +
          "Key Variables - existing online agents account and single name match".withPos
      )

      Given("I start the LLP application and reach the Application Task List")
      val stubData: StubbedSignInData = BusinessDetails.ukBasedLlpExistingOnlineAgentsAccount()

      When("I complete the Contact Details section from the Task List")
      TaskListPage.selectContactDetailsLink()
      ContactDetails.addContactDetails(stubData)

      Then("the Contact Details section is marked as completed on the Task List")
      ContactDetails.assertContactDetailsStatus()
    }

    Scenario("UK-based business -> LLP -> no online agents account", HappyPath) {

      info(
        "Covers: User providing business detail and completing contact details, " +
          "Key Variables - no online agents account and single name match"
      )

      Given("I start the LLP application without an online agents account and reach the Task List")
      val stubData: StubbedSignInData = BusinessDetails.ukBasedLlpNoOnlineAgentsAccount()

      When("I complete the Contact Details section from the Task List")
      TaskListPage.selectContactDetailsLink()
      ContactDetails.addContactDetails(stubData)

      Then("the Contact Details section is marked as completed on the Task List")
      ContactDetails.assertContactDetailsStatus()
    }

    Scenario("UK-based business -> LLP -> Contact details multiple name match", HappyPath) {

      info(
        "Covers: User providing business detail and completing contact details, " +
          "Key Variables - no online agents account and multiple name matches"
      )

      Given("I start the LLP application without an online agents account and reach the Task List")
      val stubData: StubbedSignInData = BusinessDetails.ukBasedLlpNoOnlineAgentsAccount()

      When("I complete the Contact Details section from the Task List")
      TaskListPage.selectContactDetailsLink()
      ContactDetails.addContactDetails(stubData, multiNameMatch = true)

      Then("the Contact Details section is marked as completed on the Task List")
      ContactDetails.assertContactDetailsStatus()
    }
  }
}
