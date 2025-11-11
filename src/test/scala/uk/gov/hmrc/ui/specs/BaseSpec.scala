/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.ui.specs

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import uk.gov.hmrc.selenium.webdriver.ScreenshotOnFailure
import uk.gov.hmrc.ui.utils.*

trait BaseSpec
    extends AnyFeatureSpec
    with GivenWhenThen
    with RichMatchers
    with BrowserStart
    with BrowserQuit
    with ScreenshotOnFailure
    with PageDebugInfoOnFailure
    with LazyLogging
    with SetupDevToolsForChrome:

  EnsureSystemProperties()

  export uk.gov.hmrc.ui.utils.withPos
