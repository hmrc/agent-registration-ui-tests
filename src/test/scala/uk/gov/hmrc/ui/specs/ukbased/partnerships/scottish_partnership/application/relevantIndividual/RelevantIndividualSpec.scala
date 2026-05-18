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

package uk.gov.hmrc.ui.specs.ukbased.partnerships.scottish_partnership.application.relevantIndividual

import uk.gov.hmrc.ui.domain.BusinessType.ScottishPartnership
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.partnerInformation.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.pages.agentregistration.IndividualDetailsPage.*
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.{AskPartnersToSignInPage, CheckWhoProvidedDetailsPage}
import uk.gov.hmrc.ui.specs.BaseSpec

class RelevantIndividualSpec
extends BaseSpec:

  Feature("Applicant initiates Relevant Individual details journey"):
    Scenario(
      "Applicant begins Individual details journey from tracker",
      TagScottishPartnership
    ):

      FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, ScottishPartnership)

      PartnerTaxAdvisorInformationFlow
        .singlePartner
        .runFlow()

      TaskListPage.assertPageIsDisplayed()
      TaskListPage.clickAskPartnersAndAdvisorsToSignInLink()

      AskPartnersToSignInPage.assertPageIsDisplayed()
      AskPartnersToSignInPage.clickContinue()

      TaskListPage.assertPageIsDisplayed()
      TaskListPage.clickCheckProvidedDetailsLink()

      CheckWhoProvidedDetailsPage.assertPageIsDisplayed()

      CheckWhoProvidedDetailsPage.clickWhatHappensIfSomeoneCannotSignIn()
      CheckWhoProvidedDetailsPage.assertRevealIsDisplayed()

      CheckWhoProvidedDetailsPage.clickApplicantIndividualDetailsLink()
      SelectRelevantIndividualPage.assertPageIsDisplayed()
      SelectRelevantIndividualPage.selectRelevantIndividual("Bobby Boucher")
      SelectRelevantIndividualPage.clickContinue()

      RelevantIndividualDateOfBirthPage.assertPageIsDisplayed()
      RelevantIndividualDateOfBirthPage.fillInDateOfBirth("01", "01", "1990")
      RelevantIndividualDateOfBirthPage.clickContinue()

      RelevantIndividualTelephoneNumberPage.assertPageIsDisplayed()
      RelevantIndividualTelephoneNumberPage.fillInTelephoneNumber("01234567890")
      RelevantIndividualTelephoneNumberPage.clickContinue()

      RelevantIndividualEmailAddressPage.assertPageIsDisplayed()
      RelevantIndividualEmailAddressPage.fillInEmailAddress("bobby.boucher@example.com")
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
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Email address", "bobby.boucher@example.com")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Do you know Bobby Boucher’s National Insurance number?", "Yes")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("National Insurance number", "AB123456C")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Do you know Bobby Boucher’s Self Assessment Unique Taxpayer Reference?", "Yes")
      RelevantIndividualCheckYourAnswersPage.assertSummaryRow("Self Assessment Unique Taxpayer Reference", "1234567890")
      RelevantIndividualCheckYourAnswersPage.clickContinue()

      CheckWhoProvidedDetailsPage.assertPageIsDisplayed()
      CheckWhoProvidedDetailsPage.clickContinue()

      TaskListPage.assertPageIsDisplayed()






      


