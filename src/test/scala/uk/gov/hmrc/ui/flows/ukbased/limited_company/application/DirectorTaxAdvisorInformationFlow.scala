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

package uk.gov.hmrc.ui.flows.ukbased.limited_company.application

import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.*
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.CheckThisListOfDirectorsPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.TellUsAboutTheDirectorPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.YouHaveAddedDirectorCheckYourAnswerPage

object DirectorTaxAdvisorInformationFlow:

  private val allDirectorNames = List(
    "Steve",
    "Beverly",
    "Pauline",
    "Steve",
    "Sandra"
  )

  private val allDirectorSurnames = List(
    "Austin",
    "Hills",
    "Austin",
    "Palmer",
    "Hills"
  )
  private val otherRelevantTaxAdvisorsNames = List(
    "Bruce Wayne",
    "Clark Kent",
    "Diana Prince"
  )

  object FiveOrLessDirectors:

    def runFlow(): String = // flow where there are 5 or less directors and all are known to tax authority
      val names = otherRelevantTaxAdvisorsNames.take(2)
      startJourney()
      val directorName = captureDirectorNameAndSelectCorrect()
      selectAnyOtherRelevantTaxAdvisers()
      enterOtherRelevantTaxAdvisors(names)
      checkYourAnswersOtherRelevantIndividuals(names)
      checkYourAnswersWithoutDirectorsNames(
        "Yes",
        "Yes",
        names
      )
      directorName

  object SixOrMoreDirectors: // 6 or more directors and some other tax advisers

    def runFlow(): Unit =
      val names = allDirectorNames.take(5)
      val surnames = allDirectorSurnames.take(5)
      val taxAdviserNames = otherRelevantTaxAdvisorsNames.take(2)
      startJourney()
      enterNumberOfDirectors("5")
      enterFullNameOfDirectors(names, surnames)
      checkYourAnswersKeyIndividuals(names, surnames)
      selectAnyOtherRelevantTaxAdvisers()
      enterOtherRelevantTaxAdvisors(taxAdviserNames)
      checkYourAnswersOtherRelevantIndividuals(taxAdviserNames)
      checkYourAnswersWithRelevantTaxAdvisers(
        "5",
        names,
        surnames,
        "Yes",
        taxAdviserNames
      )

  object multipleDirectors: // flow where there are two Directors

    def runFlow(): List[String] =
      startJourney()
      val directorNames = captureFirstNDirectorNamesAndSelectCorrect(2)
      selectNoOtherRelevantTaxAdvisers()
      checkYourAnswersWithoutRelevantAdviserNames(
        "Yes",
        "No"
      )
      directorNames

  object RunToCheckYourAnswersToChangeDirectors: // flow to test to change directors from check your answers page

    def runFlow(): Unit =
      val names = allDirectorNames.take(5)
      val surnames = allDirectorSurnames.take(5)
      startJourney()
      enterNumberOfDirectors("5")
      enterFullNameOfDirectors(names, surnames)

  object RunToCheckYourAnswersOtherIndividuals: // for editing other relevant individuals

    def runFlow(): Unit =
      val names = allDirectorNames.take(5)
      val surnames = allDirectorSurnames.take(5)
      val taxAdviserNames = otherRelevantTaxAdvisorsNames.take(2)
      startJourney()
      enterNumberOfDirectors("5")
      enterFullNameOfDirectors(names, surnames)
      checkYourAnswersKeyIndividuals(names, surnames)
      selectAnyOtherRelevantTaxAdvisers()
      enterOtherRelevantTaxAdvisors(taxAdviserNames)

  object RunToFinalCheckYourAnswers: // flow to get to the final check your answers page for testing the change scenarios

    def runFlow(): Unit =
      val names = allDirectorNames.take(5)
      val surnames = allDirectorSurnames.take(5)
      val taxAdviserNames = otherRelevantTaxAdvisorsNames.take(2)
      startJourney()
      enterNumberOfDirectors("5")
      enterFullNameOfDirectors(names, surnames)
      checkYourAnswersKeyIndividuals(names, surnames)
      selectAnyOtherRelevantTaxAdvisers()
      enterOtherRelevantTaxAdvisors(taxAdviserNames)
      checkYourAnswersOtherRelevantIndividuals(taxAdviserNames)

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertDirectorTaxAdvisorInformationStatus("Incomplete")
    TaskListPage.clickOnDirectorTaxAdvisorInformationLink()

  def selectListOfDirectorsCorrect(): Unit =
    CheckThisListOfDirectorsPage.assertPageIsDisplayed()
    CheckThisListOfDirectorsPage.selectYes()
    CheckThisListOfDirectorsPage.clickContinue()

  // Capture director name from the page and return it for use in stubs
  def captureDirectorNameAndSelectCorrect(): String =
    CheckThisListOfDirectorsPage.assertPageIsDisplayed()
    val directorName = CheckThisListOfDirectorsPage.getFirstDirectorName()
    CheckThisListOfDirectorsPage.selectYes()
    CheckThisListOfDirectorsPage.clickContinue()
    directorName

  // Capture first N director names from the page
  def captureFirstNDirectorNamesAndSelectCorrect(n: Int): List[String] =
    CheckThisListOfDirectorsPage.assertPageIsDisplayed()
    val directorNames = CheckThisListOfDirectorsPage.getFirstNDirectorNames(n)
    println(s"First $n director names captured: ${directorNames.mkString(", ")}")
    CheckThisListOfDirectorsPage.selectYes()
    CheckThisListOfDirectorsPage.clickContinue()
    directorNames

  def enterNumberOfDirectors(n: String): Unit =
    CheckThisListOfDirectorsPage.assertPageIsDisplayed()
    CheckThisListOfDirectorsPage.enterExactNumberOfDirectors(n)
    CheckThisListOfDirectorsPage.clickContinue()

  def enterFullNameOfDirectors(
    names: List[String],
    surnames: List[String]
  ): Unit =
    enterFirstDirectorName(names.head, surnames.head)
    names.tail.zip(surnames.tail).foreach(enterAdditionalDirectorNameAndSurname)

  def enterFirstDirectorName(
    name: String,
    surname: String
  ): Unit =
    TellUsAboutTheDirectorPage.assertPageIsDisplayed()
    TellUsAboutTheDirectorPage.enterDirectorFirstName(name)
    TellUsAboutTheDirectorPage.enterDirectorLastName(surname)
    TellUsAboutTheDirectorPage.clickContinue()

  def enterAdditionalDirectorNameAndSurname(
    name: String,
    surname: String
  ): Unit =
    YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
    YouHaveAddedDirectorCheckYourAnswerPage.clickContinue()
    TellUsAboutTheDirectorPage.assertPageIsDisplayed()
    TellUsAboutTheDirectorPage.enterDirectorFirstName(name)
    TellUsAboutTheDirectorPage.enterDirectorLastName(surname)
    TellUsAboutTheDirectorPage.clickContinue()

  def checkYourAnswersKeyIndividuals(
    names: List[String],
    surnames: List[String]
  ): Unit =
    YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
    names.zip(surnames).zipWithIndex.foreach { case ((name, surname), idx) => YouHaveAddedDirectorCheckYourAnswerPage.assertNameAt(idx, s"$name $surname") }
    YouHaveAddedDirectorCheckYourAnswerPage.clickContinue()

  def selectNoOtherRelevantTaxAdvisers(): Unit =
    HasOtherRelevantTaxAdvisersPage.assertPageIsDisplayed()
    HasOtherRelevantTaxAdvisersPage.selectNo()
    HasOtherRelevantTaxAdvisersPage.clickContinue()

  def selectAnyOtherRelevantTaxAdvisers(): Unit =
    HasOtherRelevantTaxAdvisersPage.assertPageIsDisplayed()
    HasOtherRelevantTaxAdvisersPage.selectYes()
    HasOtherRelevantTaxAdvisersPage.clickContinue()

  def enterOtherRelevantTaxAdvisors(names: List[String]): Unit =
    enterFullNameOfPerson(names.head)
    names.tail.foreach(enterAdditionalIndividualName)

  def enterFullNameOfPerson(name: String): Unit =
    OtherRelevantIndividualPage.assertPageIsDisplayed()
    OtherRelevantIndividualPage.enterOtherRelevantIndividualName(name)
    OtherRelevantIndividualPage.clickContinue()

  def enterAdditionalIndividualName(name: String): Unit =
    CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
    CheckYourAnswersOtherIndividualsPage.selectYes()
    CheckYourAnswersOtherIndividualsPage.clickContinue()
    OtherRelevantIndividualPage.assertPageIsDisplayed()
    OtherRelevantIndividualPage.enterOtherRelevantIndividualName(name)
    OtherRelevantIndividualPage.clickContinue()

  def checkYourAnswersOtherRelevantIndividuals(expectedNames: List[String]): Unit =
    CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
    expectedNames.zipWithIndex.foreach { case (name, idx) => CheckYourAnswersOtherIndividualsPage.assertNameAt(idx, name) }
    CheckYourAnswersOtherIndividualsPage.selectNo()
    CheckYourAnswersOtherIndividualsPage.clickContinue()

  def checkYourAnswersWithoutDirectorsNames(
    companiesHouseListOfDirectorsCorrect: String,
    otherRelevantTaxAdvisers: String,
    names: List[String]
  ): Unit = {
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Companies House list of directors correct", companiesHouseListOfDirectorsCorrect)
    CheckYourAnswersPage.assertSummaryRow("Other relevant tax advisers", otherRelevantTaxAdvisers)
    CheckYourAnswersPage.assertSummaryRow("Other relevant tax adviser names", names.mkString("\n"))
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertDirectorTaxAdvisorInformationStatus("Completed")
    TaskListPage.assertAskPartnersAndAdvisorsToSignInStatus("Incomplete")
    TaskListPage.assertCheckProvidedDetailsStatus("Cannot start yet")
  }

  def checkYourAnswersWithoutRelevantAdviserNames(
    companiesHouseListOfDirectorsCorrect: String,
    otherRelevantTaxAdvisers: String
  ): Unit = {
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Companies House list of directors correct", companiesHouseListOfDirectorsCorrect)
    CheckYourAnswersPage.assertSummaryRow("Other relevant tax advisers", otherRelevantTaxAdvisers)
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertDirectorTaxAdvisorInformationStatus("Completed")
    TaskListPage.assertAskDirectorsAndTaxAdvisorsToSignInStatus("Incomplete")
    TaskListPage.assertCheckProvidedDetailsStatus("Cannot start yet")
  }

  def checkYourAnswersWithRelevantTaxAdvisers(
    directorNum: String,
    names: List[String],
    surnames: List[String],
    otherRelevantTaxAdviser: String,
    otherRelevantTaxAdviserNames: List[String]
  ): Unit =
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Number of directors", directorNum)
    val fullNames = names.zip(surnames).map { case (name, surname) => s"$name $surname" }
    CheckYourAnswersPage.assertSummaryRow("Director names", fullNames.mkString("\n"))
    CheckYourAnswersPage.assertSummaryRow("Other relevant tax advisers", otherRelevantTaxAdviser)
    CheckYourAnswersPage.assertSummaryRow("Other relevant tax adviser names", otherRelevantTaxAdviserNames.mkString("\n"))
    CheckYourAnswersPage.clickContinue()
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertDirectorTaxAdvisorInformationStatus("Completed")
    TaskListPage.assertAskDirectorsAndTaxAdvisorsToSignInStatus("Incomplete")
    TaskListPage.assertCheckProvidedDetailsStatus("Cannot start yet")
