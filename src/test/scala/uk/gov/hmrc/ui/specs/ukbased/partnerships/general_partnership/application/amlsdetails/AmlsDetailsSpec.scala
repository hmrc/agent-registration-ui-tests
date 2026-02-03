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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.general_partnership.application.amlsdetails

import uk.gov.hmrc.ui.domain.BusinessType
import uk.gov.hmrc.ui.domain.BusinessType.*
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow.AmlsDetailsOption
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow.AmlsDetailsOption.NonHmrcSupervisoryBody
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.general_partnership.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.EvidenceOfAmlSupervisionPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.WhatRegistrationNumberPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.amldetails.WhatSupervisoryBodyPage
import uk.gov.hmrc.ui.specs.BaseSpec

class AmlsDetailsSpec
extends BaseSpec:

  Feature("Complete Anti-money laundering section"):
    Scenario("User selects HMRC as their Supervisory Body", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()
      TaskListPage.assertAmlsDetailsStatus("Completed")

    Scenario("User selects non-HMRC Supervisory Body", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow
        .WhenNonHmrcSupervisoryBody
        .runFlow()
      TaskListPage.assertAmlsDetailsStatus("Completed")

    Scenario("Changes Registration Number from CYA page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow
        .RunToCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.clickChangeFor("Registration number")

      WhatRegistrationNumberPage.assertPageIsDisplayed()
      WhatRegistrationNumberPage.enterRegistrationNumber("XAML00000111111")
      WhatRegistrationNumberPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Registration number", "XAML00000111111")

    Scenario("Changes Supervisory Body from CYA page", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow
        .RunToCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.clickChangeFor("Supervisory body")

      WhatSupervisoryBodyPage.assertPageIsDisplayed()
      WhatSupervisoryBodyPage.enterSupervisor("Association of Chartered Certified Accountants (ACCA)")
      WhatSupervisoryBodyPage.clickContinue()

      AmlsDetailsFlow.enterRegistrationNumber()
      AmlsDetailsFlow.enterSupervisionExpiryDate()
      AmlsDetailsFlow.uploadSupervisionEvidence()
      AmlsDetailsFlow.checkYourAnswersExpanded()

    Scenario("Upload evidence file exceeding 5MB in size", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow.startJourney()
      AmlsDetailsFlow.enterSupervisoryBody(NonHmrcSupervisoryBody)
      AmlsDetailsFlow.enterRegistrationNumber()
      AmlsDetailsFlow.enterSupervisionExpiryDate()

      EvidenceOfAmlSupervisionPage.assertPageIsDisplayed()
      EvidenceOfAmlSupervisionPage.uploadFileFromResources("Aml-Evidence-plus-5mb.docx")
      EvidenceOfAmlSupervisionPage.clickContinue()
      EvidenceOfAmlSupervisionPage.assertErrorMessage("The selected file must not be larger than 5MB")

    Scenario("Upload evidence file with virus", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow.startJourney()
      AmlsDetailsFlow.enterSupervisoryBody(NonHmrcSupervisoryBody)
      AmlsDetailsFlow.enterRegistrationNumber()
      AmlsDetailsFlow.enterSupervisionExpiryDate()

      EvidenceOfAmlSupervisionPage.assertPageIsDisplayed()
      EvidenceOfAmlSupervisionPage.uploadFileFromResources("Aml-Evidence-Virus.txt")
      EvidenceOfAmlSupervisionPage.clickContinue()
      EvidenceOfAmlSupervisionPage.assertErrorMessage("A virus has been detected in your uploaded file, try uploading another file.")

    Scenario("Upload evidence file in invalid format", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow.startJourney()
      AmlsDetailsFlow.enterSupervisoryBody(NonHmrcSupervisoryBody)
      AmlsDetailsFlow.enterRegistrationNumber()
      AmlsDetailsFlow.enterSupervisionExpiryDate()

      EvidenceOfAmlSupervisionPage.assertPageIsDisplayed()
      EvidenceOfAmlSupervisionPage.uploadFileFromResources("Aml-Evidence-Invalid-ext..zip")
      EvidenceOfAmlSupervisionPage.clickContinue()
      EvidenceOfAmlSupervisionPage.assertErrorMessage(
        "The selected file must be a JPG, JPEG, PNG, TIFF, PDF, TXT, MSG, DOC, DOCX, XLS, XLSX, PPT, PPTX, ODT, ODS or ODP file"
      )
