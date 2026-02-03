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

package uk.gov.hmrc.ui.pages.agentregistration.common.application.declaration

import org.openqa.selenium.By
import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.domain.BusinessType.GeneralPartnership
import uk.gov.hmrc.ui.domain.BusinessType.LLP
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object DeclarationPage
extends BasePage:

  override val path: String = "/agent-registration/apply/agent-declaration/confirm-declaration"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  private val bodyParas: By = By.id("main-content")

  def assertAuthorisedByTextDisplayed(businessName: String): Unit =
    getText(bodyParas) should include(s"I am authorised by $businessName to apply for an agent services account with HMRC.")

  def assertNoAuthorisedByTextDisplayed(): Unit =
    getText(bodyParas) should not include
      "I am authorised by"
