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

import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import uk.gov.hmrc.selenium.webdriver.Driver
import uk.gov.hmrc.ui.utils.RichMatchers.*

import java.lang
import scala.jdk.CollectionConverters.*
import scala.util.Try
import scala.util.chaining.scalaUtilChainingOps

object PageObject
extends PageObject

/** Enhanced PageObject trait, based on [[uk.gov.hmrc.selenium.component.PageObject]]. This implementation improves test failure debugging by using ScalaTest
  * assertions instead of raw Selenium exceptions.
  *
  * Note: The referenced [[uk.gov.hmrc.selenium.component.PageObject]] throws plain Selenium exceptions which cause tests to error rather than fail cleanly.
  * This makes debugging more difficult as the raw exception stack traces often obscure the actual failure cause. This enhanced version uses ScalaTest
  * assertions for cleaner test failures. This version:
  *   - Tests fail with clear ScalaTest assertion messages showing exact failure location
  *   - Avoids hard-to-debug Selenium exception stack traces that often obscure the root cause
  *   - Makes test failures more actionable by providing relevant context and clean failure messages
  */
trait PageObject:

  inline def findElementBy(locator: By): Option[WebElement] = eventually:
    Try(Driver.instance.findElement(locator))
      .toOption
  inline def getElementBy(locator: By): WebElement = findElementBy(locator).value

  inline def findElementsBy(locator: By): Seq[WebElement] = eventually:
    Driver
      .instance
      .findElements(locator)
      .asScala
      .toSeq

  inline def click(locator: By): Unit = getElementBy(locator).click()

  inline def get(url: String): Unit = Driver.instance.get(url)

  inline def getCurrentUrl: String = Driver.instance.getCurrentUrl

  inline def getPageSource: String = Driver.instance.getPageSource

  inline def getText(locator: By): String = findElementBy(locator).value.getText

  inline def getTitle: String = Driver.instance.getTitle

  inline def sendKeys(
    locator: By,
    value: String
  ): Unit = findElementBy(locator)
    .value
    .tap(_.clear())
    .sendKeys(value)

  inline def sendKeys(
    locator: By,
    keys: Keys*
  ): Unit = findElementBy(locator)
    .value
    .tap(_.clear())
    .tap(element => keys.foreach(key => element.sendKeys(key)))

  inline def isSelected(locator: By): Boolean = findElementBy(locator).value.isSelected

  inline def selectCheckbox(locator: By): Unit = findElementBy(locator)
    .value
    .tap(element =>
      if !element.isSelected then element.click() else ()
    )

  inline def deselectCheckbox(locator: By): Unit = findElementBy(locator)
    .value
    .tap(element =>
      if element.isSelected then element.click() else ()
    )

  private inline def getSelect(locator: By): Select = findElementBy(locator)
    .value
    .pipe(element => new Select(element))

  inline def selectByValue(
    locator: By,
    value: String
  ): Unit = getSelect(locator).selectByValue(value)

  def deselectByValue(
    locator: By,
    value: String
  ): Unit = getSelect(locator).deselectByValue(value)

  protected def selectByVisibleText(
    locator: By,
    value: String
  ): Unit = getSelect(locator).selectByVisibleText(value)

  protected def deselectByVisibleText(
    locator: By,
    value: String
  ): Unit = getSelect(locator).deselectByVisibleText(value)

  inline def clear(locator: By): Unit = findElementBy(locator).value.clear()

  protected def waitForInvisibilityOfElementWithText(
    locator: By,
    text: String
  ): Unit = eventually:
    ExpectedConditions
      .invisibilityOfElementWithText(locator, text)
      .apply(Driver.instance)
      .booleanValue() shouldBe true
