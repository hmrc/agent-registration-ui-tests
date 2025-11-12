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

import scala.util.Random

object RandomData {

  private val rnd = new Random()

  /** A–Z, a–z, 0–9 only */
  def alphaNum(n: Int): String = rnd.alphanumeric.filter(_.isLetterOrDigit).take(n).mkString

  /** 0–9 only */
  def digits(n: Int): String = (1 to n).map(_ => rnd.nextInt(10)).mkString

  /** username = prefix + random alphanumerics (no separators) */
  def username(
    prefix: String = "test",
    len: Int = 10
  ): String = prefix + alphaNum(len)

  /** groupId = prefix + random alphanumerics (no separators) */
  def planetId(
    prefix: String = "planet",
    len: Int = 8
  ): String = prefix + alphaNum(len)

  /** If you still need a UUID-like string without hyphens */
  def uuidCompact(): String = java.util.UUID.randomUUID().toString.replaceAll("-", "")

}
