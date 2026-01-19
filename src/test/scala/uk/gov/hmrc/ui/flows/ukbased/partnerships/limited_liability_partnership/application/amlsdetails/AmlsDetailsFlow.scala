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

package uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.amlsdetails

import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.EvidenceOfAmlSupervisionPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.EvidenceUploadCompletePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.WhatRegistrationNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.WhatSupervisoryBodyPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.WhenDoesSupervisionRunOutPage

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object AmlsDetailsFlow:

  private val today = LocalDate.now()
  private val nextYearDate = today.plusYears(1)
  private val nonHmrcSupervisoryBody = "Association of Chartered Certified Accountants (ACCA)"
  private val evidenceFile = "Aml-Evidence.docx"

  sealed trait AmlsDetailsOption
  object AmlsDetailsOption:

    case object HmrcIsSupervisoryBody
    extends AmlsDetailsOption
    case object NonHmrcSupervisoryBody
    extends AmlsDetailsOption

  object WhenHmrcAreSupervisoryBody:
    def runFlow(): Unit =
      startJourney()
      enterSupervisoryBody(AmlsDetailsOption.HmrcIsSupervisoryBody)
      enterRegistrationNumber()
      checkYourAnswers()

  object WhenNonHmrcSupervisoryBody:
    def runFlow(): Unit =
      startJourney()
      enterSupervisoryBody(AmlsDetailsOption.NonHmrcSupervisoryBody)
      enterRegistrationNumber()
      enterSupervisionExpiryDate()
      uploadSupervisionEvidence(evidenceFile)
      checkYourAnswersExpanded()

  object RunToCheckYourAnswers:
    def runFlow(): Unit =
      startJourney()
      enterSupervisoryBody(AmlsDetailsOption.HmrcIsSupervisoryBody)
      enterRegistrationNumber()

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertAmlsDetailsStatus("Incomplete")
    TaskListPage.clickOnAmlsDetailsLink()

  def enterSupervisoryBody(option: AmlsDetailsOption): Unit =
    WhatSupervisoryBodyPage.assertPageIsDisplayed()
    option match
      case AmlsDetailsOption.HmrcIsSupervisoryBody => WhatSupervisoryBodyPage.enterSupervisor()
      case AmlsDetailsOption.NonHmrcSupervisoryBody => WhatSupervisoryBodyPage.enterSupervisor(nonHmrcSupervisoryBody)
    WhatSupervisoryBodyPage.clickContinue()

  def enterRegistrationNumber(): Unit =
    WhatRegistrationNumberPage.assertPageIsDisplayed()
    WhatRegistrationNumberPage.enterRegistrationNumber()
    WhatRegistrationNumberPage.clickContinue()

  def enterSupervisionExpiryDate(): Unit =
    WhenDoesSupervisionRunOutPage.assertPageIsDisplayed()
    WhenDoesSupervisionRunOutPage.enterDay()
    WhenDoesSupervisionRunOutPage.enterMonth()
    WhenDoesSupervisionRunOutPage.enterYear()
    WhenDoesSupervisionRunOutPage.clickContinue()

  def uploadSupervisionEvidence(fileName: String = evidenceFile): Unit =
    EvidenceOfAmlSupervisionPage.assertPageIsDisplayed()
    EvidenceOfAmlSupervisionPage.uploadFileFromResources(fileName)
    EvidenceOfAmlSupervisionPage.clickContinue()
    EvidenceUploadCompletePage.assertPageIsDisplayed()
    EvidenceUploadCompletePage.clickContinue()

  def checkYourAnswers(): Unit =
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Supervisory body", "HM Revenue and Customs (HMRC)")
    CheckYourAnswersPage.assertSummaryRow("Registration number", "XAML00000123456")
    CheckYourAnswersPage.clickContinue()

  def checkYourAnswersExpanded(): Unit =
    CheckYourAnswersPage.assertPageIsDisplayed()
    CheckYourAnswersPage.assertSummaryRow("Supervisory body", nonHmrcSupervisoryBody)
    CheckYourAnswersPage.assertSummaryRow("Registration number", "XAML00000123456")
    CheckYourAnswersPage.assertSummaryRow("Supervision expiry date", formattedDate())
    CheckYourAnswersPage.assertSummaryRow("Evidence of anti-money laundering supervision", "Aml-Evidence.docx")
    CheckYourAnswersPage.clickContinue()

  def formattedDate(): String = nextYearDate.format(
    DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK)
  )
