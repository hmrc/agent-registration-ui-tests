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

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.Assertions.fail
import play.api.libs.json.*

import java.net.HttpURLConnection
import java.net.URL
import scala.io.Source
import org.scalatest.concurrent.Eventually

object PasscodeHelper
extends LazyLogging
with RichMatchers:

  private val passcodesUrl = "http://localhost:9891/test-only/passcodes"

  // TODO: rewrite using sttp and eventually
  // example: https://github.com/hmrc/agent-helpdesk-ui-tests/blob/main/src/test/scala/utils/ExternalStubs.scala

  private def call(
    bearerToken: String,
    sessionId: String
  ): (Int, String) = {
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

  def getPasscode(
    bearerToken: String,
    sessionId: String,
    expectedEmail: Option[String] = None
  ): String = eventually {
    val (status, body) = call(bearerToken, sessionId)

    if (status >= 200 && status < 300) {
      val json = Json.parse(body)
      val passcodes = (json \ "passcodes").as[Seq[JsObject]]

      val latest = passcodes.lastOption.getOrElse {
        // tell Eventually "not yet" by throwing
        fail(s"No passcodes returned yet: $body")
      }

      val emailOpt = (latest \ "email").asOpt[String]
      val codeOpt = (latest \ "passcode").asOpt[String]

      // Condition: last email must match expected email, if provided
      if (!expectedEmail.forall(exp => emailOpt.exists(_.equalsIgnoreCase(exp)))) {
        fail(
          s"Latest passcode email=$emailOpt does not yet match expected=$expectedEmail"
        )
      }

      codeOpt.getOrElse {
        fail(s"Could not find passcode in response: $body")
      }
    }
    else if (status == 404) {
      // treated as "not ready yet" so Eventually will retry
      fail(s"Passcodes endpoint returned 404 (not ready yet)")
    }
    else {
      // hard failure - no point retrying
      fail(s"Passcodes call failed with status $status")
    }
  }
