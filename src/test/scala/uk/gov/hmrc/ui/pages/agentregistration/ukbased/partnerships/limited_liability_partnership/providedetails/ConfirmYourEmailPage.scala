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

package uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.providedetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object ConfirmYourEmailPage
extends BasePage:

  override val path: String = "/agent-registration/provide-details/verify-email-address"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  private inline def passcodeValue: String =
    val el = getElementBy(confirmationCodeField)
    Option(el.getAttribute("value")).getOrElse("").trim

  inline def isPasscodeEmpty: Boolean = passcodeValue.isEmpty

  private val confirmationCodeField = By.id("passcode")
  private val pageHeading = By.cssSelector("main#main-content h1.govuk-heading-l")
  private val changeEmailAddressLink = By.linkText("you can change the email address you entered")

  def enterConfirmationCode(code: String): Unit = sendKeys(confirmationCodeField, code)

  def clickChangeEmailAddress(): Unit = click(changeEmailAddressLink)

  def waitUntilPasscodeEmpty(): Unit = eventually:
    withClue(s"Expected passcode field to be empty, but was '${passcodeValue}'") {
      isPasscodeEmpty shouldBe true
    }

  def submitInvalidCodeAndWaitToClear(code: String): Unit =
    enterConfirmationCode(code)
    clickContinue()
    waitUntilPasscodeEmpty()

  def forceInvalidAttempts(
    code: String,
    attempts: Int = 5
  ): Unit =
    // Ensure ready for first attempt
    waitUntilPasscodeEmpty()
    (1 to attempts).foreach { _ =>
      submitInvalidCodeAndWaitToClear(code)
    }

  def assertPageHeading(expected: String): Unit = eventually:
    getText(pageHeading).trim shouldBe expected
