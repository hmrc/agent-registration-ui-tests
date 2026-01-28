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

package uk.gov.hmrc.ui.flows.common.application.agentstandards

import uk.gov.hmrc.ui.domain.BusinessType
import BusinessType.*
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.agentstandards.AgentStandardsPage

object AgentStandardsFlow:

  object AgreeToMeetStandards:

    def runFlow(businessType: BusinessType): Unit =
      startJourney()
      clickAgreeAndSave(businessType)

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertHmrcStandardsForAgentsStatus("Incomplete")
    TaskListPage.clickOnHmrcStandardsForAgentsLink()

  def clickAgreeAndSave(businessType: BusinessType): Unit =
    AgentStandardsPage.assertPageIsDisplayed()
    businessType match
      case SoleTrader => AgentStandardsPage.assertSoleTraderTextDisplayed()
      case LLP => AgentStandardsPage.assertPartnershipTextDisplayed()
    AgentStandardsPage.clickContinue()
