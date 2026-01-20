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

package uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.StubbedSignInData
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.StubbedSignInFlow
import uk.gov.hmrc.ui.pages.agentregistration.ApplyEntryPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.CreateYourAgentAccountPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.HmrcOnlineServicesAccountPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.HowIsYourBusinessSetUpPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.IsYourAgentBusinessBasedInTheUKPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.SignInWithAgentAccountPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.UserRolePage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage

/** Flow for completing the Business Details section of an agent registration application.
  *
  * As reflected in the package structure, this object handles the flow for:
  *   - agent type: UK-based
  *   - business type: Limited Liability Partnership (LLP)
  *   - an applicant making an initial application
  */
object BusinessDetailsFlow:

  object HasOnlineAgentAccount:
    def runFlow(): StubbedSignInData = completeBusinessDetailsSection(hasOnlineAgentsAccount = true)

  object NoOnlineAgentAccount:
    def runFlow(): StubbedSignInData = completeBusinessDetailsSection(hasOnlineAgentsAccount = false)

  /** Completes the Business Details section
    */
  private def completeBusinessDetailsSection(hasOnlineAgentsAccount: Boolean): StubbedSignInData =

    ApplyEntryPage.open()
    IsYourAgentBusinessBasedInTheUKPage.assertPageIsDisplayed()

    IsYourAgentBusinessBasedInTheUKPage.selectYes()
    IsYourAgentBusinessBasedInTheUKPage.clickContinue()

    HowIsYourBusinessSetUpPage.assertPageIsDisplayed()
    HowIsYourBusinessSetUpPage.selectSoleTrader()
    HowIsYourBusinessSetUpPage.clickContinue()

    UserRolePage.assertPageIsDisplayed()
    UserRolePage.selectAuthorised()
    UserRolePage.clickContinue()

    HmrcOnlineServicesAccountPage.assertPageIsDisplayed()

    if hasOnlineAgentsAccount
    then
      HmrcOnlineServicesAccountPage.selectYes()
      HmrcOnlineServicesAccountPage.clickContinue()
      SignInWithAgentAccountPage.assertPageIsDisplayed()
      SignInWithAgentAccountPage.clickContinue()
    else
      HmrcOnlineServicesAccountPage.selectNo()
      HmrcOnlineServicesAccountPage.clickContinue()
      CreateYourAgentAccountPage.assertPageIsDisplayed()
      CreateYourAgentAccountPage.clickContinue()

    GovernmentGatewaySignInPage.assertPageIsDisplayed()
    val stubbedSignInData: StubbedSignInData = StubbedSignInFlow.signInAndDataSetupViaStubsForAgent()
    TaskListPage.assertPageIsDisplayed()

    stubbedSignInData
