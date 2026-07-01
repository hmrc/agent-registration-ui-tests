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

package uk.gov.hmrc.ui.flows.common.application.riskingOutcome

import uk.gov.hmrc.ui.flows.common.application.StubbedSignInData
import uk.gov.hmrc.ui.pages.PageObject
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ProvideDetailsStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ApplicationStatusPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotMetIndividualsPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.ConditionsNotMetTaskListPage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage
import uk.gov.hmrc.ui.utils.AppConfig

object RiskingOutcomeFlow:

  object SignInAsApplicantAfterRiskingOutcome:
    def runFlow(stubbedSignInData: StubbedSignInData): Unit = signInToApplicationStatusPage(stubbedSignInData: StubbedSignInData)

  object signInAsPreviouslyUsedIndividual:
    def runFlow(
      stubbedSignInData: StubbedSignInData,
      linkId: String,
      username: String
    ): Unit =
      val individualRiskingStatusUrl = AppConfig.baseUrlAgentRegistrationFrontend + ProvideDetailsStatusPage.path + "/" + linkId
      val signInUrl =
        AppConfig.baseUrlGovernmentGateway +
          s"/bas-gateway/sign-in?continue_url=$individualRiskingStatusUrl&origin=agent-registration-frontend&affinityGroup=individual"
      PageObject.get(signInUrl)
      GovernmentGatewaySignInPage.assertPageIsDisplayed()
      GovernmentGatewaySignInPage.enterKnownUserId(username)
      GovernmentGatewaySignInPage.enterKnownPlanetId(stubbedSignInData.planetId)
      GovernmentGatewaySignInPage.clickContinue()
      ProvideDetailsStatusPage.assertPageIsDisplayed()

  object viewListOfIndividualActions:
    def runFlow(stubbedSignInData: StubbedSignInData): Unit =
      signInToApplicationStatusPage(stubbedSignInData: StubbedSignInData)
      viewApplicationStatusPage()
      viewIndividualFailuresPage()

  private def signInToApplicationStatusPage(stubbedSignInData: StubbedSignInData): Unit =
    val applicationStatusUrl = AppConfig.baseUrlAgentRegistrationFrontend + ApplicationSubmittedPage.path
    val signInUrl =
      AppConfig.baseUrlGovernmentGateway +
        s"/bas-gateway/sign-in?continue_url=$applicationStatusUrl&origin=agent-registration-frontend&affinityGroup=agent"
    PageObject.get(signInUrl)
    GovernmentGatewaySignInPage.assertPageIsDisplayed()
    GovernmentGatewaySignInPage.enterKnownUserId(stubbedSignInData.username)
    GovernmentGatewaySignInPage.enterKnownPlanetId(stubbedSignInData.planetId)
    GovernmentGatewaySignInPage.clickContinue()
    ApplicationStatusPage.assertPageIsDisplayed()

  private def viewApplicationStatusPage(): Unit =
    ApplicationStatusPage.assertPageIsDisplayed()
    ApplicationStatusPage.clickViewActionsToTakeButton()
    ConditionsNotMetTaskListPage.assertPageIsDisplayed()

  private def viewIndividualFailuresPage(): Unit =
    ConditionsNotMetTaskListPage.assertPageIsDisplayed()
    ConditionsNotMetTaskListPage.clickIndividualFailuresLink()
    ConditionsNotMetIndividualsPage.assertPageIsDisplayed()
