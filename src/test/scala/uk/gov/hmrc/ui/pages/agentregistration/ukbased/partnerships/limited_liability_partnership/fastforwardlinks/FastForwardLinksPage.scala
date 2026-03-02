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
import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.domain.BusinessType.*
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

  private def sectionHeading(businessType: BusinessType): String =
    businessType match
      case LLP => "Limited Liability Partnership"
      case GeneralPartnership => "General Partnership"
      case ScottishPartnership => "Scottish Partnership"
      case SoleTrader => "Sole trader"
      case LimitedPartnership => "Limited Partnership"
      case LimitedCompany => "Limited Company"
      case ScottishLimitedPartnership => "Scottish Limited Partnership"

  private def linkInSection(
    businessType: BusinessType,
    linkText: String
  ): By = By.xpath(
    s"//h2[normalize-space()='${sectionHeading(businessType)}']/following-sibling::ul[1]//a[normalize-space()='$linkText']"
  )

  def clickLogIn(): Unit = click(logInLink)

  def clickAboutYourBusinessLink(businessType: BusinessType): Unit = click(linkInSection(businessType, "About your business"))

  def clickContactDetailsLink(businessType: BusinessType): Unit = click(linkInSection(businessType, "Applicant Contact Details"))

  def clickAgentDetailsLink(businessType: BusinessType): Unit = click(linkInSection(businessType, "Agent services account details"))

  def clickAmlsDetailsLink(businessType: BusinessType): Unit = click(linkInSection(businessType, "Anti-money laundering supervision details"))

  def clickAgentStandardsLink(businessType: BusinessType): Unit = click(linkInSection(businessType, "HMRC standard for agents"))

  def clickDeclarationLink(businessType: BusinessType): Unit = click(linkInSection(businessType, "Declaration"))
