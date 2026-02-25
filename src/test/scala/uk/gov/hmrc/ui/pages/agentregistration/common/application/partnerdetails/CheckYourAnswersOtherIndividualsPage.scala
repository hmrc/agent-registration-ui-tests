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

package uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.agentregistration.common.application.BaseCheckYourAnswersPage
import uk.gov.hmrc.ui.utils.AppConfig

object CheckYourAnswersOtherIndividualsPage
extends BaseCheckYourAnswersPage:

  override val path: String = "/agent-registration/apply/list-details/other-relevant-individuals/check-your-answers"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  private val yesRadio = By.id("addOtherRelevantIndividuals")
  private val noRadio = By.id("addOtherRelevantIndividuals-2")

  private def removeLinkFor(name: String): By = By.xpath(
    s"""//dt[contains(@class,'govuk-summary-list__key') and contains(normalize-space(.), "$name")]
       |/ancestor::div[contains(@class,'govuk-summary-list__row')][1]
       |//a[contains(@href, '/remove-other-relevant-individual/') and contains(normalize-space(.), 'Remove')]""".stripMargin
  )

  private def changeLinkFor(name: String): By = By.xpath(
    s"""//dt[contains(@class,'govuk-summary-list__key') and contains(normalize-space(.), "$name")]
       |/ancestor::div[contains(@class,'govuk-summary-list__row')][1]
       |//a[contains(@href, '/change-other-relevant-individual/') and contains(normalize-space(.), 'Change')]""".stripMargin
  )

  def selectYes(): Unit = click(yesRadio)
  def selectNo(): Unit = click(noRadio)

  def removePartner(name: String): Unit = click(removeLinkFor(name))
  def changePartnerName(name: String): Unit = click(changeLinkFor(name))
