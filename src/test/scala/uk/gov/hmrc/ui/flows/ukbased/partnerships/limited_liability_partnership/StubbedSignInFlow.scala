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

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.StubbedSignInFlow.JourneyType.{Agent, Individual, IndividualWithUtr}
import uk.gov.hmrc.ui.pages.*
import uk.gov.hmrc.ui.pages.stubs.*

/** Encapsulates the Agents External Stubs sign-in/setup screens. */
object StubbedSignInFlow:

  enum JourneyType:
    case Agent, Individual, IndividualWithUtr

  /** Public entry point—keeps all logic in one place. */
  def signInAndDataSetupViaStubs(journey: JourneyType): StubbedSignInData =
    // 1) Government Gateway sign-in
    GovernmentGatewaySignInPage.assertPageIsDisplayed()
    val username = GovernmentGatewaySignInPage.enterRandomUsername()
    GovernmentGatewaySignInPage.enterRandomPlanetId()
    GovernmentGatewaySignInPage.clickContinue()

    // 2) Capture bearer token + session
    AgentExternalStubCreateUserPage.assertPageIsDisplayed()
    AgentExternalStubCreateUserPage.selectCurrentUserLink()
    AgentExternalStubUserPage.assertPageIsDisplayed()
    val bearerToken = AgentExternalStubUserPage.bearerToken
    val sessionId = AgentExternalStubUserPage.sessionId
    AgentExternalStubUserPage.clickBrowserBack()

    // 3) Configure user on stubs (journey-specific)
    journey match
      case JourneyType.Agent => configureForAgent()
      case JourneyType.Individual => configureForIndividual()
      case JourneyType.IndividualWithUtr => configureForIndividual(hasUtr = true)

    StubbedSignInData(
      username,
      bearerToken,
      sessionId
    )

  // --- Convenience wrappers for callers that know the journey ---
  def signInAndDataSetupViaStubsForAgent(): StubbedSignInData = signInAndDataSetupViaStubs(Agent)
  def signInAndDataSetupViaStubsForIndividual(): StubbedSignInData = signInAndDataSetupViaStubs(Individual)
  def signInAndDataSetupViaStubsForIndividualWithUtr(): StubbedSignInData = signInAndDataSetupViaStubs(IndividualWithUtr)

  // --- Private helpers keep journey-specific steps isolated ---
  private def configureForAgent(): Unit =
    AgentExternalStubCreateUserPage.selectAffinityGroupAgent()
    AgentExternalStubCreateUserPage.selectEnrolmentNone()
    AgentExternalStubCreateUserPage.clickContinue()
    AgentExternalStubConfigureUserPage.assertPageIsDisplayed()
    AgentExternalStubConfigureUserPage.clickContinue()
    // GRS stub screen
    GrsDataSetupPage.assertPageIsDisplayed()
    GrsDataSetupPage.clickContinue()

  private def configureForIndividual(hasUtr: Boolean = false): Unit =
    AgentExternalStubCreateUserPage.selectAffinityGroupIndividual()
    AgentExternalStubCreateUserPage.selectEnrolmentNone()
    AgentExternalStubCreateUserPage.clickContinue()
    AgentExternalStubConfigureUserPage.assertPageIsDisplayed()

    if (hasUtr) {
      AgentExternalStubConfigureUserPage.enterServiceKey()
      AgentExternalStubConfigureUserPage.enterIdentifierName()
      AgentExternalStubConfigureUserPage.enterIdentifierValue()
    }

    AgentExternalStubConfigureUserPage.clickContinue()
