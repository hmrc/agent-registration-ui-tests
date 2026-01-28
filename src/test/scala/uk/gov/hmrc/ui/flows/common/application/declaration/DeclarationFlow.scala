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

package uk.gov.hmrc.ui.flows.common.application.declaration

import uk.gov.hmrc.ui.domain.BusinessType
import BusinessType.*
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.declaration.DeclarationPage

object DeclarationFlow:

  object AcceptDeclaration:

    def runFlow(businessType: BusinessType): Unit =
      startJourney()
      clickAcceptAndSave(businessType)
      completeJourney()

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertDeclarationStatus("Incomplete")
    TaskListPage.clickOnDeclarationLink()

  def clickAcceptAndSave(businessType: BusinessType): Unit =
    DeclarationPage.assertPageIsDisplayed()
    businessType match
      case SoleTrader => DeclarationPage.assertAuthorisedByTextNotDisplayed()
      case LLP => DeclarationPage.assertAuthorisedByTextDisplayed()
    DeclarationPage.clickContinue()

  def completeJourney(): Unit =
    ApplicationSubmittedPage.assertPageIsDisplayed()
    ApplicationSubmittedPage.assertCopyLinkButtonText("Copy link to clipboard")
    ApplicationSubmittedPage.clickCopyLinkButton()
    ApplicationSubmittedPage.assertCopyLinkButtonText("Link copied")
