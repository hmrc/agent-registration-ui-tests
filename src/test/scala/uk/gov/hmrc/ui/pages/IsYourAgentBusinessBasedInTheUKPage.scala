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

package uk.gov.hmrc.ui.pages

import org.openqa.selenium.By

object IsYourAgentBusinessBasedInTheUKPage extends BasePage {
  override val path: String       = "/agent-registration/apply"
  override val expectedH1: String = "Is your agent business based in the UK?"

  private val yesRadio = By.id("agentType")
  private val noRadio  = By.id("agentType-2")

  def selectYes(): Unit = click(yesRadio)
  def selectNo(): Unit  = click(noRadio)

}
