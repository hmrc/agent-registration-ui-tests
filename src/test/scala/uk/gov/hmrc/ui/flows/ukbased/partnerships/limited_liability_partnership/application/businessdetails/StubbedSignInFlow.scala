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

package uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails

import uk.gov.hmrc.ui.pages.*
import uk.gov.hmrc.ui.pages.stubs.AgentExternalStubConfigureUserPage
import uk.gov.hmrc.ui.pages.stubs.AgentExternalStubCreateUserPage
import uk.gov.hmrc.ui.pages.stubs.AgentExternalStubUserPage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage
import uk.gov.hmrc.ui.pages.stubs.GrsDataSetupPage

/** Encapsulates the Agents External Stubs sign-in/setup screens. */
object StubbedSignInFlow:

  /** Runs the stubbed sign-in flow and returns the generated username (if needed it later).
    */
  def signInAndDataSetupViaStubs(): StubbedSignInData =
    // We assume the test is already on GovernmentGatewaySignInPage
    GovernmentGatewaySignInPage.assertPageIsDisplayed()

    // Create random creds (page returns the generated values)
    val username = GovernmentGatewaySignInPage.enterRandomUsername()
    GovernmentGatewaySignInPage.enterRandomPlanetId()
    GovernmentGatewaySignInPage.clickContinue()

    // Capture Bearer Token and Session ID
    AgentExternalStubCreateUserPage.assertPageIsDisplayed()
    AgentExternalStubCreateUserPage.selectCurrentUserLink()
    AgentExternalStubUserPage.assertPageIsDisplayed()
    val bearerToken = AgentExternalStubUserPage.bearerToken
    val sessionId = AgentExternalStubUserPage.sessionId
    AgentExternalStubUserPage.clickBrowserBack()

    // Configure user on stubs
    AgentExternalStubCreateUserPage.selectAffinityGroupAgent()
    AgentExternalStubCreateUserPage.selectEnrolmentNone()
    AgentExternalStubCreateUserPage.clickContinue()

    AgentExternalStubConfigureUserPage.assertPageIsDisplayed()
    AgentExternalStubConfigureUserPage.clickContinue()

    // GRS stub screen (heading may be dynamic; your page can set skipH1Assertion = true)
    GrsDataSetupPage.assertPageIsDisplayed()
    GrsDataSetupPage.clickContinue()

    StubbedSignInData(
      username,
      bearerToken,
      sessionId
    )
