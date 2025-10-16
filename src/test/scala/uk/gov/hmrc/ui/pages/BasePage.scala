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

import org.openqa.selenium.{By, WebElement}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import java.time.Duration
import uk.gov.hmrc.selenium.component.PageObject
import uk.gov.hmrc.selenium.webdriver.Driver
import uk.gov.hmrc.ui.utils.AppConfig
import org.openqa.selenium.support.ui.Select

trait BasePage extends PageObject {
  def path: String
  def expectedH1: String
  def expectedRadioIds: Seq[String] = Seq.empty

  /** Set true on stub/setup pages where H1 is dynamic or irrelevant. */
  protected def skipH1Assertion: Boolean = false

  def url: String   = AppConfig.baseUrl + path
  def open(): Unit  = Driver.instance.get(url)
  def title: String = Driver.instance.getTitle

  /** renamed to avoid conflict with Object.wait() */
  protected def webDriverWait: WebDriverWait =
    new WebDriverWait(Driver.instance, Duration.ofSeconds(10))

  protected def findVisible(locator: By): WebElement =
    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(locator))

  protected def continueSelector: String =
    "button.govuk-button[type='submit'], input.govuk-button[type='submit'], a.govuk-button[role='button']"

  protected def h1: WebElement =
    findVisible(By.cssSelector("h1"))

  /** Common assertion after any navigation */
  def assertPageIsDisplayed(): Unit = {
    webDriverWait.until(ExpectedConditions.urlContains(path))

    // Only assert H1 when it's meaningful for this page
    if (!skipH1Assertion) {
      val actualH1 = h1.getText.trim
      assert(
        actualH1 == expectedH1,
        s"Expected H1 '$expectedH1' but got '$actualH1'"
      )
    }
  }

  def clickContinue(cssOverride: Option[String] = None, timeoutSec: Int = 10): Unit = {
    val driver = Driver.instance
    val by     = By.cssSelector(cssOverride.getOrElse(continueSelector))
    val wait   = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))

    // Re-locate each poll so we don't hold a stale reference
    val el = wait.until(
      ExpectedConditions.refreshed(
        ExpectedConditions.visibilityOfElementLocated(by)
      )
    )
    el.click()
  }

  /** --- Helpers for GOV.UK form interactions --- */
  def selectRadioById(id: String): Unit =
    Driver.instance.findElement(By.id(id)).click()

  def fillById(id: String, value: String): Unit = {
    val input = Driver.instance.findElement(By.id(id))
    input.clear()
    input.sendKeys(value)
  }

  /** Select from a native <select> by its id, using the option's value attribute */
  def selectFromDropdownByIdValue(selectId: String, value: String): Unit = {
    val selectEl: WebElement = Driver.instance.findElement(By.id(selectId)) // MUST be <select>
    new Select(selectEl).selectByValue(value)
  }
}
