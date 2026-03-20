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

package uk.gov.hmrc.ui.flows.common.application.providedetails

import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInData
import uk.gov.hmrc.ui.pages.PageObject
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ApproveApplicationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.AskPartnersToSignInPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.CheckWhoProvidedDetailsPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ConfirmYourDetailsPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ProvideDetailsAgreeStandardsPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ProvideDetailsCheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ProvideDetailsConfirmEmailPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ProvideDetailsConfirmationPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ProvideDetailsEmailAddressPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ProvideDetailsTelephoneNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ProvideDetailsUtrPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.SignInAndConfirmDetailsPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.EmailVerificationTestOnlyPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.soletrader.proveyouridentity.ProveYourIdentityPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.soletrader.proveyouridentity.YouHaveProvenYourIdentityPage
import uk.gov.hmrc.ui.pages.stubs.AgentExternalStubConfigureUserPage
import uk.gov.hmrc.ui.pages.stubs.AgentExternalStubCreateUserPage
import uk.gov.hmrc.ui.pages.stubs.AgentExternalStubUserPage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage
import uk.gov.hmrc.ui.utils.PasscodeHelper
import uk.gov.hmrc.ui.utils.RichMatchers.shouldBe

object ProvideIndividualDetailsFlow:

  enum listProgress:

    case complete
    case partial

  object ProvideIndividualDetails:
    def runFlow(
      stubData: StubbedSignInData,
      progress: listProgress,
      businessType: BusinessType
    ): Unit =
      val link = getProvideDetailsLink
      signOut()
      PageObject.get(link)
      val (bearerToken, sessionId) = signIn(stubData.planetId, businessType)
      confirmDetails()
      provideTelephoneNumber()
      provideEmailAddress(stubData.copy(bearerToken = bearerToken, sessionId = sessionId))
      provideUtr()
      approveApplication()
      agreeStandards()
      checkYourAnswers()
      finishAndSignOut()
      PageObject.get("http://localhost:22201/agent-registration/apply/task-list")
      returnToApplication(stubData)
      progress match
        case listProgress.complete => checkPartnerListProgressComplete()
        case listProgress.partial => checkPartnerListProgressPartial()

  object ProvideIndividualDetailsSoleTrader:
    def runFlow(
      stubData: StubbedSignInData,
      progress: listProgress
    ): Unit =
      startJourney()
      val (bearerToken, sessionId) = signIn(stubData.planetId, BusinessType.SoleTrader)
      confirmDetails()
      provideUtr()
      identityProvenConfirmation()
      returnToApplication(stubData)
      checkProveYourIdentityProgressComplete()

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.clickOnProveYourIdentityLink()
    ProveYourIdentityPage.assertPageIsDisplayed()
    ProveYourIdentityPage.clickContinue()

  def getProvideDetailsLink: String =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.clickAskPartnersAndAdvisorsToSignInLink()
    AskPartnersToSignInPage.assertPageIsDisplayed()
    val link = AskPartnersToSignInPage.getShareLinkText
    AskPartnersToSignInPage.clickContinue()
    link

  def signOut(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.clickSignOutLink()

  def signIn(
    planet: String,
    businessType: BusinessType
  ): (String, String) =
    if businessType != BusinessType.SoleTrader then
      SignInAndConfirmDetailsPage.assertPageIsDisplayed()
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
    AgentExternalStubConfigureUserPage.enterName("ST Name ST Lastname")
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

  def returnToApplication(stubData: StubbedSignInData): Unit =
    GovernmentGatewaySignInPage.assertPageIsDisplayed()
    GovernmentGatewaySignInPage.enterKnownUserId(stubData.username)
    GovernmentGatewaySignInPage.enterKnownPlanetId(stubData.planetId)
    GovernmentGatewaySignInPage.clickContinue()

  def checkPartnerListProgressComplete(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertAskPartnersAndAdvisorsToSignInStatus("Completed")
    TaskListPage.assertCheckProvidedDetailsStatus("Completed")

  def checkPartnerListProgressPartial(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertAskPartnersAndAdvisorsToSignInStatus("Completed")
    TaskListPage.assertCheckProvidedDetailsStatus("Incomplete")
    TaskListPage.clickCheckProvidedDetailsLink()
    CheckWhoProvidedDetailsPage.assertPageIsDisplayed()
    CheckWhoProvidedDetailsPage.detailsProvided("Bobby Boucher") shouldBe "Yes"
    CheckWhoProvidedDetailsPage.detailsProvided("Sonny Koufax") shouldBe "No"

  def identityProvenConfirmation(): Unit =
    YouHaveProvenYourIdentityPage.assertPageIsDisplayed()
    YouHaveProvenYourIdentityPage.assertConfirmationTextDisplayed()
    YouHaveProvenYourIdentityPage.clickReturnToApplicationLink()

  def checkProveYourIdentityProgressComplete(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertProveYourIdentityStatus("Completed")
