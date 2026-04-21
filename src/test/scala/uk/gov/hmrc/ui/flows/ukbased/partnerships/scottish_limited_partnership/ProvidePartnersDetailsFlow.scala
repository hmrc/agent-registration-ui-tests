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

package uk.gov.hmrc.ui.flows.ukbased.partnerships.scottish_limited_partnership

import uk.gov.hmrc.ui.flows.common.application.StubbedSignInData
import uk.gov.hmrc.ui.pages.PageObject
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.*
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.pages.stubs.{AgentExternalStubConfigureUserPage, AgentExternalStubCreateUserPage, AgentExternalStubUserPage, GovernmentGatewaySignInPage}
import uk.gov.hmrc.ui.utils.PasscodeHelper
import uk.gov.hmrc.ui.utils.RichMatchers.shouldBe

object ProvidePartnersDetailsFlow:

  enum listProgress:

    case complete
    case partial

  object ProvidePartnersDetails:

    def runFlowWithLink(
      stubData: StubbedSignInData,
      link: String,
      progress: listProgress,
      partnersName: Option[String] = None,
      allPartnersNames: Option[List[String]] = None
    ): Unit =
      signOut()
      PageObject.get(link)
      val (bearerToken, sessionId) = signIn(stubData.planetId, partnersName)
      confirmDetails()
      provideTelephoneNumber()
      provideEmailAddress(stubData.copy(bearerToken = bearerToken, sessionId = sessionId))
      provideUtr()
      approveApplication()
      agreeStandards()
      checkYourAnswers()
      finishAndSignOut()
      PageObject.get("http://localhost:22201/agent-registration/apply/task-list")
      returnToTasklist(stubData)
      progress match
        case listProgress.complete => checkPartnersListProgressComplete()
        case listProgress.partial => checkPartnersListProgressPartial(allPartnersNames)

  def getProvideDetailsLink: String =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.clickAskPartnersAndOtherAdvisorsToSignInLink()
    AskPartnersToSignInPage.assertPageIsDisplayed()
    val link = AskPartnersToSignInPage.getShareLinkText
    AskPartnersToSignInPage.clickContinue()
    link

  def signOut(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.clickSignOutLink()

  def signIn(
    planet: String,
    partnerNames: Option[String] = None
  ): (String, String) =
    println(s"🔍 signIn called with partnerNames: $partnerNames")
    SignInAndConfirmDetailsPage.clickContinue()
    GovernmentGatewaySignInPage.assertPageIsDisplayed()
    GovernmentGatewaySignInPage.enterRandomUsername()
    GovernmentGatewaySignInPage.enterKnownPlanetId(planet)
    GovernmentGatewaySignInPage.clickContinue()
    AgentExternalStubCreateUserPage.assertPageIsDisplayed()
    AgentExternalStubCreateUserPage.selectCurrentUserLink()
    AgentExternalStubUserPage.assertPageIsDisplayed()
    val bearerToken = AgentExternalStubUserPage.bearerToken // capture bearer token to use in email verification call
    val sessionId = AgentExternalStubUserPage.sessionId // capture sessionId to use in email verification call
    AgentExternalStubUserPage.clickBrowserBack()
    AgentExternalStubCreateUserPage.assertPageIsDisplayed()
    AgentExternalStubCreateUserPage.selectAffinityGroupIndividual()
    AgentExternalStubCreateUserPage.selectEnrolment("HMRC-PT")
    AgentExternalStubCreateUserPage.clickContinue()
    AgentExternalStubConfigureUserPage.assertPageIsDisplayed()
    // Use captured partner name if provided, otherwise use default
    val nameToUse = partnerNames.getOrElse("Beverly Hills")
    AgentExternalStubConfigureUserPage.enterName(nameToUse)
    AgentExternalStubConfigureUserPage.clickContinue()
    (bearerToken, sessionId)

  def confirmDetails(): Unit =
    ConfirmYourDetailsPage.assertPageIsDisplayed()
    ConfirmYourDetailsPage.selectYes()
    ConfirmYourDetailsPage.clickContinue()

  def provideTelephoneNumber(): Unit =
    ProvideDetailsTelephoneNumberPage.assertPageIsDisplayed()
    ProvideDetailsTelephoneNumberPage.enterTelephoneNumber()
    ProvideDetailsTelephoneNumberPage.clickContinue()

  def provideEmailAddress(stubData: StubbedSignInData): Unit =
    ProvideDetailsEmailAddressPage.assertPageIsDisplayed()
    ProvideDetailsEmailAddressPage.enterEmailAddress()
    ProvideDetailsEmailAddressPage.clickContinue()

    // get email verification code from test only page
    EmailVerificationTestOnlyPage.assertPageIsDisplayed()
    EmailVerificationTestOnlyPage.clickContinue()

    // confirm email by providing confirmation code
    val passcode = PasscodeHelper.getPasscode(stubData.bearerToken, stubData.sessionId)
    ProvideDetailsConfirmEmailPage.enterConfirmationCode(passcode)
    ProvideDetailsConfirmEmailPage.clickContinue()

  def provideUtr(): Unit =
    ProvideDetailsUtrPage.assertPageIsDisplayed()
    ProvideDetailsUtrPage.selectYes()
    ProvideDetailsUtrPage.enterUtr()
    ProvideDetailsUtrPage.clickContinue()

  def approveApplication(): Unit =
    ApproveApplicationPage.assertPageIsDisplayed()
    ApproveApplicationPage.selectYes()
    ApproveApplicationPage.clickContinue()

  def agreeStandards(): Unit =
    ProvideDetailsAgreeStandardsPage.assertPageIsDisplayed()
    ProvideDetailsAgreeStandardsPage.clickContinue()

  def checkYourAnswers(): Unit =
    ProvideDetailsCheckYourAnswersPage.assertPageDisplayed()
    ProvideDetailsCheckYourAnswersPage.assertSummaryRow("Telephone number", "07777777777")
    ProvideDetailsCheckYourAnswersPage.assertSummaryRow("Email address", "individual@email.com")
    ProvideDetailsCheckYourAnswersPage.assertSummaryRow("Do you have a Self Assessment Unique Taxpayer Reference?", "Yes")
    ProvideDetailsCheckYourAnswersPage.assertSummaryRow("Self Assessment Unique Taxpayer Reference", "1234567890")
    ProvideDetailsCheckYourAnswersPage.clickContinue()

  def finishAndSignOut(): Unit =
    ProvideDetailsConfirmationPage.assertPageIsDisplayed()
    ProvideDetailsConfirmationPage.clickFinishAndSignOut()

  def returnToTasklist(stubData: StubbedSignInData): Unit =
    GovernmentGatewaySignInPage.assertPageIsDisplayed()
    GovernmentGatewaySignInPage.enterKnownUserId(stubData.username)
    GovernmentGatewaySignInPage.enterKnownPlanetId(stubData.planetId)
    GovernmentGatewaySignInPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()

  def checkPartnersListProgressComplete(): Unit =
    TaskListPage.assertAskPartnersAndTaxAdvisorsToSignInStatus("Completed")
    TaskListPage.assertCheckProvidedDetailsStatus("Completed")

  def checkPartnersListProgressPartial(allPartnerNames: Option[List[String]] = None): Unit =
    TaskListPage.assertAskPartnersAndTaxAdvisorsToSignInStatus("Completed")
    TaskListPage.assertCheckProvidedDetailsStatus("Incomplete")
    allPartnerNames match
      case Some(names) if names.size >= 2 =>
        TaskListPage.clickCheckProvidedDetailsLink()
        CheckWhoProvidedDetailsPage.assertPageIsDisplayed()
        CheckWhoProvidedDetailsPage.detailsProvided(names(0)) shouldBe "Yes"
        CheckWhoProvidedDetailsPage.detailsProvided(names(1)) shouldBe "No"
        // Navigate back to task list for next partner
        PageObject.get("http://localhost:22201/agent-registration/apply/task-list")
        TaskListPage.assertPageIsDisplayed()
      case _ =>
        TaskListPage.clickCheckProvidedDetailsLink()
        CheckWhoProvidedDetailsPage.assertPageIsDisplayed()
        CheckWhoProvidedDetailsPage.detailsProvided("Steve Austin") shouldBe "Yes"
        CheckWhoProvidedDetailsPage.detailsProvided("Beverly Hills") shouldBe "Yes"
        // Navigate back to task list for next partner
        PageObject.get("http://localhost:22201/agent-registration/apply/task-list")
        TaskListPage.assertPageIsDisplayed()
