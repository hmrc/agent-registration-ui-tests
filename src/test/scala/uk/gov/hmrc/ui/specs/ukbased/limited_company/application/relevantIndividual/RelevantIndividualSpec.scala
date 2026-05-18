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

package uk.gov.hmrc.ui.specs.ukbased.limited_company.application.relevantIndividual

import uk.gov.hmrc.ui.domain.BusinessType.LimitedCompany
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.DirectorTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.ukbased.soletrader.application.businessdetails.BusinessDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.IndividualDetailsPage.*
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.agentdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.{AskPartnersToSignInPage, AskSoleTraderToSignInPage, CheckWhoProvidedDetailsPage}
import uk.gov.hmrc.ui.specs.BaseSpec

class RelevantIndividualSpec
  extends BaseSpec:

  Feature("Applicant initiates Relevant Individual details journey"):
    Scenario(
      "Applicant begins Individual details journey from tracker",
      TagLimitedCompany
    ):

      FastForwardLinks
        .FastForward
        .runFlow(
          AgentStandards,
          LimitedCompany
        )

      DirectorTaxAdvisorInformationFlow
        .FiveOrLessDirectors
        .runFlow()

      TaskListPage.assertPageIsDisplayed()
      TaskListPage.clickAskAllRelevantIndividualsSignInLink()

      AskPartnersToSignInPage.assertPageIsDisplayed()
      AskPartnersToSignInPage.clickContinue()

      TaskListPage.assertPageIsDisplayed()
      TaskListPage.clickCheckProvidedDetailsLink()

      CheckWhoProvidedDetailsPage.assertPageIsDisplayed()

      CheckWhoProvidedDetailsPage.clickWhatHappensIfSomeoneCannotSignIn()
      CheckWhoProvidedDetailsPage.assertRevealIsDisplayed()

      CheckWhoProvidedDetailsPage.clickApplicantIndividualDetailsLink()
      SelectRelevantIndividualPage.assertPageIsDisplayed()
      SelectRelevantIndividualPage.selectRelevantIndividual("Bruce Wayne")
      SelectRelevantIndividualPage.clickContinue()

      RelevantIndividualDateOfBirthPage.assertPageIsDisplayed()
      RelevantIndividualDateOfBirthPage.fillInDateOfBirth("01", "01", "1990")
      RelevantIndividualDateOfBirthPage.clickContinue()

      RelevantIndividualTelephoneNumberPage.assertPageIsDisplayed()
      RelevantIndividualTelephoneNumberPage.fillInTelephoneNumber("01234567890")
      RelevantIndividualTelephoneNumberPage.clickContinue()

      RelevantIndividualEmailAddressPage.assertPageIsDisplayed()
      RelevantIndividualEmailAddressPage.fillInEmailAddress("test.user@example.com")
      RelevantIndividualEmailAddressPage.clickContinue()

      RelevantIndividualNationalInsuranceNumberPage.assertPageIsDisplayed()
      RelevantIndividualNationalInsuranceNumberPage.fillInNationalInsuranceNumber("AB123456C")
      RelevantIndividualNationalInsuranceNumberPage.clickContinue()

      RelevantIndividualSelfAssessmentUtrPage.assertPageIsDisplayed()
      RelevantIndividualSelfAssessmentUtrPage.fillInSelfAssessmentUtr("1234567890")
      RelevantIndividualSelfAssessmentUtrPage.clickContinue()

      RelevantIndividualCheckYourAnswersPage.assertPageIsDisplayed()
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Date of birth", "1 January 1990")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Telephone number", "01234567890")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Email address", "test.user@example.com")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Do you know Bruce Wayne’s National Insurance number?", "Yes")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("National Insurance number", "AB123456C")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Do you know Bruce Wayne’s Self Assessment Unique Taxpayer Reference?", "Yes")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Self Assessment Unique Taxpayer Reference", "1234567890")
      RelevantIndividualCheckYourAnswersPage.clickContinue()

      CheckWhoProvidedDetailsPage.assertPageIsDisplayed()
      CheckWhoProvidedDetailsPage.clickContinue()

      TaskListPage.assertPageIsDisplayed()






      


