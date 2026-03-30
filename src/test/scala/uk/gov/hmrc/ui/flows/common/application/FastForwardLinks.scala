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

package uk.gov.hmrc.ui.flows.common.application

import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentDetails
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AmlsDetails
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AskPartnersAndAdvisorsToSignIn
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.BusinessDetails
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.CheckProvidedDetails
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.ContactDetails
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.PartnersAndAdvisors
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.ProveYourIdentity
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.CompanyStatus.Ok
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.DeceasedFlag.False
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.FastForwardFlag.True
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.JourneyType.Agent
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.signInAndDataSetupViaStubs
import uk.gov.hmrc.ui.pages.PageObject
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.fastforwardlinks.FastForwardLinksPage
import uk.gov.hmrc.ui.pages.stubs.GovernmentGatewaySignInPage
import uk.gov.hmrc.ui.utils.AppConfig

object FastForwardLinks:

  enum ApplicationProgress:
    case BusinessDetails, AgentDetails, ContactDetails, AmlsDetails, AgentStandards, ProveYourIdentity,
      PartnersAndAdvisors, AskPartnersAndAdvisorsToSignIn, CheckProvidedDetails, Declaration

  object FastForward:

    /** Use when the test does not need the bearer token / session ID. Navigates directly to the fast-forward page, skipping the stub sign-in screens entirely.
      */
    def runFlow(
      applicationProgress: ApplicationProgress,
      businessType: BusinessType
    ): Unit = {
      startJourneyDirect()
      selectFastForwardLink(applicationProgress, businessType)
    }

    /** Use when the test needs the bearer token / session ID (e.g. email verification). */
    def runFlowWithStubData(
      applicationProgress: ApplicationProgress,
      businessType: BusinessType
    ): StubbedSignInData = {
      startJourneyViaStubs()
      val stubbedSignInData = signInAndDataSetupViaStubs(
        Agent,
        Ok,
        False,
        True
      )
      selectFastForwardLink(applicationProgress, businessType)
      stubbedSignInData
    }

  /* /** Use when the test needs to specify number of directors for GRS stub data. */
    def runFlowWithNumberOfDirectors(
      applicationProgress: ApplicationProgress,
      businessType: BusinessType,
      numberOfDirectors: Option[Int]
    ): Unit = {
      startJourneyViaStubs()
      signInAndDataSetupViaStubs(
        Agent,
        Ok,
        False,
        True,
        None,
        numberOfDirectors
      )
      selectFastForwardLink(applicationProgress, businessType)
    }*/

  def startJourneyViaStubs(): Unit =
    val continueUrl = AppConfig.baseUrlAgentRegistrationFrontend + FastForwardLinksPage.path
    val signInUrl =
      AppConfig.baseUrlGovernmentGateway +
        s"/bas-gateway/sign-in?continue_url=$continueUrl&origin=agent-registration-frontend&affinityGroup=agent"
    PageObject.get(signInUrl)
    GovernmentGatewaySignInPage.assertPageIsDisplayed()

  def startJourneyDirect(): Unit =
    FastForwardLinksPage.open()
    FastForwardLinksPage.assertPageIsDisplayed()

  def selectFastForwardLink(
    applicationProgress: ApplicationProgress,
    businessType: BusinessType
  ): Unit =
    applicationProgress match
      case BusinessDetails =>
        FastForwardLinksPage.clickAboutYourBusinessLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertBusinessDetailsStatus("Completed")
      case ContactDetails =>
        FastForwardLinksPage.clickContactDetailsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertContactDetailsStatus("Completed")
      case AgentDetails =>
        FastForwardLinksPage.clickAgentDetailsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertAgentServicesAccountDetailsStatus("Completed")
      case AmlsDetails =>
        FastForwardLinksPage.clickAmlsDetailsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertAmlsDetailsStatus("Completed")
      case AgentStandards =>
        FastForwardLinksPage.clickAgentStandardsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertHmrcStandardsForAgentsStatus("Completed")
      case PartnersAndAdvisors =>
        FastForwardLinksPage.clickPartnersAndAdvisorsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertPartnersAndAdvisorsStatus("Completed")
      case AskPartnersAndAdvisorsToSignIn =>
        FastForwardLinksPage.clickAskPartnersAndAdvisorsToSignInLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertAskPartnersAndAdvisorsToSignInStatus("Completed")
      case CheckProvidedDetails =>
        FastForwardLinksPage.clickCheckProvidedDetailsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertCheckProvidedDetailsStatus("Completed")
      case ProveYourIdentity =>
        FastForwardLinksPage.clickProveYourIdentityLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertProveYourIdentityStatus("Completed")
      case Declaration =>
        FastForwardLinksPage.clickDeclarationLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertDeclarationStatus("Completed")
