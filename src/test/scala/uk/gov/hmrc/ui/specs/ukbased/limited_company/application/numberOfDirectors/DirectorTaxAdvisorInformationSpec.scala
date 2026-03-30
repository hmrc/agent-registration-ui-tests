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

package uk.gov.hmrc.ui.specs.ukbased.limited_company.application.numberOfDirectors

import uk.gov.hmrc.ui.domain.BusinessType.LimitedCompany
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.agentstandards.AgentStandardsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.limited_company.application.DirectorTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.CheckYourAnswersOtherIndividualsPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.CheckYourAnswersPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.ChangeDirectorPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.ChangeOtherRelevantIndividualPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.CheckThisListOfDirectorsPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.RemoveCompaniesHouseOfficePage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.TellUsAboutTheDirectorPage
import uk.gov.hmrc.ui.pages.agentregistration.ukbased.limited_company.businessdetails.YouHaveAddedDirectorCheckYourAnswerPage
import uk.gov.hmrc.ui.specs.BaseSpec

class DirectorTaxAdvisorInformationSpec
extends BaseSpec:

  Feature("Complete Director and Other Tax Adviser information section"):
    Scenario(
      "Director has 5 or less directors",
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

    Scenario(
      "Company has 6 or more directors (some relevant tax advisors, happy path)",
      TagLimitedCompany
    ):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LimitedCompany)

      DirectorTaxAdvisorInformationFlow
        .SixOrMoreDirectors
        .runFlow()

    Scenario(
      "Change number of directors from Check your answers screen",
      TagLimitedCompany
    ):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LimitedCompany)

      DirectorTaxAdvisorInformationFlow
        .RunToCheckYourAnswersToChangeDirectors
        .runFlow()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.clickChangeNumberOfDirectors()

      CheckThisListOfDirectorsPage.assertPageIsDisplayed()
      CheckThisListOfDirectorsPage.enterExactNumberOfDirectors("6")
      CheckThisListOfDirectorsPage.clickContinue()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.assertWarningTextIsDisplayed("You need to tell us about 1 more director that deals with tax.")
      YouHaveAddedDirectorCheckYourAnswerPage.clickContinue()

      TellUsAboutTheDirectorPage.assertPageIsDisplayed()
      TellUsAboutTheDirectorPage.enterDirectorFirstName("Justine")
      TellUsAboutTheDirectorPage.enterDirectorLastName("Hills")
      TellUsAboutTheDirectorPage.clickContinue()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.assertNameAt(5, "Justine Hills")

    Scenario(
      "Change director name from Check your answers screen",
      TagLimitedCompany
    ):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LimitedCompany)

      DirectorTaxAdvisorInformationFlow
        .RunToCheckYourAnswersToChangeDirectors
        .runFlow()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.changeDirectorName("Steve Austin")

      ChangeDirectorPage.assertPageIsDisplayed()
      ChangeDirectorPage.enterDirectorFirstName("Justine")
      ChangeDirectorPage.enterDirectorLastName("Hills")
      ChangeDirectorPage.clickContinue()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.assertNameAt(0, "Justine Hills")

    Scenario(
      "Change other relevant tax advisers from Check Your Answers screen",
      TagLimitedCompany
    ):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LimitedCompany)

      DirectorTaxAdvisorInformationFlow
        .RunToCheckYourAnswersOtherIndividuals
        .runFlow()

      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.changeOtherRelevantTaxAdviserName("Bruce Wayne")

      ChangeOtherRelevantIndividualPage.assertPageIsDisplayed()
      ChangeOtherRelevantIndividualPage.enterRelevantIndividualFullName("Dick Grayson")
      ChangeOtherRelevantIndividualPage.clickContinue()

      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.assertNameAt(0, "Dick Grayson")

    Scenario(
      "Change number of directors from final Check Your Answers",
      TagLimitedCompany
    ):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LimitedCompany)

      DirectorTaxAdvisorInformationFlow
        .RunToFinalCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Number of directors", "5")
      CheckYourAnswersPage.changeNumberOfDirectors()

      CheckThisListOfDirectorsPage.assertPageIsDisplayed()
      CheckThisListOfDirectorsPage.enterExactNumberOfDirectors("6")
      CheckThisListOfDirectorsPage.clickContinue()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.assertWarningTextIsDisplayed("You need to tell us about 1 more director that deals with tax.")
      YouHaveAddedDirectorCheckYourAnswerPage.clickContinue()

      TellUsAboutTheDirectorPage.assertPageIsDisplayed()
      TellUsAboutTheDirectorPage.enterDirectorFirstName("Justine")
      TellUsAboutTheDirectorPage.enterDirectorLastName("Hills")
      TellUsAboutTheDirectorPage.clickContinue()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.assertNameAt(5, "Justine Hills")
      YouHaveAddedDirectorCheckYourAnswerPage.removeDirector("Steve Austin")

      RemoveCompaniesHouseOfficePage.assertPageIsDisplayed()
      RemoveCompaniesHouseOfficePage.selectYes()
      RemoveCompaniesHouseOfficePage.clickContinue()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.assertWarningTextIsDisplayed("You need to tell us about 1 more director that deals with tax.")
      YouHaveAddedDirectorCheckYourAnswerPage.clickChangeNumberOfDirectors()

      CheckThisListOfDirectorsPage.assertPageIsDisplayed()
      CheckThisListOfDirectorsPage.enterExactNumberOfDirectors("5")
      CheckThisListOfDirectorsPage.clickContinue()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.assertNameAt(4, "Justine Hills")
      YouHaveAddedDirectorCheckYourAnswerPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Number of directors", "5")

    Scenario(
      "Change director name from final Check Your Answers",
      TagLimitedCompany
    ):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LimitedCompany)

      DirectorTaxAdvisorInformationFlow
        .RunToFinalCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.changeDirectorNames()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.changeDirectorName("Steve Austin")

      ChangeDirectorPage.assertPageIsDisplayed()
      ChangeDirectorPage.enterDirectorFirstName("Justine")
      ChangeDirectorPage.enterDirectorLastName("Hills")
      ChangeDirectorPage.clickContinue()

      YouHaveAddedDirectorCheckYourAnswerPage.assertPageIsDisplayed()
      YouHaveAddedDirectorCheckYourAnswerPage.assertNameAt(0, "Justine Hills")
      YouHaveAddedDirectorCheckYourAnswerPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Director names", "Justine Hills\nBeverly Hills\nPauline Austin\nSteve Palmer\nSandra Hills")

    Scenario(
      "Change other relevant tax adviser name from final Check Your Answers",
      TagLimitedCompany
    ):
      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(LimitedCompany)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      AgentStandardsFlow
        .AgreeToMeetStandards
        .runFlow(LimitedCompany)

      DirectorTaxAdvisorInformationFlow
        .RunToFinalCheckYourAnswers
        .runFlow()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.changeRelevantTaxAdviserNames()

      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.changeOtherRelevantTaxAdviserName("Bruce Wayne")

      ChangeOtherRelevantIndividualPage.assertPageIsDisplayed()
      ChangeOtherRelevantIndividualPage.enterRelevantIndividualFullName("Dick Grayson")
      ChangeOtherRelevantIndividualPage.clickContinue()

      CheckYourAnswersOtherIndividualsPage.assertPageIsDisplayed()
      CheckYourAnswersOtherIndividualsPage.assertNameAt(0, "Dick Grayson")
      CheckYourAnswersOtherIndividualsPage.selectNo()
      CheckYourAnswersOtherIndividualsPage.clickContinue()

      CheckYourAnswersPage.assertPageIsDisplayed()
      CheckYourAnswersPage.assertSummaryRow("Other relevant tax adviser names", "Dick Grayson\nClark Kent")
