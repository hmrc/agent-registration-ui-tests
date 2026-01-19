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

package uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.StubbedSignInFlow.JourneyType.Agent
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.StubbedSignInFlow.JourneyType.Individual
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.StubbedSignInFlow.JourneyType.IndividualWithUtr
import uk.gov.hmrc.ui.pages.*
import uk.gov.hmrc.ui.pages.stubs.*

/** Encapsulates the Agents External Stubs sign-in/setup screens. */
object StubbedSignInFlow:

  enum JourneyType:
    case Agent, Individual, IndividualWithUtr

  enum CompanyStatus:
    case Ok, Blocked

  /** Public entry point—keeps all logic in one place. */
  def signInAndDataSetupViaStubs(
    journey: JourneyType,
    companyStatus: CompanyStatus = CompanyStatus.Ok
  ): StubbedSignInData =

    // 1) Government Gateway sign-in
    val username = governmentGatewaySignIn()

    // 2) Capture bearer token + session
    val (bearerToken, sessionId) = captureBearerTokenAndSession()

    // 3) Configure user on stubs (journey-specific)
    journey match
      case JourneyType.Agent => configureForAgent(companyStatus)

      case JourneyType.Individual => configureForIndividual(hasUtr = false)

      case JourneyType.IndividualWithUtr => configureForIndividual(hasUtr = true)

    StubbedSignInData(
      username,
      bearerToken,
      sessionId
    )

  // --- Convenience wrappers for callers that know the journey ---

  def signInAndDataSetupViaStubsForAgent(
    companyStatus: CompanyStatus = CompanyStatus.Ok
  ): StubbedSignInData = signInAndDataSetupViaStubs(Agent, companyStatus)

  def signInAndDataSetupViaStubsForIndividual(): StubbedSignInData = signInAndDataSetupViaStubs(Individual)

  def signInAndDataSetupViaStubsForIndividualWithUtr(): StubbedSignInData = signInAndDataSetupViaStubs(IndividualWithUtr)

  // --- Granular steps (so you can fork cleanly) ---

  private def governmentGatewaySignIn(): String =
    GovernmentGatewaySignInPage.assertPageIsDisplayed()
    val username = GovernmentGatewaySignInPage.enterRandomUsername()
    GovernmentGatewaySignInPage.enterRandomPlanetId()
    GovernmentGatewaySignInPage.clickContinue()
    username

  private def captureBearerTokenAndSession(): (String, String) =
    AgentExternalStubCreateUserPage.assertPageIsDisplayed()
    AgentExternalStubCreateUserPage.selectCurrentUserLink()
    AgentExternalStubUserPage.assertPageIsDisplayed()
    val bearerToken = AgentExternalStubUserPage.bearerToken
    val sessionId = AgentExternalStubUserPage.sessionId
    AgentExternalStubUserPage.clickBrowserBack()
    (bearerToken, sessionId)

  private def configureForAgent(companyStatus: CompanyStatus): Unit =
    selectAffinityGroupAgent()
    selectNoEnrolmentAndContinue()
    continueFromConfigureUser()
    configureGrs(companyStatus) // <-- NEW fork lives here, right where it matters

  private def configureForIndividual(hasUtr: Boolean): Unit =
    selectAffinityGroupIndividual()
    selectNoEnrolmentAndContinue()
    AgentExternalStubConfigureUserPage.assertPageIsDisplayed()

    if (hasUtr) then enterUtrEnrolmentData()

    AgentExternalStubConfigureUserPage.clickContinue()

  // --- Tiny helpers to keep each screen's logic isolated ---

  private def selectAffinityGroupAgent(): Unit = AgentExternalStubCreateUserPage.selectAffinityGroupAgent()

  private def selectAffinityGroupIndividual(): Unit = AgentExternalStubCreateUserPage.selectAffinityGroupIndividual()

  private def selectNoEnrolmentAndContinue(): Unit =
    AgentExternalStubCreateUserPage.selectEnrolmentNone()
    AgentExternalStubCreateUserPage.clickContinue()

  private def continueFromConfigureUser(): Unit =
    AgentExternalStubConfigureUserPage.assertPageIsDisplayed()
    AgentExternalStubConfigureUserPage.clickContinue()

  private def configureGrs(companyStatus: CompanyStatus): Unit =
    GrsDataSetupPage.assertPageIsDisplayed()

    companyStatus match
      case CompanyStatus.Ok => ()

      case CompanyStatus.Blocked => GrsDataSetupPage.enterCompanyNumber()

    GrsDataSetupPage.clickContinue()

  private def enterUtrEnrolmentData(): Unit =
    AgentExternalStubConfigureUserPage.enterServiceKey()
    AgentExternalStubConfigureUserPage.enterIdentifierName()
    AgentExternalStubConfigureUserPage.enterIdentifierValue()
