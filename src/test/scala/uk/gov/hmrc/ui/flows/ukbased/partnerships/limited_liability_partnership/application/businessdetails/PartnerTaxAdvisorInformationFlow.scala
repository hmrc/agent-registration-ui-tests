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
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.{CheckYourAnswersKeyIndividualsPage, CheckYourAnswersOtherIndividualsPage, CheckYourAnswersPage, HowManyPartnersPage, OtherRelevantIndividualPage, PartnerFullNamePage, HasOtherRelevantTaxAdvisersPage}

object PartnerTaxAdvisorInformationFlow:

  private val allPartnerNames = List(
    "Bobby Boucher",
    "Sonny Koufax",
    "Jack Burton",
    "Steve Rogers",
    "Tony Stark",
    "Natasha Romanov",
    "Carol Danvers"
  )

  private val allUnofficialPartnerNames = List(
    "Bruce Wayne",
    "Clark Kent",
    "Diana Prince"
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

    def runFlow(): Unit = // flow where there are 5 or less partners and all are known to tax authority
      val names = allPartnerNames.take(3)
      startJourney()
      enterNumberOfPartners("3", FiveOrLess)
      enterPartners(names)
      checkYourAnswersKeyIndividuals(names)
      noUnofficialPartners()
      checkYourAnswersNoUnofficial("3", names, "No")

  object SixOrMorePartners: // flow where there are 6 or more partners and all are known to tax authority

    def runFlow(): Unit =
      val names = allPartnerNames
      startJourney()
      enterNumberOfPartners("7", SixOrMore)
      enterPartners(names)
      checkYourAnswersKeyIndividuals(names)
      noUnofficialPartners()
      checkYourAnswersNoUnofficial("7", names, "No")

  object SixOrMorePartnersAlt: // alternate flow where there are 6 or more partners but less than 6 with tax authority

    def runFlow(): Unit =
      val names = allPartnerNames.take(5)
      startJourney()
      enterNumberOfPartners("3", SixOrMore)
      enterPartners(names)
      checkYourAnswersKeyIndividuals(names)
      noUnofficialPartners()
      checkYourAnswersNoUnofficial("5", names, "No")

  object WithUnofficialPartners: // flow where there are partners and unofficial partners

    def runFlow(): Unit =
      val names = allPartnerNames.take(3)
      val uNames = allUnofficialPartnerNames.take(3)
      startJourney()
      enterNumberOfPartners("3", FiveOrLess)
      enterPartners(names)
      checkYourAnswersKeyIndividuals(names)
      addUnofficialPartners(uNames)
      checkYourAnswersOtherIndividuals(uNames)
      checkYourAnswersWithUnofficial("3", names, "Yes", uNames)
      completeJourney()

  object runToCheckYourAnswersOfficialPartners: // flow to get to the official partners check your answers page for testing the change scenarios

    def runFlow(): Unit =
      val names = allPartnerNames.take(5)
      startJourney()
      enterNumberOfPartners("3", SixOrMore)
      enterPartners(names)

  object runToCheckYourAnswersUnofficialPartners:// flow to get to the unofficial partners check your answers page for testing the change scenarios

    def runFlow(): Unit =
      val names = allPartnerNames.take(3)
      val uNames = allUnofficialPartnerNames.take(3)
      startJourney()
      enterNumberOfPartners("3", FiveOrLess)
      enterPartners(names)
      checkYourAnswersKeyIndividuals(names)
      addUnofficialPartners(uNames)

  object runToCheckYourAnswers: // flow to get to the final check your answers page for testing the change scenarios

    def runFlow(): Unit =
      val names = allPartnerNames.take(3)
      val uNames = allUnofficialPartnerNames.take(3)
      startJourney()
      enterNumberOfPartners("3", FiveOrLess)
      enterPartners(names)
      checkYourAnswersKeyIndividuals(names)
      addUnofficialPartners(uNames)
      checkYourAnswersOtherIndividuals(uNames)

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

  def enterPartners(names: List[String]): Unit =
    enterFirstPartnerName(names.head)
    names.tail.foreach(enterAdditionalPartnerName)

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

  def noUnofficialPartners(): Unit =
    HasOtherRelevantTaxAdvisersPage.assertPageIsDisplayed()
    HasOtherRelevantTaxAdvisersPage.selectNo()
    HasOtherRelevantTaxAdvisersPage.clickContinue()

  def addUnofficialPartners(uNames: List[String]): Unit =
    HasOtherRelevantTaxAdvisersPage.assertPageIsDisplayed()
    HasOtherRelevantTaxAdvisersPage.selectYes()
    HasOtherRelevantTaxAdvisersPage.clickContinue()
    OtherRelevantIndividualPage.assertPageIsDisplayed()
    enterUnofficialPartners(uNames)

  def enterUnofficialPartners(names: List[String]): Unit =
    enterFirstUnofficialPartnerName(names.head)
    names.tail.foreach(enterAdditionalUnofficialPartnerName)

  def enterFirstUnofficialPartnerName(name: String): Unit =
    OtherRelevantIndividualPage.assertPageIsDisplayed()
    OtherRelevantIndividualPage.enterPartnerFullName(name)
    OtherRelevantIndividualPage.clickContinue()

  def enterAdditionalUnofficialPartnerName(name: String): Unit =
    CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
    CheckYourAnswersOtherIndividualsPage.selectYes()
    CheckYourAnswersKeyIndividualsPage.clickContinue()
    OtherRelevantIndividualPage.assertPageIsDisplayed()
    OtherRelevantIndividualPage.enterPartnerFullName(name)
    OtherRelevantIndividualPage.clickContinue()

  def checkYourAnswersKeyIndividuals(expectedNames: List[String]): Unit =
    CheckYourAnswersKeyIndividualsPage.assertPageIsDisplayed()
    expectedNames.zipWithIndex.foreach { case (name, idx) => CheckYourAnswersKeyIndividualsPage.assertNameAt(idx, name) }
    CheckYourAnswersKeyIndividualsPage.clickContinue()

  def checkYourAnswersOtherIndividuals(expectedNames: List[String]): Unit =
    CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
    expectedNames.zipWithIndex.foreach { case (name, idx) => CheckYourAnswersOtherIndividualsPage.assertNameAt(idx, name) }
    CheckYourAnswersOtherIndividualsPage.selectNo()
    CheckYourAnswersOtherIndividualsPage.clickContinue()

  def checkYourAnswersWithUnofficial(partNum: String, names: List[String], unofficialPart: String, uNames: List[String]): Unit =
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Number of partners", partNum)
    CheckYourAnswersPage.assertSummaryRow("Partner names", names.mkString("\n"))
    CheckYourAnswersPage.assertSummaryRow("Other relevant tax advisers", unofficialPart)
    CheckYourAnswersPage.assertSummaryRow("Other relevant tax adviser names", uNames.mkString("\n"))

  def checkYourAnswersNoUnofficial(partNum: String, names: List[String], unofficialPart: String): Unit =
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Number of partners", partNum)
    CheckYourAnswersPage.assertSummaryRow("Partner names", names.mkString("\n"))
    CheckYourAnswersPage.assertSummaryRow("Other relevant tax advisers", unofficialPart)
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertPartnerTaxAdvisorInformationStatus("Completed")

  def completeJourney(): Unit =
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertPartnerTaxAdvisorInformationStatus("Completed")