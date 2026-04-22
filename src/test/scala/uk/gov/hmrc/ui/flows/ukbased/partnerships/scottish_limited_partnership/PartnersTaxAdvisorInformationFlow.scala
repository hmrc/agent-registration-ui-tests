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

import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.*
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.scottish_limited_partnership.PartnersAndOtherTaxAdvisersCheckYourAnswerPage.clickChangeOtherIndividualTaxAdvisers
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.scottish_limited_partnership.PartnersAndOtherTaxAdvisersCheckYourAnswerPage.clickChangeOtherRelevantTaxAdvisers
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.scottish_limited_partnership.CheckThisListOfPartnersPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.scottish_limited_partnership.PartnersAndOtherTaxAdvisersCheckYourAnswerPage

object PartnersTaxAdvisorInformationFlow:

  private val allPartnersNames = List(
    "Steve",
    "Beverly"
  )

  private val allPartnersSurnames = List(
    "Austin",
    "Hills"
  )
  private val otherRelevantTaxAdvisorsNames = List(
    "Bruce Wayne",
    "Clark Kent",
    "Diana Prince"
  )

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertPartnersAndAdvisorsStatus("Incomplete")
    TaskListPage.clickPartnersAndAdvisorsStatusLink()

  def selectListOfPartnersCorrect(): Unit =
    CheckThisListOfPartnersPage.assertPageIsDisplayed()
    CheckThisListOfPartnersPage.selectYes()
    CheckThisListOfPartnersPage.clickContinue()

  def selectNoOtherRelevantTaxAdvisers(): Unit =
    HasOtherRelevantTaxAdvisersPage.assertPageIsDisplayed()
    HasOtherRelevantTaxAdvisersPage.selectNo()
    HasOtherRelevantTaxAdvisersPage.clickContinue()

  def selectYesOtherRelevantTaxAdvisers(): Unit =
    HasOtherRelevantTaxAdvisersPage.assertPageIsDisplayed()
    HasOtherRelevantTaxAdvisersPage.selectYes()
    HasOtherRelevantTaxAdvisersPage.clickContinue()

  def checkYourAnswersPartnersAndOtherTaxAdvisers(): Unit =
    PartnersAndOtherTaxAdvisersCheckYourAnswerPage.assertPageIsDisplayed()
    PartnersAndOtherTaxAdvisersCheckYourAnswerPage.clickContinue()

  def enterFullNameOfPerson(name: String): Unit =
    OtherRelevantIndividualPage.assertPageIsDisplayed()
    OtherRelevantIndividualPage.enterOtherRelevantIndividualName(name)
    OtherRelevantIndividualPage.clickContinue()

  // Capture first N partner names from the page
  def captureFirstNPartnerNamesAndSelectCorrect(n: Int): List[String] =
    CheckThisListOfPartnersPage.assertPageIsDisplayed()
    val partnerNames = CheckThisListOfPartnersPage.getFirstNPartnerNames(n)
    println(s"First $n director names captured: ${partnerNames.mkString(", ")}")
    CheckThisListOfPartnersPage.selectYes()
    CheckThisListOfPartnersPage.clickContinue()
    partnerNames

  def checkYourAnswersWithoutRelevantAdviserNames(
    companiesHouseListOfPartnersCorrect: String,
    otherRelevantTaxAdvisers: String
  ): Unit = {
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Companies House list of partners correct", companiesHouseListOfPartnersCorrect)
    CheckYourAnswersPage.assertSummaryRow("Other relevant tax advisers", otherRelevantTaxAdvisers)
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertPartnerTaxAdvisorInformationStatus("Completed")
    TaskListPage.assertAskPartnersAndTaxAdvisorsToSignInStatus("Incomplete")
    TaskListPage.assertCheckProvidedDetailsStatus("Cannot start yet")
  }

  object partnersAndOtherTaxAdvisers:

    def runFlow(): Unit =
      startJourney()
      selectListOfPartnersCorrect()
      selectNoOtherRelevantTaxAdvisers()
      checkYourAnswersPartnersAndOtherTaxAdvisers()

  object otherTaxAdvisers:

    def runFlowWith1OtherRelevantTaxAdvisers(): Unit =
      startJourney()
      selectListOfPartnersCorrect()
      selectYesOtherRelevantTaxAdvisers()
      enterFullNameOfPerson("Bruce Wayne")
      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.selectNo()
      HasOtherRelevantTaxAdvisersPage.clickContinue()
      checkYourAnswersPartnersAndOtherTaxAdvisers()

  object anyMoreTaxAdvisers:

    def runFlowWithAnyMoreRelevantTaxAdvisers(): Unit =
      startJourney()
      selectListOfPartnersCorrect()
      selectYesOtherRelevantTaxAdvisers()
      enterFullNameOfPerson("Bruce Wayne")
      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.selectYes()
      HasOtherRelevantTaxAdvisersPage.clickContinue()
      enterFullNameOfPerson("Dick Grayson")
      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.selectNo()
      HasOtherRelevantTaxAdvisersPage.clickContinue()
      checkYourAnswersPartnersAndOtherTaxAdvisers()

  object changeNoOtherRelevantTaxAdvisers:

    def runFlowWithChangeNoOtherRelevantTaxAdvisers(): Unit =
      startJourney()
      selectListOfPartnersCorrect()
      selectYesOtherRelevantTaxAdvisers()
      enterFullNameOfPerson("Bruce Wayne")
      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.selectNo()
      HasOtherRelevantTaxAdvisersPage.clickContinue()
      checkYourAnswersPartnersAndOtherTaxAdvisers()
      TaskListPage.clickPartnersAndAdvisorsStatusLink()
      clickChangeOtherRelevantTaxAdvisers()
      selectNoOtherRelevantTaxAdvisers()
      checkYourAnswersPartnersAndOtherTaxAdvisers()

  object removeOtherRelevantTaxAdvisers:

    def runFlowWithRemoveOtherRelevantTaxAdvisers(): Unit =
      startJourney()
      selectListOfPartnersCorrect()
      selectYesOtherRelevantTaxAdvisers()
      enterFullNameOfPerson("Bruce Wayne")
      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.selectNo()
      HasOtherRelevantTaxAdvisersPage.clickContinue()
      checkYourAnswersPartnersAndOtherTaxAdvisers()
      TaskListPage.clickPartnersAndAdvisorsStatusLink()
      clickChangeOtherIndividualTaxAdvisers()
      // Remove the existing other individual tax adviser
      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.removePartner("Bruce Wayne")
      CheckYourAnswersOtherIndividualsPage.selectRemoveYes()
      HasOtherRelevantTaxAdvisersPage.clickContinue()
      selectNoOtherRelevantTaxAdvisers()
      checkYourAnswersPartnersAndOtherTaxAdvisers()

  object multiplePartners: // flow where there are two Partners and no other relevant tax advisers - captures the names of the partners to be used in later flows

    def runFlow(): List[String] =
      startJourney()
      val partnersNames = captureFirstNPartnerNamesAndSelectCorrect(2)
      selectNoOtherRelevantTaxAdvisers()
      checkYourAnswersWithoutRelevantAdviserNames(
        "Yes",
        "No"
      )
      partnersNames
