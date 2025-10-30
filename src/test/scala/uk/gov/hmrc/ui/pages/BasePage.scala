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
import org.openqa.selenium.By
import uk.gov.hmrc.selenium.webdriver.Driver
import java.time.Duration
import org.openqa.selenium.support.ui.{ExpectedConditions, FluentWait}

trait BasePage extends PageObject {
  def path: String
  def expectedH1: String
  def expectedRadioIds: Seq[String] = Seq.empty

  /** Set true on stub/setup pages where H1 is dynamic or irrelevant. */
  protected def skipH1Assertion: Boolean = false

  // ---- Navigation ----
  def url: String   = AppConfig.baseUrl + path
  def title: String = getTitle

  // ---- Common selectors ----
  protected def continueSelector: String =
    "button.govuk-button[type='submit'], input.govuk-button[type='submit'], a.govuk-button[role='button']"

  // ---- Assertions ----
  /** Call after navigation to assert we're on the expected page */
  private def urlPath(url: String): String =
    try
      new java.net.URI(url).getPath
    catch {
      case _: Throwable => url
    }

  def assertPageIsDisplayed(timeoutSec: Long = 15): Unit = {
    val driver = Driver.instance
    val wait   = new FluentWait(driver)
      .withTimeout(Duration.ofSeconds(timeoutSec))
      .pollingEvery(Duration.ofMillis(200))

    val h1By = By.cssSelector("main h1, #main-content h1, h1")

    // 1) Wait until EITHER the URL path contains ours OR an <h1> is present (no text check here)
    wait.until(
      ExpectedConditions.or(
        (_: org.openqa.selenium.WebDriver) => urlPath(driver.getCurrentUrl).contains(path),
        ExpectedConditions.presenceOfElementLocated(h1By)
      )
    )

    // 2) Final URL assertion (now navigation should be settled enough)
    val current = driver.getCurrentUrl
    assert(
      urlPath(current).contains(path),
      s"Expected URL path to contain '$path' but was: $current"
    )

    // 3) Optional H1 text assertion (do this AFTER the presence wait)
    if (!skipH1Assertion) {
      val actual = getText(h1By).trim // PageObject.getText waits for presence again
      assert(actual == expectedH1, s"Expected H1 '$expectedH1' but got '$actual' at URL: $current")
    }
  }

  // ---- Interactions (thin wrappers over PageObject helpers) ----
  def clickContinue(cssOverride: Option[String] = None): Unit =
    click(By.cssSelector(cssOverride.getOrElse(continueSelector)))
}
