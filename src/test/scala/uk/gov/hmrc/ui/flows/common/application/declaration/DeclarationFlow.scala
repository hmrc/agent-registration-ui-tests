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
import uk.gov.hmrc.ui.utils.MongoHelper
import uk.gov.hmrc.ui.utils.MongoHelper.firstIndividual
import uk.gov.hmrc.ui.utils.MongoHelper.getNestedString
import uk.gov.hmrc.ui.utils.MongoHelper.getTopLevelString

object DeclarationFlow:

  object AcceptDeclaration:

    def runFlow(
      businessType: BusinessType,
      soleTraderOwner: Boolean = true,
      fastForwardUsed: Boolean = false
    ): Unit =
      startJourney()
      clickAcceptAndSend(businessType, soleTraderOwner)
      completeJourney(
        businessType,
        soleTraderOwner,
        fastForwardUsed
      )

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertDeclarationStatus("Incomplete")
    TaskListPage.clickOnDeclarationLink()

  def clickAcceptAndSend(
    businessType: BusinessType,
    soleTraderOwner: Boolean
  ): Unit =
    DeclarationPage.assertPageIsDisplayed()
    businessType match
      case SoleTrader =>
        if soleTraderOwner then DeclarationPage.assertNoAuthorisedByTextDisplayed() else DeclarationPage.assertAuthorisedByTextDisplayed("ST Name ST Lastname")
      case LLP => DeclarationPage.assertAuthorisedByTextDisplayed("Test Partnership")
      case GeneralPartnership => DeclarationPage.assertAuthorisedByTextDisplayed("Electronicsson Group")
      case LimitedPartnership => DeclarationPage.assertAuthorisedByTextDisplayed("Test Partnership")
      case LimitedCompany => DeclarationPage.assertAuthorisedByTextDisplayed("Test Company Ltd")
      case ScottishLimitedPartnership => DeclarationPage.assertAuthorisedByTextDisplayed("Test Partnership")
      case ScottishPartnership => DeclarationPage.assertAuthorisedByTextDisplayed("Electronicsson Group")
    DeclarationPage.clickContinue()

  def completeJourney(
    businessType: BusinessType,
    soleTraderOwner: Boolean,
    fastForwardUsed: Boolean
  ): Unit =
    ApplicationSubmittedPage.assertPageIsDisplayed()
    ApplicationSubmittedPage.assertConfirmationTitle("You’ve applied for an agent services account")
    // Assert that the application risking record has been created in MongoDB and has the expected values
    val applicationReference = ApplicationSubmittedPage.getApplicationReference
    val record = MongoHelper.findByApplicationReference(applicationReference)
    val doc = record.getOrElse(throw new AssertionError(s"No Mongo record for $applicationReference"))
    val status = getTopLevelString(doc, "status")
    val providedName = getNestedString(firstIndividual(doc), "providedName")

    assert(status == "ReadyForSubmission")
    businessType match
      case SoleTrader => if fastForwardUsed then assert(providedName == "ST Name ST Lastname") else assert(providedName == "Test User")
      case LLP => assert(providedName == "Test Partnership")
      case GeneralPartnership => assert(providedName == "Bobby Boucher")
      case LimitedPartnership => assert(providedName == "TBC")
      case LimitedCompany => assert(providedName == "TBC")
      case ScottishLimitedPartnership => assert(providedName == "TBC")
      case ScottishPartnership => assert(providedName == "TBC")
