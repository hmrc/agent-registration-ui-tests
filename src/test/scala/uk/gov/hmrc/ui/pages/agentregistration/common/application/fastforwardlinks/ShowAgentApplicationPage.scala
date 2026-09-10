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

package uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import uk.gov.hmrc.ui.pages.EntryPage
import uk.gov.hmrc.ui.pages.PageObject.click
import uk.gov.hmrc.ui.pages.PageObject.findElementsBy
import uk.gov.hmrc.ui.pages.PageObject.getCurrentUrl
import uk.gov.hmrc.ui.pages.PageObject.getText
import uk.gov.hmrc.ui.utils.RichMatchers.*
import uk.gov.hmrc.ui.pages.PageObject.get

object ShowAgentApplicationPage
extends EntryPage:

  override val path: String = "/agent-registration/test-only/agent-application-details/"
  override val baseUrl: String = FastForwardLinksPage.baseUrl

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  private val goToTaskListLink = By.linkText("Task list page")
  private val applicantLogInLink = By.linkText("Log in as applicant")
  private val individualLogInLink = By.linkText("Log in as individual")
  private val gotToExternalStubLink = By.id("goto-agents-external-stubs")
  private val runRiskingLink = By.linkText("Run risking")
  private val runResultsFileProcessingLink = By.linkText("Run results file processing")
  private val stateValue = By.xpath("//dt[normalize-space()='State']/following-sibling::dd[1]")
  private val chooseEntityFailuresLink = By.linkText("Choose entity failures")
  private val chooseIndividualFailuresLink = By.linkText("Choose individual failures")
  private val approveApplicantLink = By.xpath(
    "//div[@id='entity-minerva-simulator']//a[contains(@href, '/quick/approved') and normalize-space()='approve']"
  )
  private val chooseIndividualFailuresLinksByIndividual = By.xpath(
    "//div[starts-with(@id, 'individual-')]//a[normalize-space()='Choose individual failures']"
  )
  private val individualCards = By.xpath(
    "//div[starts-with(@id, 'individual-') and contains(@class, 'test-only-card') and not(contains(@id, '-minerva-simulator'))]"
  )
  private val approveIndividualLinksByIndividual = By.xpath(
    "//div[starts-with(@id, 'individual-')]//a[contains(@href, '/quick/approved') and normalize-space()='approve']"
  )
  private val internalUserIdValue = By.xpath("//li[contains(normalize-space(), 'Internal user id:')]/code")
  private val provideDetailsLink = By.cssSelector("a[href*='/agent-registration/provide-details/start/']")

  def clickGoToTaskListLink(): Unit =
    assertPageIsDisplayed()
    click(goToTaskListLink)

  def clickLogInAsApplicantLink(): Unit =
    assertPageIsDisplayed()
    click(applicantLogInLink)
    assertPageIsDisplayed()

  def clickLoginAsIndividualLink(): Unit =
    assertPageIsDisplayed()
    click(individualLogInLink)
    assertPageIsDisplayed()

  def clickGoToExternalStubLink(): Unit = click(gotToExternalStubLink)
  def getInternalUserDetails: (String, String) =
    val rawValue = getText(internalUserIdValue).trim
    val parts = rawValue.split("@")
    require(parts.length == 2, s"Unexpected format for Internal user id: $rawValue")
    val username = parts(0)
    val planetId = parts(1)
    (username, planetId)
  def openForApplicationReference(applicationReference: String): Unit = get(s"$url$applicationReference")
  def clickRunRiskingLink(): Unit =
    assertPageIsDisplayed()
    click(runRiskingLink)
    assertPageIsDisplayed()

  def clickRunResultsFileProcessingLink(): Unit =
    assertPageIsDisplayed()
    click(runResultsFileProcessingLink)
    assertPageIsDisplayed()
  def getApplicationStateText: String = getText(stateValue).replaceAll("\\s+", " ").trim
  def clickChooseEntityFailuresLink(): Unit =
    assertPageIsDisplayed()
    click(chooseEntityFailuresLink)

  def clickChooseIndividualFailuresLink(): Unit =
    assertPageIsDisplayed()
    click(chooseIndividualFailuresLink)

  def clickApproveApplicantLink(): Unit =
    assertPageIsDisplayed()
    click(approveApplicantLink)
    assertPageIsDisplayed()
  def hasChooseIndividualFailuresLinksByIndividual: Boolean = findElementsBy(chooseIndividualFailuresLinksByIndividual).nonEmpty
  def numberOfChooseIndividualFailuresLinksByIndividual: Int = findElementsBy(chooseIndividualFailuresLinksByIndividual).size
  def hasApproveIndividualLinksByIndividual: Boolean = findElementsBy(approveIndividualLinksByIndividual).nonEmpty
  def numberOfApproveIndividualLinksByIndividual: Int = findElementsBy(approveIndividualLinksByIndividual).size
  def clickFirstRemainingChooseIndividualFailuresLink(): Unit = eventually {
    findElementsBy(chooseIndividualFailuresLinksByIndividual)
      .headOption
      .getOrElse(
        fail("No remaining 'Choose individual failures' links were found")
      )
      .click()
  }

  def clickFirstRemainingApproveIndividualLink(): Unit = eventually {
    findElementsBy(approveIndividualLinksByIndividual)
      .headOption
      .getOrElse(
        fail("No remaining 'approve' links were found")
      )
      .click()
  }

  private def normaliseWhitespace(value: String): String = value.replaceAll("\\s+", " ").trim

  private def individualNameFromCard(card: WebElement): String = normaliseWhitespace(
    card.findElement(By.xpath(".//dt[normalize-space()='Name']/following-sibling::dd[1]")).getText
  )

  private def individualCardForName(individualName: String): WebElement =
    val matchingCards = findElementsBy(individualCards).filter { card =>
      individualNameFromCard(card) == individualName
    }

    matchingCards match
      case Seq(card) => card
      case Seq() =>
        val availableNames = findElementsBy(individualCards).map(individualNameFromCard).mkString(", ")
        fail(
          s"No individual card found for name '$individualName'. Available names: [$availableNames]"
        )
      case _ => fail(s"Multiple individual cards found for name '$individualName'")

  def clickChooseIndividualFailuresLinkForIndividualName(individualName: String): Unit = eventually {
    val card = individualCardForName(individualName)
    card.findElement(By.linkText("Choose individual failures")).click()
  }

  def clickApproveIndividualLinkForIndividualName(individualName: String): Unit = eventually {
    val card = individualCardForName(individualName)
    card.findElement(By.xpath(".//a[contains(@href, '/quick/approved') and normalize-space()='approve']")).click()
  }

  def clickLoginAsIndividualLinkForIndividualName(individualName: String): Unit = eventually {
    val card = individualCardForName(individualName)
    card.findElement(By.linkText("Log in as individual")).click()
  }

  def clickChooseIndividualFailuresLinkForIndividual(index0: Int): Unit =
    require(index0 >= 0, s"index0 must be >= 0, but was $index0")

    eventually {
      findElementsBy(chooseIndividualFailuresLinksByIndividual)
        .lift(index0)
        .getOrElse(
          fail(
            s"No 'Choose individual failures' link found for individual index $index0. Available links: ${findElementsBy(chooseIndividualFailuresLinksByIndividual).size}"
          )
        )
        .click()
    }
  def clickProvideDetailsLink(): Unit =
    assertPageIsDisplayed()
    click(provideDetailsLink)
