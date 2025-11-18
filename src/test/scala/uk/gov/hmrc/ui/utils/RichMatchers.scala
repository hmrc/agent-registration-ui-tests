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

import org.scalatest.*
import org.scalatest.concurrent.Futures.scaled
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.diagrams.Diagrams
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Millis
import org.scalatest.time.Seconds
import org.scalatest.time.Span

object RichMatchers
extends RichMatchers

trait RichMatchers
extends Matchers,
  Diagrams,
  TryValues,
  EitherValues,
  OptionValues,
  AppendedClues,
  ScalaFutures,
  Eventually:

  override implicit val patienceConfig: PatienceConfig = {
    val config = PatienceConfig(
      timeout = scaled(Span(3, Seconds)),
      interval = scaled(Span(100, Millis))
    )

    // Debug log to confirm scaling
    println(s"[DEBUG] Effective PatienceConfig -> timeout: ${config.timeout}, interval: ${config.interval}")
    println(s"[DEBUG] SCALATEST_SPAN_SCALE_FACTOR: ${sys.env.getOrElse("SCALATEST_SPAN_SCALE_FACTOR", "not set")}")

    config
  }
