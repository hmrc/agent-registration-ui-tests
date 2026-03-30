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

package uk.gov.hmrc.ui.pages.agentregistration.common.application

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object TaskListPage
extends BasePage:

  override val path: String = "/agent-registration/apply/task-list"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  // Status assertions
  def assertContactDetailsStatus(expectedStatus: String): Unit = getText(contactDetailsStatus) shouldBe expectedStatus
  def assertBusinessDetailsStatus(expectedStatus: String): Unit = getText(businessDetailsStatus) shouldBe expectedStatus
  def assertAgentServicesAccountDetailsStatus(expectedStatus: String): Unit = getText(agentServicesAccountDetailsStatus) shouldBe expectedStatus
  def assertAmlsDetailsStatus(expectedStatus: String): Unit = getText(amlsDetailsStatus) shouldBe expectedStatus
  def assertHmrcStandardsForAgentsStatus(expectedStatus: String): Unit = getText(hmrcStandardsForAgentsStatus) shouldBe expectedStatus
  def assertProveYourIdentityStatus(expectedStatus: String): Unit = getText(proveYourIdentityStatus) shouldBe expectedStatus
  def assertPartnersAndAdvisorsStatus(expectedStatus: String): Unit = getText(partnersAndAdvisorsStatus) shouldBe expectedStatus
  def assertAskPartnersAndAdvisorsToSignInStatus(expectedStatus: String): Unit = getText(askPartnersAndAdvisorsToSignInStatus) shouldBe expectedStatus
  def assertAskBusinessOwnerToSignInStatus(expectedStatus: String): Unit = getText(askBusinessOwnerToSignInStatus) shouldBe expectedStatus
  def assertCheckProvidedDetailsStatus(expectedStatus: String): Unit = getText(checkProvidedDetailsStatus) shouldBe expectedStatus
  def assertDeclarationStatus(expectedStatus: String): Unit = getText(declarationStatus) shouldBe expectedStatus
  def assertPartnerTaxAdvisorInformationStatus(expectedStatus: String): Unit = getText(partnerTaxAdvisorInformationStatus)
  def assertAskDirectorsAndTaxAdvisorsToSignInStatus(expectedStatus: String): Unit = getText(askDirectorsAndTaxAdvisorsToSignInStatus) shouldBe expectedStatus
  def assertDirectorTaxAdvisorInformationStatus(expectedStatus: String): Unit = getText(directorTaxAdvisorInformationStatus) shouldBe expectedStatus
  // Click actions
  def clickOnApplicantContactDetailsLink(): Unit = click(contactDetailsLink)
  def clickOnAgentServicesAccountDetailsLink(): Unit = click(agentServicesAccountDetailsLink)
  def clickOnAmlsDetailsLink(): Unit = click(amlsDetailsLink)
  def clickOnHmrcStandardsForAgentsLink(): Unit = click(hmrcStandardsForAgentsLink)
  def clickOnProveYourIdentityLink(): Unit = click(proveYourIdentityLink)
  def clickPartnersAndAdvisorsStatusLink(): Unit = click(partnersAndAdvisorsLink)
  def clickAskPartnersAndAdvisorsToSignInLink(): Unit = click(askPartnersAndAdvisorsToSignInLink)
  def clickAskBusinessOwnerToSignInLink(): Unit = click(askBusinessOwnerToSignInLink)
  def clickCheckProvidedDetailsLink(): Unit = click(checkProvidedDetailsLink)
  def clickOnDeclarationLink(): Unit = click(declarationLink)
  def clickOnPartnerTaxAdvisorInformationLink(): Unit = click(partnerTaxAdvisorInformationLink)
  def clickSignOutLink(): Unit = click(signOutLink)
  def clickOnDirectorTaxAdvisorInformationLink(): Unit = click(directorTaxAdvisorInformationLink)
  def clickAskDirectorsAndOtherAdvisorsToSignInLink(): Unit = click(askDirectorsAndOtherAdvisorsToSignInLink)

  // Link locators
  private val contactDetailsLink = By.cssSelector("a[aria-describedby='contact-1-status']")
  private val agentServicesAccountDetailsLink = By.cssSelector("a[aria-describedby='accountDetails-1-status']")
  private val amlsDetailsLink = By.cssSelector("a[aria-describedby='hmrcStandards-1-status']")
  private val hmrcStandardsForAgentsLink = By.cssSelector("a[aria-describedby='hmrcStandards-2-status']")
  private val proveYourIdentityLink = By.cssSelector("a[aria-describedby='lists-1-status']")
  private val partnersAndAdvisorsLink = By.cssSelector("a[aria-describedby='lists-1-status']")
  private val askPartnersAndAdvisorsToSignInLink = By.cssSelector("a[aria-describedby='lists-2-status']")
  private val askBusinessOwnerToSignInLink = By.cssSelector("a[aria-describedby='lists-1-status']")
  private val checkProvidedDetailsLink = By.cssSelector("a[aria-describedby='lists-3-status']")
  private val declarationLink = By.cssSelector("a[aria-describedby='declaration-1-status']")
  private val partnerTaxAdvisorInformationLink = By.cssSelector("a[aria-describedby='lists-1-status']")
  private val signOutLink = By.linkText("Sign out")
  private val directorTaxAdvisorInformationLink = By.cssSelector("a[aria-describedby='lists-1-status']")
  private val askDirectorsAndOtherAdvisorsToSignInLink = By.cssSelector("a[aria-describedby='lists-2-status']")
  // Status locators
  private val contactDetailsStatus = By.id("contact-1-status")
  private val businessDetailsStatus = By.id("businessDetails-1-status")
  private val agentServicesAccountDetailsStatus = By.id("accountDetails-1-status")
  private val amlsDetailsStatus = By.id("hmrcStandards-1-status")
  private val hmrcStandardsForAgentsStatus = By.id("hmrcStandards-2-status")
  private val proveYourIdentityStatus = By.id("lists-1-status")
  private val partnersAndAdvisorsStatus = By.id("lists-1-status")
  private val askPartnersAndAdvisorsToSignInStatus = By.id("lists-2-status")
  private val askBusinessOwnerToSignInStatus = By.id("lists-1-status")
  private val checkProvidedDetailsStatus = By.id("lists-3-status")
  private val declarationStatus = By.id("declaration-1-status")
  private val partnerTaxAdvisorInformationStatus = By.id("lists-1-status")
  private val askDirectorsAndTaxAdvisorsToSignInStatus = By.id("lists-2-status")
  private val directorTaxAdvisorInformationStatus = By.id("lists-1-status")
