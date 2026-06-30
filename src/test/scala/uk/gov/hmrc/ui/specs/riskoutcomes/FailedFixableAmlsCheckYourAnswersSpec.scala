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

package uk.gov.hmrc.ui.specs.riskoutcomes


import org.mongodb.scala.Document
import uk.gov.hmrc.ui.domain.BusinessType.SoleTrader
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.pages.PageObject.getCurrentUrl
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.failedfixable.{ConditionsNotYetMetAmlsCheckYourAnswersPage, ConditionsNotYetMetAmlsEntityFailureV31Page, ConditionsNotYetMetAmlsEvidencePage, ConditionsNotYetMetAmlsEvidenceUploadResultPage, ConditionsNotYetMetAmlsRegistrationNumberPage, ConditionsNotYetMetAmlsSupervisorNamePage, ConditionsNotYetMetTaskListPage}
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper
import uk.gov.hmrc.ui.utils.Tags.TagFixableFailures

class FailedFixableAmlsCheckYourAnswersSpec
extends BaseSpec:

 Feature("Check and confirm AMLS details after AMLS failure"):
   Scenario(
     "SoleTraderOwner reviews and confirms AMLS details with reason code 3.1 successfully",
     TagFixableFailures
   ):

     val stubbedSignInData = FastForwardLinks
       .FastForward
       .runFlow(Declaration, SoleTrader)

     ApplicationSubmittedPage.assertPageIsDisplayed()

     val applicationReference = ApplicationSubmittedPage.getApplicationReference
     MongoHelper
       .findByApplicationReference(applicationReference)
       .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

     val amlsFixes = Seq(
       Document(
         "failure" -> Document("type" -> "_3._1"),
         "amlsDetails" -> Document(
           "supervisoryBody" -> "HMRC",
           "amlsRegistrationNumber" -> "XAML00000123456"
         ),
         "type" -> "EntityFix._3.AmlsFix"
       )
     )

     val entityRiskingFailures = Seq(
       Document("type" -> "_3._1")
     )

     // Insert risking outcome data with AMLS details into both backend and risking databases
     MongoHelper.insertRiskingOutcomeToAgentApplicationWithAmlsDetails(
       applicationReference = applicationReference,
       riskingCompletedDate = "2026-06-18",
       outcome = "FailedFixable",
       correctiveActionExpiryDate = "2026-08-17",
       fixes = amlsFixes,
     )

     val riskingIndividuals = MongoHelper.findRiskingIndividualsByApplicationReference(applicationReference)
     riskingIndividuals should not be empty

      MongoHelper.insertRiskingOutcomeIndividualByAgentApplicationId(
        applicationReference,
        riskingOutcomeType = "Approved"
      )
     
     RiskingOutcomeFlow
       .SignInAsApplicantAfterRiskingOutcome
       .runFlow(stubbedSignInData)

     ApplicationSubmittedPage.assertPageIsDisplayed()

     ApplicationSubmittedPage.clickViewActionLink()
     ConditionsNotYetMetTaskListPage.assertPageIsDisplayed()
     ConditionsNotYetMetTaskListPage.clickOnProvideYourSupervisionDetailsLink()

     ConditionsNotYetMetAmlsEntityFailureV31Page.assertPageIsDisplayed()
     ConditionsNotYetMetAmlsEntityFailureV31Page.clickContinue()

     ConditionsNotYetMetAmlsCheckYourAnswersPage.assertPageIsDisplayed()
     ConditionsNotYetMetAmlsCheckYourAnswersPage.assertSummaryRowPresent("Supervisory body")
     ConditionsNotYetMetAmlsCheckYourAnswersPage.assertSummaryRowPresent("Registration number")

     ConditionsNotYetMetAmlsCheckYourAnswersPage.clickChangeFor("Supervisory body")
     ConditionsNotYetMetAmlsSupervisorNamePage.assertPageIsDisplayed()
     ConditionsNotYetMetAmlsSupervisorNamePage.enterSupervisor("Association of Chartered Certified Accountants (ACCA)")
     ConditionsNotYetMetAmlsSupervisorNamePage.clickContinue()

     ConditionsNotYetMetAmlsRegistrationNumberPage.assertPageIsDisplayed()
     ConditionsNotYetMetAmlsRegistrationNumberPage.enterNonHMRCRegistrationNumber()
     ConditionsNotYetMetAmlsRegistrationNumberPage.clickContinue()

     ConditionsNotYetMetAmlsEvidencePage.assertPageIsDisplayed()
     ConditionsNotYetMetAmlsEvidencePage.uploadFileFromResources("Aml-Evidence.docx")
     ConditionsNotYetMetAmlsEvidencePage.clickContinue()

     ConditionsNotYetMetAmlsEvidenceUploadResultPage.assertPageIsDisplayed()
     ConditionsNotYetMetAmlsEvidenceUploadResultPage.clickContinue()

     ConditionsNotYetMetAmlsCheckYourAnswersPage.clickContinue()

     ConditionsNotYetMetTaskListPage.assertPageIsDisplayed()
     ConditionsNotYetMetTaskListPage.assertAmlsDetailsLinkText("Provide your supervision details again")
     ConditionsNotYetMetTaskListPage.assertAmlsDetailsStatus("Completed")

   

