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

package uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.fastforwardlinks

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.EntryPage
import uk.gov.hmrc.ui.pages.PageObject.click
import uk.gov.hmrc.ui.pages.PageObject.getCurrentUrl
import uk.gov.hmrc.ui.utils.AppConfig
import uk.gov.hmrc.ui.utils.RichMatchers.eventually
import uk.gov.hmrc.ui.utils.RichMatchers.shouldBe

object FastForwardLinksPage
extends EntryPage:

  override val path: String = "/agent-registration/test-only/fast-forward"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val logInLink = By.linkText("Log In")
  private val aboutYourBusinessLink = By.linkText("About your business")
  private val contactDetailsLink = By.linkText("Applicant Contact Details")
  private val agentDetailsLink = By.linkText("Agent services account details")
  private val amlsDetailsLink = By.linkText("Anti-money laundering supervision details")
  private val agentStandardsLink = By.linkText("HMRC standard for agents")
  private val declarationLink = By.linkText("Declaration")

  def clickLogIn(): Unit = click(logInLink)
  def clickAboutYourBusinessLink(): Unit = click(aboutYourBusinessLink)
  def clickContactDetailsLink(): Unit = click(contactDetailsLink)
  def clickAgentDetailsLink(): Unit = click(agentDetailsLink)
  def clickAmlsDetailsLink(): Unit = click(amlsDetailsLink)
  def clickAgentStandardsLink(): Unit = click(agentStandardsLink)
  def clickDeclarationLink(): Unit = click(declarationLink)
