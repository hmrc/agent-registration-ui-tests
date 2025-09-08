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

import scala.util.chaining.scalaUtilChainingOps

object EnsureSystemProperties:

  def apply(): Unit =
    "browser".pipe(p => if (System.getProperty(p) == null) System.setProperty(p, "chrome"))
    "environment".pipe(p => if (System.getProperty(p) == null) System.setProperty(p, "local"))
    // other options?

    val isTestRunFromIdea =
      System.getProperty("idea.test.cyclic.buffer.size") != null ||
        System.getProperty("idea.launcher.port") != null ||
        System.getProperty("idea.launcher.bin.path") != null ||
        System.getProperty("java.class.path", "").contains("idea_rt.jar")

    // show browser when running tests from intellij idea
    if (isTestRunFromIdea)
      "browser.option.headless".pipe(p => if (System.getProperty(p) == null) System.setProperty(p, "false"))
