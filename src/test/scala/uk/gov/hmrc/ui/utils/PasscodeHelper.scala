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

import play.api.libs.json.*

import java.net.HttpURLConnection
import java.net.URL
import scala.io.Source

object PasscodeHelper {

  private val passcodesUrl = "http://localhost:9891/test-only/passcodes"

  def getPasscode(
    bearerToken: String,
    sessionId: String
  ): String = {

    def call(): (Int, String) = {
      val url = new URL(passcodesUrl)
      val conn = url.openConnection().asInstanceOf[HttpURLConnection]

      conn.setRequestMethod("GET")
      conn.setRequestProperty("Authorization", s"$bearerToken")
      conn.setRequestProperty("X-Session-ID", sessionId)

      val status = conn.getResponseCode
      val stream =
        if (status >= 200 && status < 300)
          conn.getInputStream
        else
          conn.getErrorStream
      val body =
        if (stream != null)
          try Source.fromInputStream(stream).mkString
          finally stream.close()
        else
          ""

      println(s"[DEBUG] Passcodes call -> status=$status, body=$body")
      (status, body)
    }

    var attempts = 0
    while (attempts < 5) {
      val (status, body) = call()
      if (status >= 200 && status < 300) {
        val json = Json.parse(body)
        val passcodes = (json \ "passcodes").as[Seq[JsObject]]
        return passcodes.lastOption
          .flatMap(obj => (obj \ "passcode").asOpt[String])
          .getOrElse(sys.error(s"Could not find passcode in response: $body"))
      }
      else if (status == 404) {
        // passcode not ready yet – wait briefly then try again
        attempts += 1
        Thread.sleep(500) // half a second between tries
      }
      else {
        throw new IllegalArgumentException(s"Passcodes call failed with status $status")
      }
    }

    throw new IllegalStateException("Passcode not available after 5 attempts")
  }

}
