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

package uk.gov.hmrc.ui.utils

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v137.network.model.Response
import org.scalatest.BeforeAndAfterEach
import uk.gov.hmrc.selenium.webdriver.Driver
import uk.gov.hmrc.ui.specs.BaseSpec

import scala.collection.mutable

/** A trait that adds request/response logging capabilities to Chrome WebDriver tests.
  */
trait SetupDevToolsForChrome
extends BeforeAndAfterEach { self: BaseSpec =>

  override def beforeEach(): Unit =
    super.beforeEach()
    Driver.instance match
      case driver: ChromeDriver if SystemPropertiesHelper.isTestRunFromIdea => setupChromeDriver(driver)
      case _ => ()

  private def setupChromeDriver(driver: ChromeDriver): Unit =
    val devTools: DevTools = driver.getDevTools
    devTools.createSession()
    // Enable network tracking
    devTools.send(
      org.openqa.selenium.devtools.v137.network.Network.enable(
        java.util.Optional.empty(),
        java.util.Optional.empty(),
        java.util.Optional.empty()
      )
    )
    // Store requests by their RequestId
    type RequestIdString = String
    val pendingRequests: mutable.Map[RequestIdString, org.openqa.selenium.devtools.v137.network.model.RequestWillBeSent] = mutable.Map()

    val ignoredExtensions = List(
      ".js",
      ".css",
      ".woff",
      ".woff2",
      "manifest.json",
      ".svg"
    )

    devTools.addListener(
      org.openqa.selenium.devtools.v137.network.Network.requestWillBeSent(),
      (request: org.openqa.selenium.devtools.v137.network.model.RequestWillBeSent) =>
        if ignoredExtensions.exists(request.getRequest.getUrl.endsWith) || ignoredExtensions.exists(request.getRequest.getUrl.startsWith)
        then ()
        else
          // Redirects are available directly in the responseReceived event, not sure why...
          if request.getRedirectResponse.isPresent
          then
            val redirectResp: Response = request.getRedirectResponse.get()
            val originalRequest = pendingRequests.get(request.getRequestId.toString)
            val location = "Location=" + redirectResp.getHeaders.get("Location")
            originalRequest.foreach: req =>
              println(
                s"<<< ${req.getRequest.getMethod} ${req.getRequest.getUrl} ${redirectResp.getStatusText}(${redirectResp.getStatus}), $location"
              )
          else ()

          pendingRequests += (request.getRequestId.toString -> request)
          println(s">>> ${request.getRequest.getMethod} ${request.getRequest.getUrl}")
    )

    devTools.addListener(
      org.openqa.selenium.devtools.v137.network.Network.responseReceived(),
      (response: org.openqa.selenium.devtools.v137.network.model.ResponseReceived) =>
        pendingRequests
          .get(response.getRequestId.toString)
          .foreach: request =>
            println(
              s"<<< ${request.getRequest.getMethod} ${request.getRequest.getUrl} ${response.getResponse.getStatusText}(${response.getResponse.getStatus}) "
            )

            if response.getResponse.getStatus < 300
            then
              // remove only 2xx, because chrome reuses request id for other status codes ...
              pendingRequests.remove(response.getRequestId.toString)
            else ()
    )

}
