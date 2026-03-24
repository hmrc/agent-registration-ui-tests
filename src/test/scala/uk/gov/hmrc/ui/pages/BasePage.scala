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
import uk.gov.hmrc.selenium.webdriver.Driver
import uk.gov.hmrc.ui.utils.RichMatchers

trait BasePage
extends PageObject:

  val path: String
  val baseUrl: String

  /** Call this method after navigation to ensure you've reached the expected page.
    */
  inline def assertPageIsDisplayed(): Unit

  final def url: String = baseUrl + path

  def clickContinue(continueSelector: By = continueSelector): Unit = click(continueSelector)

  def enterTextAndBlur(
    locator: By,
    text: String
  ): Unit =
    val element = findElementBy(locator).getOrElse(
      throw new NoSuchElementException(s"Element not found for locator: $locator")
    )
    element.click()
    element.sendKeys(text)
    element.sendKeys(org.openqa.selenium.Keys.TAB)

  protected def valueLocatorFor(keyText: String): By = By.xpath(
    "//div[contains(@class,'govuk-summary-list__row')]" +
      s"[normalize-space(.//dt[contains(@class,'govuk-summary-list__key')])='$keyText']" +
      "//dd[contains(@class,'govuk-summary-list__value')]"
  )

  protected def valueLocatorFor(
    keyText: String,
    occurrence: Int
  ): By =
    require(occurrence >= 1, s"occurrence must be >= 1, but was $occurrence")
    By.xpath(
      s"(//div[contains(@class,'govuk-summary-list__row')]" +
        s"[normalize-space(.//dt[contains(@class,'govuk-summary-list__key')])='$keyText']" +
        s"//dd[contains(@class,'govuk-summary-list__value')])[$occurrence]"
    )

  def getSummaryValueFor(keyText: String): String = getText(valueLocatorFor(keyText)).trim

  def getSummaryValueFor(
    keyText: String,
    occurrence: Int
  ): String = getText(valueLocatorFor(keyText, occurrence)).trim

  def assertSummaryRow(
    key: String,
    expectedValue: String
  ): Unit = eventually {
    val actual = getText(valueLocatorFor(key)).trim
    withClue(s"For summary key '$key': ") {
      actual shouldBe expectedValue
    }
  }

  def assertSummaryRow(
    key: String,
    expectedValue: String,
    occurrence: Int
  ): Unit = eventually {
    val actual = getText(valueLocatorFor(key, occurrence)).trim
    withClue(s"For summary key '$key' (occurrence $occurrence): ") {
      actual shouldBe expectedValue
    }
  }

  def assertNameAt(
    index0: Int,
    expectedName: String
  ): Unit = eventually {
    require(index0 >= 0, s"index0 must be >= 0, but was $index0")
    val index1 = index0 + 1 // XPath is 1-based

    val keyCellAtIndex = By.xpath(
      s"""(
         |  //dl[contains(@class,'govuk-summary-list')]
         |    //div[contains(@class,'govuk-summary-list__row')]
         |      //dt[contains(@class,'govuk-summary-list__key')]
         |)[$index1]""".stripMargin
    )

    val actual = getText(keyCellAtIndex).trim
    withClue(s"Name at row index $index0: ") {
      actual shouldBe expectedName
    }
  }

  def assertSummaryRowNotPresent(key: String): Unit = eventually:
    findElementBy(valueLocatorFor(key)) shouldBe None

  def assertSummaryRowPresent(key: String): Unit = eventually:
    findElementBy(valueLocatorFor(key)) shouldBe defined

  def clickBrowserBack(): Unit = Driver.instance.navigate().back()

  export RichMatchers.*

  private val continueSelector: By =
    val selectorString = "button.govuk-button[type='submit'], input.govuk-button[type='submit'], a.govuk-button[role='button']"
    By.cssSelector(selectorString)
