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

package uk.gov.hmrc.ui.pages.stubs

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage

object AgentExternalStubCreateUserPage extends BasePage {
  override val path: String = "agents-external-stubs/user/create"

  private val affinityGroupAgentRadio = By.id("affinityGroup-4")
  private val enrolmentDropdown       = By.id("principalEnrolmentService")
  private val currentUserLink         = By.id("link_users_current")
  private val bearerTokenField        = By.id("authToken")
  private val sessionIdField          = By.id("sessionId")

  def selectAffinityGroupAgent(): Unit = click(affinityGroupAgentRadio)
  def selectEnrolmentNone(): Unit      = selectByValue(enrolmentDropdown, "none")
  def selectCurrentUserLink(): Unit    = click(currentUserLink)
  def bearerToken: String              = getText(bearerTokenField)
  def sessionId: String                = getText(sessionIdField)
}
