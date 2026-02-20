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

package uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.PartnerTaxAdvisorInformationFlow.NumberOfPartners.FiveOrLess
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.PartnerTaxAdvisorInformationFlow.NumberOfPartners.SixOrMore
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.CheckYourAnswersKeyIndividualsPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.HowManyPartnersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.PartnerFullNamePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.UnofficialPartnersPage

object PartnerTaxAdvisorInformationFlow:

  private val allNames = List(
    "Bobby Boucher",
    "Sonny Koufax",
    "Jack Burton",
    "Steve Rogers",
    "Tony Stark",
    "Natasha Romanov",
    "Carol Danvers"
  )

  enum NumberOfPartners:

    case FiveOrLess, SixOrMore
    def selectOnPage(n: String): Unit =
      this match
        case FiveOrLess =>
          HowManyPartnersPage.selectFiveOrLess()
          HowManyPartnersPage.enterExactNumber(n)
        case SixOrMore =>
          HowManyPartnersPage.selectSixOrMore()
          HowManyPartnersPage.enterNumResponsible(n)

  object FiveOrLessPartners:

    def runFlow(): Unit =
      val names = allNames.take(3)
      startJourney()
      enterNumberOfPartners("3", FiveOrLess)
      enterPartners(names)
      checkYourAnswers(names)
      confirmEntries()

  object SixOrMorePartners:

    def runFlow(): Unit =
      val names = allNames
      startJourney()
      enterNumberOfPartners("7", SixOrMore)
      enterPartners(names)
      checkYourAnswers(names)
      confirmEntries()

  object SixOrMorePartnersAlt: // alternate flow where there are 6 or more partners but less than 6 with tax authority

    def runFlow(): Unit =
      val names = allNames.take(5)
      startJourney()
      enterNumberOfPartners("3", SixOrMore)
      enterPartners(names)
      checkYourAnswers(names)
      confirmEntries()

  object runToCheckYourAnswers:

    def runFlow(): Unit =
      val names = allNames.take(5)
      startJourney()
      enterNumberOfPartners("3", SixOrMore)
      enterPartners(names)
      checkYourAnswers(names)

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertPartnerTaxAdvisorInformationStatus("Incomplete")
    TaskListPage.clickOnPartnerTaxAdvisorInformationLink()

  def enterNumberOfPartners(
    n: String,
    totalNum: NumberOfPartners
  ): Unit =
    HowManyPartnersPage.assertPageIsDisplayed()
    totalNum.selectOnPage(n)
    HowManyPartnersPage.clickContinue()

  def enterFirstPartnerName(name: String): Unit =
    PartnerFullNamePage.assertPageIsDisplayed()
    PartnerFullNamePage.enterPartnerFullName(name)
    PartnerFullNamePage.clickContinue()

  def enterAdditionalPartnerName(name: String): Unit =
    CheckYourAnswersKeyIndividualsPage.assertPageIsDisplayed()
    CheckYourAnswersKeyIndividualsPage.clickContinue()
    PartnerFullNamePage.assertPageIsDisplayed()
    PartnerFullNamePage.enterPartnerFullName(name)
    PartnerFullNamePage.clickContinue()

  def enterPartners(names: List[String]): Unit =
    enterFirstPartnerName(names.head)
    names.tail.foreach(enterAdditionalPartnerName)

  def checkYourAnswers(expectedNames: List[String]): Unit =
    CheckYourAnswersKeyIndividualsPage.assertPageIsDisplayed()
    expectedNames.zipWithIndex.foreach { case (name, idx) => CheckYourAnswersKeyIndividualsPage.assertNameAt(idx, name) }

  def confirmEntries(): Unit =
    CheckYourAnswersKeyIndividualsPage.clickContinue()
    UnofficialPartnersPage.assertPageIsDisplayed()
    UnofficialPartnersPage.selectNo()
    UnofficialPartnersPage.clickContinue()
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertPartnerTaxAdvisorInformationStatus("Completed")
