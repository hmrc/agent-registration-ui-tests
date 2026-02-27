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
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.BusinessDetails
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.ContactDetails
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.CompanyStatus.Ok
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.DeceasedFlag.False
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.FastForwardFlag.True
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.JourneyType.Agent
import uk.gov.hmrc.ui.flows.common.application.StubbedSignInFlow.signInAndDataSetupViaStubs
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.fastforwardlinks.FastForwardLinksPage

object FastForwardLinks:

  enum ApplicationProgress:
    case BusinessDetails, AgentDetails, ContactDetails, AmlsDetails, AgentStandards, Declaration

  object FastForward:
    def runFlow(
      applicationProgress: ApplicationProgress,
      businessType: BusinessType
    ): StubbedSignInData = {
      startJourney()
      val stubbedSignInData = signInAndDataSetupViaStubs(
        Agent,
        Ok,
        False,
        True
      )
      selectFastForwardLink(applicationProgress, businessType)
      stubbedSignInData
    }

  def startJourney(): Unit =
    FastForwardLinksPage.open()
    FastForwardLinksPage.assertPageIsDisplayed()
    FastForwardLinksPage.clickLogIn()

  def selectFastForwardLink(
    applicationProgress: ApplicationProgress,
    businessType: BusinessType
  ): Unit =
    applicationProgress match
      case BusinessDetails =>
        FastForwardLinksPage.clickAboutYourBusinessLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertBusinessDetailsStatus("Completed")
        TaskListPage.assertContactDetailsStatus("Incomplete")
      case ContactDetails =>
        FastForwardLinksPage.clickContactDetailsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertContactDetailsStatus("Completed")
        TaskListPage.assertAgentServicesAccountDetailsStatus("Incomplete")
      case AgentDetails =>
        FastForwardLinksPage.clickAgentDetailsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertAgentServicesAccountDetailsStatus("Completed")
        TaskListPage.assertAmlsDetailsStatus("Incomplete")
      case AmlsDetails =>
        FastForwardLinksPage.clickAmlsDetailsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertAmlsDetailsStatus("Completed")
        TaskListPage.assertHmrcStandardsForAgentsStatus("Incomplete")
      case AgentStandards =>
        FastForwardLinksPage.clickAgentStandardsLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertHmrcStandardsForAgentsStatus("Completed")
        TaskListPage.assertDeclarationStatus("Incomplete")
      case Declaration =>
        FastForwardLinksPage.clickDeclarationLink(businessType)
        TaskListPage.assertPageIsDisplayed()
        TaskListPage.assertDeclarationStatus("Completed")
