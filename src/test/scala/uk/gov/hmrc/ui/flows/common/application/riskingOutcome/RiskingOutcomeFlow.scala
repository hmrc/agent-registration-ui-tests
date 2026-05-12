package uk.gov.hmrc.ui.flows.common.application.riskingOutcome

import uk.gov.hmrc.ui.flows.common.application.StubbedSignInData
import uk.gov.hmrc.ui.pages.PageObject
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage
import uk.gov.hmrc.ui.utils.AppConfig

object RiskingOutcomeFlow:

/** Sign in again using the same credentials. Navigate directly to the application-status page — the frontend reads the updated status
 * from Mongo and shows the outcome page.
 */
  object SignInAsApplicantAfterRiskingOutcome:
   def runFlow(stubbedSignInData: StubbedSignInData): Unit =
    val applicationStatusUrl = AppConfig.baseUrlAgentRegistrationFrontend + ApplicationSubmittedPage.path
    val signInUrl =
      AppConfig.baseUrlGovernmentGateway +
        s"/bas-gateway/sign-in?continue_url=$applicationStatusUrl&origin=agent-registration-frontend&affinityGroup=agent"
    PageObject.get(signInUrl)
    GovernmentGatewaySignInPage.assertPageIsDisplayed()
    GovernmentGatewaySignInPage.enterKnownUserId(stubbedSignInData.username)
    GovernmentGatewaySignInPage.enterKnownPlanetId(stubbedSignInData.planetId)
    GovernmentGatewaySignInPage.clickContinue()
  