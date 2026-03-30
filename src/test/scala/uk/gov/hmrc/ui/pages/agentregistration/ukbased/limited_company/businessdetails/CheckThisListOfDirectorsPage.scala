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

package uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object CheckThisListOfDirectorsPage
extends BasePage:

  override val path: String = "/agent-registration/apply/list-details/companies-house-officers"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val yesRadio = By.id("isCompaniesHouseOfficersListCorrect")
  private val noRadio = By.id("isCompaniesHouseOfficersListCorrect-2")

  def selectYes(): Unit = click(yesRadio)
  def selectNo(): Unit = click(noRadio)

  private val exactNumberField = By.id("numberOfOfficersResponsibleForTaxMatters")

  def enterExactNumberOfDirectors(n: String): Unit = sendKeys(exactNumberField, n)

  // Capture director names from the list displayed on the page
  def getDirectorNames(): List[String] =
    val directorNameLocators = By.cssSelector("#main-content > div > div > ul > li")
    findElementsBy(directorNameLocators).map(_.getText.trim).toList

  // Get the first director name (most common use case)
  def getFirstDirectorName(): String =
    val names = getDirectorNames()
    if names.nonEmpty then names.head
    else throw new NoSuchElementException("No director names found on the page")

  // Get the first N director names
  def getFirstNDirectorNames(n: Int): List[String] =
    val names = getDirectorNames()
    if n > names.length then throw new IndexOutOfBoundsException(s"Requested $n directors but only ${names.length} available")
    names.take(n)
