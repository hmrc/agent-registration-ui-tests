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

import uk.gov.hmrc.ui.utils.RandomData

object GovernmentGatewaySignInPage extends BasePage {
  override val path: String       = "/bas-gateway/sign-in"
  override val expectedH1: String = "Sign in"

  // Prefer stable IDs if you have them:
  private val usernameId = "userId"
  private val planetId   = "planetId"

  /** Fill username with a generated value and return it */
  def enterRandomUsername(prefix: String = "user"): String = {
    val value = RandomData.username(prefix)
    fillById(usernameId, value)
    value
  }

  /** Fill group ID with a generated value and return it */
  def enterRandomPlanetId(prefix: String = "pln", len: Int = 8): String = {
    val value = RandomData.planetId(prefix, len)
    fillById(planetId, value)
    value
  }
}
