/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.general_partnership.application.numberofpartners

import uk.gov.hmrc.ui.domain.BusinessType.GeneralPartnership
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.general_partnership.businessdetails.application.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.ChangePartnerPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.HowManyPartnersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.PartnerFullNamePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.RemovePartnerPage
import uk.gov.hmrc.ui.specs.BaseSpec

class PartnerTaxAdvisorInformationSpec
extends BaseSpec:

  Feature("Complete Partner and Tax Advisor information section"):
    Scenario("Partnership has 5 or less partners", HappyPath):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      PartnerTaxAdvisorInformationFlow
        .FiveOrLessPartners
        .runFlow()

    Scenario("Partnership has 6 or more partners", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      PartnerTaxAdvisorInformationFlow
        .SixOrMorePartners
        .runFlow()

    Scenario("Partnership has 6 more partners but less than 6 with tax authority", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      PartnerTaxAdvisorInformationFlow
        .SixOrMorePartnersAlt
        .runFlow()

    Scenario("Change number of partners from Check your answers screen", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      PartnerTaxAdvisorInformationFlow
        .runToCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.changeNumberOfPartners()

      HowManyPartnersPage.assertPageIsDisplayed()
      HowManyPartnersPage.selectFiveOrLess()
      HowManyPartnersPage.enterExactNumber("3")
      HowManyPartnersPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertWarningTextIsDisplayed("You told us there are 3 partners. " +
        "Change the number of partners or remove 2 partners from the list before you continue.")
      CheckYourAnswersPage.removePartner("Tony Stark")

      RemovePartnerPage.assertPageIsDisplayed()
      RemovePartnerPage.selectYes()
      RemovePartnerPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertWarningTextIsDisplayed("You told us there are 3 partners. " +
        "Change the number of partners or remove 1 partner from the list before you continue.")
      CheckYourAnswersPage.removePartner("Steve Rogers")

      RemovePartnerPage.assertPageIsDisplayed()
      RemovePartnerPage.selectYes()
      RemovePartnerPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()

      PartnerTaxAdvisorInformationFlow.confirmEntries()

    Scenario("Change partner name from Check your answers screen", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      PartnerTaxAdvisorInformationFlow
        .runToCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.changePartnerName("Tony Stark")

      ChangePartnerPage.assertPageIsDisplayed()
      ChangePartnerPage.enterPartnerFullName("Bruce Banner")
      ChangePartnerPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertNameAt(4, "Bruce Banner")
