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

import uk.gov.hmrc.ui.pages._
import uk.gov.hmrc.ui.utils.Tags.HappyPath
import uk.gov.hmrc.ui.flows.StubbedSignIn

class LimitedLiabilityPartnershipRegistrationSpec extends BaseSpec with StubbedSignIn {

  Feature("Limited Liability Partnership registration journey") {

    Scenario("UK-based business -> select partnership -> choose LLP -> existing online agents account", HappyPath) {

      Given("I start the Agent Registration journey at the apply page")
      IsYourAgenBusinessBasedInTheUKPage.open()

      Then("the start page is displayed")
      IsYourAgenBusinessBasedInTheUKPage.assertPageIsDisplayed()

      When("I answer that the business is based in the UK and continue")
      IsYourAgenBusinessBasedInTheUKPage.selectYes()
      IsYourAgenBusinessBasedInTheUKPage.clickContinue()

      Then("the 'How is your business set up?' page is displayed")
      HowIsYourBusinessSetUpPage.assertPageIsDisplayed()

      When("I choose 'A type of partnership' and continue")
      HowIsYourBusinessSetUpPage.selectATypeOfPartnership()
      HowIsYourBusinessSetUpPage.clickContinue()

      Then("the 'What type of partnership' page is displayed")
      WhatTypeOfPartnershipPage.assertPageIsDisplayed()

      When("I choose 'Limited liability partnership (LLP)' and continue")
      WhatTypeOfPartnershipPage.selectLimitedLiabilityPartnership()
      WhatTypeOfPartnershipPage.clickContinue()

      Then("the 'HMRC online service account' screen is displayed")
      HmrcOnlineServicesAccountPage.assertPageIsDisplayed()

      When("I choose 'Yes' and continue")
      HmrcOnlineServicesAccountPage.selectYes()
      HmrcOnlineServicesAccountPage.clickContinue()

      Then("the 'Sign in with your agent account details' screen is displayed")
      SignInWithAgentAccountPage.assertPageIsDisplayed()

      When("I select continue")
      SignInWithAgentAccountPage.clickContinue()

      Then("the 'Government Gateway Sign In' screen is displayed")
      GovernmentGatewaySignInPage.assertPageIsDisplayed()

      When("I sign in with valid credentials") // Stubbed journey
      signInAndDataSetupViaStubs()

      Then("I am taken to the Application Task List")
      ApplicationTaskList.assertPageIsDisplayed()
    }

    Scenario("UK-based business -> select partnership -> choose LLP -> no online agents account", HappyPath) {

      Given("I start the Agent Registration journey at the apply page")
      IsYourAgenBusinessBasedInTheUKPage.open()

      Then("the start page is displayed")
      IsYourAgenBusinessBasedInTheUKPage.assertPageIsDisplayed()

      When("I answer that the business is based in the UK and continue")
      IsYourAgenBusinessBasedInTheUKPage.selectYes()
      IsYourAgenBusinessBasedInTheUKPage.clickContinue()

      Then("the 'How is your business set up?' page is displayed")
      HowIsYourBusinessSetUpPage.assertPageIsDisplayed()

      When("I choose 'A type of partnership' and continue")
      HowIsYourBusinessSetUpPage.selectATypeOfPartnership()
      HowIsYourBusinessSetUpPage.clickContinue()

      Then("the 'What type of partnership' page is displayed")
      WhatTypeOfPartnershipPage.assertPageIsDisplayed()

      When("I choose 'Limited liability partnership (LLP)' and continue")
      WhatTypeOfPartnershipPage.selectLimitedLiabilityPartnership()
      WhatTypeOfPartnershipPage.clickContinue()

      Then("the 'HMRC online service account' screen is displayed")
      HmrcOnlineServicesAccountPage.assertPageIsDisplayed()

      When("I choose 'No' and continue")
      HmrcOnlineServicesAccountPage.selectNo()
      HmrcOnlineServicesAccountPage.clickContinue()

      Then("the 'Create your agent sign in details' screen is displayed")
      CreateYourAgentAccountPage.assertPageIsDisplayed()

      When("I select continue")
      SignInWithAgentAccountPage.clickContinue()

      Then("the 'Government Gateway Sign In' screen is displayed")
      GovernmentGatewaySignInPage.assertPageIsDisplayed()

      When("I sign in with valid credentials") // Stubbed journey
      signInAndDataSetupViaStubs()

      Then("I am taken to the Application Task List")
      ApplicationTaskList.assertPageIsDisplayed()
    }
  }
}
