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

package uk.gov.hmrc.ui.specs.riskoutcomes

import org.mongodb.scala.Document
import uk.gov.hmrc.ui.domain.BusinessType.LLP
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.Declaration
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes.{ApplicationStatusPage, ConditionsNotYetMetApplicantTaskListPage, ConditionsNotYetMetEntityFailureDetailsV41Page, SaveAndComeBackLaterPage}
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper
import uk.gov.hmrc.ui.utils.MongoHelper.{IndividualFix, IndividualRiskingOutcome}

class FailedFixableOutcomeForApplicantFailureTasklistSpec
extends BaseSpec:

  Feature("Applicant Task List Page"):
    Scenario(
      "Applicant actions are incomplete on task list",
      TagFixableFailures
    ):
      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

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
        ),
        Document(
          "type" -> "EntityFix._4._1"
        ),
        Document(
          "type" -> "EntityFix._4._3"
        )
      )

      MongoHelper.insertRiskingOutcomeToAgentApplicationWithAmlsDetails(
        applicationReference = applicationReference,
        actualDecisionDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = amlsFixes
      )

      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Steve Austin" -> IndividualRiskingOutcome(
            fixes = Seq.empty
          ),
          "Beverly Hills" -> IndividualRiskingOutcome(
            fixes = Seq.empty
          )
        )
      )

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      // Click the "View actions to take" button to navigate to the Conditions Not Met Task List page
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Partnership has not met the registration conditions")

      // verify actions and their status
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "VAT - missing returns",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Declare and submit",
        "Cannot start yet"
      )

      // Click the "Save and Come Back Later" button to navigate to the Save and Come Back Later page
      ConditionsNotYetMetApplicantTaskListPage.clickSaveAndComeBackLaterButton()
      SaveAndComeBackLaterPage.assertPageIsDisplayed()

    Scenario(
      "Applicant actions are complete, but individual action is incomplete on task list",
      TagFixableFailures
    ):
      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

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
          "type" -> "EntityFix._3.AmlsFix", "isConfirmed" -> true
        ),
        Document(
          "type" -> "EntityFix._4._1", "isConfirmed" -> true
        ),
        Document(
          "type" -> "EntityFix._4._3", "isConfirmed" -> true
        )
      )

      MongoHelper.insertRiskingOutcomeToAgentApplicationWithAmlsDetails(
        applicationReference = applicationReference,
        actualDecisionDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = amlsFixes
      )

      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Steve Austin" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3"),
              IndividualFix("IndividualFix._8._7", isConfirmed = true)
            )
          ),
          "Beverly Hills" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._1", isConfirmed = true),
              IndividualFix("IndividualFix._5._1", isConfirmed = true)
            ),
            declarationAgreed = true
          )
        )
      )

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      // Click the "View actions to take" button to navigate to the Conditions Not Met Task List page
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Partnership has not met the registration conditions")

      // verify actions and their status
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "VAT - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "We are awaiting information from these people",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Declare and submit",
        "Cannot start yet"
      )

    Scenario(
      "Applicant action is incomplete, but individual action is complete on task list",
      TagFixableFailures
    ):
      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

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
          "type" -> "EntityFix._3.AmlsFix", "isConfirmed" -> true
        ),
        Document(
          "type" -> "EntityFix._4._1", "isConfirmed" -> false
        ),
        Document(
          "type" -> "EntityFix._4._3", "isConfirmed" -> true
        )
      )

      MongoHelper.insertRiskingOutcomeToAgentApplicationWithAmlsDetails(
        applicationReference = applicationReference,
        actualDecisionDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = amlsFixes
      )

      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Steve Austin" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3", isConfirmed = true),
              IndividualFix("IndividualFix._8._7", isConfirmed = true)
            ),
            declarationAgreed = true
          ),
          "Beverly Hills" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._1", isConfirmed = true),
              IndividualFix("IndividualFix._5._1", isConfirmed = true)
            ),
            declarationAgreed = true
          )
        )
      )

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      // Click the "View actions to take" button to navigate to the Conditions Not Met Task List page
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Partnership has not met the registration conditions")

      // verify actions and their status
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Incomplete"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "VAT - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "We are awaiting information from these people",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Declare and submit",
        "Cannot start yet"
      )

    Scenario(
      "Both applicant and individual actions are complete on task list",
      TagFixableFailures
    ):
      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(Declaration, LLP)

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
          "type" -> "EntityFix._3.AmlsFix", "isConfirmed" -> true
        ),
        Document(
          "type" -> "EntityFix._4._1", "isConfirmed" -> true
        ),
        Document(
          "type" -> "EntityFix._4._3", "isConfirmed" -> true
        )
      )

      MongoHelper.insertRiskingOutcomeToAgentApplicationWithAmlsDetails(
        applicationReference = applicationReference,
        actualDecisionDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = amlsFixes
      )

      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Steve Austin" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3", isConfirmed = true),
              IndividualFix("IndividualFix._8._7", isConfirmed = true)
            ),
            declarationAgreed = true
          ),
          "Beverly Hills" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._1", isConfirmed = true),
              IndividualFix("IndividualFix._5._1", isConfirmed = true)
            ),
            declarationAgreed = true
          )
        )
      )

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertConfirmationTitleHeading("Test Partnership does not meet the registration conditions yet")

      // Click the "View actions to take" button to navigate to the Conditions Not Met Task List page
      ApplicationStatusPage.clickViewActionsToTakeButton()
      ConditionsNotYetMetApplicantTaskListPage.assertPageIsDisplayed()
      ConditionsNotYetMetApplicantTaskListPage.assertTaskListTitleHeading("Take action: Test Partnership has not met the registration conditions")

      // verify actions and their status
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Provide your supervision details again",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Self Assessment - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "VAT - missing returns",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "We are awaiting information from these people",
        "Completed"
      )
      ConditionsNotYetMetApplicantTaskListPage.assertActionStatus(
        "Declare and submit",
        "Incomplete"
      )
