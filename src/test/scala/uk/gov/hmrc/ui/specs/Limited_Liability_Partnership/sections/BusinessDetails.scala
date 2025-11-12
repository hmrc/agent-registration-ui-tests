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

package uk.gov.hmrc.ui.specs.Limited_Liability_Partnership.sections

import uk.gov.hmrc.ui.flows.{StubbedSignIn, StubbedSignInData}
import uk.gov.hmrc.ui.pages.*
import uk.gov.hmrc.ui.pages.applyJourney.{CreateYourAgentAccountPage, HmrcOnlineServicesAccountPage, HowIsYourBusinessSetUpPage, IsYourAgentBusinessBasedInTheUKPage, SignInWithAgentAccountPage, TaskListPage, WhatTypeOfPartnershipPage}
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage

object BusinessDetails extends StubbedSignIn {

  def ukBasedLlpExistingOnlineAgentsAccount(): StubbedSignInData =
    ukBasedLlp(hasOnlineAgentsAccount = true)

  def ukBasedLlpNoOnlineAgentsAccount(): StubbedSignInData =
    ukBasedLlp(hasOnlineAgentsAccount = false)

  /** Core journey: UK-based LLP with a flag for online agents account. */
  private def ukBasedLlp(hasOnlineAgentsAccount: Boolean): StubbedSignInData = {

    IsYourAgentBusinessBasedInTheUKPage.open()
    IsYourAgentBusinessBasedInTheUKPage.assertPageIsDisplayed()

    IsYourAgentBusinessBasedInTheUKPage.selectYes()
    IsYourAgentBusinessBasedInTheUKPage.clickContinue()

    HowIsYourBusinessSetUpPage.assertPageIsDisplayed()
    HowIsYourBusinessSetUpPage.selectATypeOfPartnership()
    HowIsYourBusinessSetUpPage.clickContinue()

    WhatTypeOfPartnershipPage.assertPageIsDisplayed()
    WhatTypeOfPartnershipPage.selectLimitedLiabilityPartnership()
    WhatTypeOfPartnershipPage.clickContinue()

    HmrcOnlineServicesAccountPage.assertPageIsDisplayed()

    if (hasOnlineAgentsAccount) {
      HmrcOnlineServicesAccountPage.selectYes()
      HmrcOnlineServicesAccountPage.clickContinue()

      SignInWithAgentAccountPage.assertPageIsDisplayed()
      SignInWithAgentAccountPage.clickContinue()
    } else {
      HmrcOnlineServicesAccountPage.selectNo()
      HmrcOnlineServicesAccountPage.clickContinue()

      CreateYourAgentAccountPage.assertPageIsDisplayed()
      CreateYourAgentAccountPage.clickContinue()
    }

    GovernmentGatewaySignInPage.assertPageIsDisplayed()
    val stubData: StubbedSignInData = signInAndDataSetupViaStubs()
    TaskListPage.assertPageIsDisplayed()

    stubData
  }
}
