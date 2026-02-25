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

package uk.gov.hmrc.ui.flows.ukbased.limited_company

import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.CompanyStatus.{Blocked, Ok}
import uk.gov.hmrc.ui.flows.common.application.{StubbedSignInData, StubbedSignInFlow}
import uk.gov.hmrc.ui.pages.agentregistration.ApplyEntryPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.businessdetails.*
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.AreYouADirectorOfTheLimitedCompanyPage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage

/** Flow for completing the Business Details section of an agent registration application.
  *
  * UK-based -> Limited Company -> initial application
  */
object BusinessDetailsFlow:

  enum OnlineAgentsAccount:
    case HasOnlineAgentAccount, NoOnlineAgentAccount

  enum CompanyStatus:
    case Ok, Blocked

  // --- Public "journeys" (like ProvideDetailsFlow objects) ---

  object HasOnlineAgentAccount:
    def runFlow(): StubbedSignInData =
      startJourney()
      selectUkBased()
      selectLimitedCompanySetup()
      confirmIfDirectorOfLimitedCompany()
      answerOnlineServicesAccount(OnlineAgentsAccount.HasOnlineAgentAccount)
      proceedToGovernmentGateway()
      val stubData = stubbedSignIn(CompanyStatus.Ok)
      landOnTaskList()
      stubData

  object HasNoOnlineAccount:
    def runFlow(): StubbedSignInData =
      startJourney()
      selectUkBased()
      selectLimitedCompanySetup()
      confirmIfDirectorOfLimitedCompany()
      answerOnlineServicesAccount(OnlineAgentsAccount.NoOnlineAgentAccount)
      proceedToGovernmentGateway()
      val stubData = stubbedSignIn(CompanyStatus.Ok)
      landOnTaskList()
      stubData

  object HasBlockingStatus:
    def runFlow(): Unit =
      startJourney()
      selectUkBased()
      selectLimitedCompanySetup()
      confirmIfDirectorOfLimitedCompany()
      answerOnlineServicesAccount(OnlineAgentsAccount.HasOnlineAgentAccount)
      proceedToGovernmentGateway()
      stubbedSignIn(CompanyStatus.Blocked)
      landOnCannotRegisterPage()

  // --- Granular steps (each page gets a function) ---

  def startJourney(): Unit =
    ApplyEntryPage.open()
    IsYourAgentBusinessBasedInTheUKPage.assertPageIsDisplayed()

  def selectUkBased(): Unit =
    IsYourAgentBusinessBasedInTheUKPage.selectYes()
    IsYourAgentBusinessBasedInTheUKPage.clickContinue()
    HowIsYourBusinessSetUpPage.assertPageIsDisplayed()

  def selectLimitedCompanySetup(): Unit =
    HowIsYourBusinessSetUpPage.assertPageIsDisplayed()
    HowIsYourBusinessSetUpPage.selectLimitedCompany()
    HowIsYourBusinessSetUpPage.clickContinue()

  def confirmIfDirectorOfLimitedCompany(): Unit =
    AreYouADirectorOfTheLimitedCompanyPage.assertPageIsDisplayed()
    AreYouADirectorOfTheLimitedCompanyPage.selectYes()
    AreYouADirectorOfTheLimitedCompanyPage.clickContinue()

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

  def stubbedSignIn(status: CompanyStatus): StubbedSignInData =
    status match
      case CompanyStatus.Ok => StubbedSignInFlow.signInAndDataSetupViaStubsForAgent(Ok)
      case CompanyStatus.Blocked => StubbedSignInFlow.signInAndDataSetupViaStubsForAgent(Blocked)

  def landOnTaskList(): Unit = TaskListPage.assertPageIsDisplayed()

  def landOnCannotRegisterPage(): Unit = CannotRegisterPage.assertPageIsDisplayed()
