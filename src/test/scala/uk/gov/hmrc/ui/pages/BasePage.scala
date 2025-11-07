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

import uk.gov.hmrc.selenium.component.PageObject
import uk.gov.hmrc.ui.utils.AppConfig
import org.openqa.selenium.{By, WebDriver}
import uk.gov.hmrc.selenium.webdriver.Driver

import java.time.Duration
import org.openqa.selenium.support.ui.{ExpectedConditions, FluentWait}

trait BasePage extends PageObject {
  def path: String
  def expectedRadioIds: Seq[String] = Seq.empty

  /** Set true on stub/setup pages where H1 is dynamic or irrelevant. */
  protected def skipH1Assertion: Boolean = false

  // ---- Navigation ----
  def url: String                                             = AppConfig.baseUrl + path
  def title: String                                           = getTitle
  def clickContinue(cssOverride: Option[String] = None): Unit =
    click(By.cssSelector(cssOverride.getOrElse(continueSelector)))

  // ---- Common selectors ----
  protected def continueSelector: String =
    "button.govuk-button[type='submit'], input.govuk-button[type='submit'], a.govuk-button[role='button']"

  // in BasePage
  def goBack(): Unit =
    Driver.instance.navigate().back()

  // ---- Assertions ----
  /** Call after navigation to assert we're on the expected page */
  def assertPageIsDisplayed(timeoutSec: Int = 5, pollingMs: Int = 500): Unit = {
    val driver = Driver.instance
    new FluentWait[WebDriver](driver)
      .withTimeout(Duration.ofSeconds(timeoutSec))
      .pollingEvery(Duration.ofMillis(pollingMs))
      .until(ExpectedConditions.urlContains(path)) // `path` comes from this page object
  }
}
