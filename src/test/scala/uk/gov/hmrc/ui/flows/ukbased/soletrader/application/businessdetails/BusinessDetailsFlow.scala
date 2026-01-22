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

import uk.gov.hmrc.ui.flows.common.application.StubbedSignInData
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow
import StubbedSignInFlow.CompanyStatus.Blocked
import StubbedSignInFlow.CompanyStatus.Ok
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.DeceasedFlag
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow.Deceased.False
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow.Deceased.True
import uk.gov.hmrc.ui.pages.agentregistration.ApplyEntryPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.*
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.soletrader.businessdetails.AreYouTheBusinessOwnerPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.soletrader.businessdetails.CannotConfirmIdentityPage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage

/** Flow for completing the Business Details section of an agent registration application.
  *
  * UK-based -> LLP -> initial application
  */
object BusinessDetailsFlow:

  enum OnlineAgentsAccount:
    case HasOnlineAgentAccount, NoOnlineAgentAccount

  enum Deceased:
    case True, False

  // --- Public "journeys" (like ProvideDetailsFlow objects) ---

  object HasOnlineAgentAccount:
    def runFlow(): StubbedSignInData =
      startJourney()
      selectUkBased()
      selectSoleTraderBusinessSetup()
      confirmIfBusinessOwner()
      answerOnlineServicesAccount(OnlineAgentsAccount.HasOnlineAgentAccount)
      proceedToGovernmentGateway()
      val stubData = stubbedSignIn(Deceased.False)
      landOnTaskList()
      stubData

  object HasNoOnlineAccount:
    def runFlow(): StubbedSignInData =
      startJourney()
      selectUkBased()
      selectSoleTraderBusinessSetup()
      confirmIfBusinessOwner()
      answerOnlineServicesAccount(OnlineAgentsAccount.NoOnlineAgentAccount)
      proceedToGovernmentGateway()
      val stubData = stubbedSignIn(Deceased.False)
      landOnTaskList()
      stubData

  object IsDeceased:
    def runFlow(): Unit =
      startJourney()
      selectUkBased()
      selectSoleTraderBusinessSetup()
      confirmIfBusinessOwner()
      answerOnlineServicesAccount(OnlineAgentsAccount.HasOnlineAgentAccount)
      proceedToGovernmentGateway()
      stubbedSignIn(Deceased.True)
      landOnCannotConfirmIdentityPage()

  // --- Granular steps (each page gets a function) ---

  def startJourney(): Unit = ApplyEntryPage.open()

  def selectUkBased(): Unit =
    IsYourAgentBusinessBasedInTheUKPage.assertPageIsDisplayed()
    IsYourAgentBusinessBasedInTheUKPage.selectYes()
    IsYourAgentBusinessBasedInTheUKPage.clickContinue()

  def selectSoleTraderBusinessSetup(): Unit =
    HowIsYourBusinessSetUpPage.assertPageIsDisplayed()
    HowIsYourBusinessSetUpPage.selectSoleTrader()
    HowIsYourBusinessSetUpPage.clickContinue()

  def confirmIfBusinessOwner(): Unit =
    AreYouTheBusinessOwnerPage.assertPageIsDisplayed()
    AreYouTheBusinessOwnerPage.selectYes()
    AreYouTheBusinessOwnerPage.clickContinue()

  def answerOnlineServicesAccount(answer: OnlineAgentsAccount): Unit =
    answer match
      case OnlineAgentsAccount.HasOnlineAgentAccount =>
        HmrcOnlineServicesAccountPage.assertPageIsDisplayed()
        HmrcOnlineServicesAccountPage.selectYes()
        HmrcOnlineServicesAccountPage.clickContinue()
        SignInWithAgentAccountPage.assertPageIsDisplayed()
        SignInWithAgentAccountPage.clickContinue()

      case OnlineAgentsAccount.NoOnlineAgentAccount =>
        HmrcOnlineServicesAccountPage.assertPageIsDisplayed()
        HmrcOnlineServicesAccountPage.selectNo()
        HmrcOnlineServicesAccountPage.clickContinue()
        CreateYourAgentAccountPage.assertPageIsDisplayed()
        CreateYourAgentAccountPage.clickContinue()

  def proceedToGovernmentGateway(): Unit = GovernmentGatewaySignInPage.assertPageIsDisplayed()

  def stubbedSignIn(status: Deceased): StubbedSignInData =
    status match
      case True => StubbedSignInFlow.signInAndDataSetupViaStubsForAgent(deceasedFlag = DeceasedFlag.True)
      case False => StubbedSignInFlow.signInAndDataSetupViaStubsForAgent(deceasedFlag = DeceasedFlag.False)

  def landOnTaskList(): Unit = TaskListPage.assertPageIsDisplayed()

  def landOnCannotConfirmIdentityPage(): Unit = CannotConfirmIdentityPage.assertPageIsDisplayed()
