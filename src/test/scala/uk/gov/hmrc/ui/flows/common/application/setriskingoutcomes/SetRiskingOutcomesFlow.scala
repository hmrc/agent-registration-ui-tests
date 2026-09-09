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

package uk.gov.hmrc.ui.flows.common.application.setriskingoutcomes

import scala.annotation.targetName
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.SelectEntityFailurePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.SelectIndividualFailurePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.fastforwardlinks.ShowAgentApplicationPage

object SetRiskingOutcomesFlow:

  sealed trait ApplicantOutcomeSelection
  case object ApplicantApproved
  extends ApplicantOutcomeSelection
  final case class ApplicantFailures(failureCodes: Seq[String])
  extends ApplicantOutcomeSelection

  sealed trait IndividualOutcomeSelection
  case object Approved
  extends IndividualOutcomeSelection
  final case class Failures(failureCodes: Seq[String])
  extends IndividualOutcomeSelection

  /** Sets risking outcomes by running through the full UI flow:
    *   1. Load the Application Details Page
    *   2. Run Risking
    *   3. Select Entity Failures
    *   4. Select Individual Failures
    *   5. Run results file processing
    *
    * @param applicationReference
    *   The application reference to set outcomes for
    * @param entityFailureCodes
    *   List of entity failure codes (e.g., "3.1", "4.1", "4.3", "8.7")
    * @param individualFailureCodes
    *   List of individual failure codes for a single-individual journey
    */
  def runFlow(
    applicationReference: String,
    entityFailureCodes: Seq[String],
    individualFailureCodes: Seq[String]
  ): Unit = runFlow(
    applicationReference = applicationReference,
    applicantOutcomeSelection = ApplicantFailures(entityFailureCodes),
    individualOutcomeSelections = Seq(Failures(individualFailureCodes))
  )

  /** Sets risking outcomes by running through the full UI flow:
    *   1. Load the Application Details Page
    *   2. Run Risking
    *   3. Select Entity Failures
    *   4. Select Individual Failures for each individual in order
    *   5. Run results file processing
    *
    * Use this overload when the application can contain multiple individuals and each individual may need a different set of failures.
    *
    * @param applicationReference
    *   The application reference to set outcomes for
    * @param entityFailureCodes
    *   List of entity failure codes (e.g., "3.1", "4.1", "4.3", "8.7")
    * @param individualFailureCodesByIndividual
    *   One sequence of failure codes per individual, in UI order
    */
  @targetName("runFlowByIndividual")
  def runFlow(
    applicationReference: String,
    entityFailureCodes: Seq[String],
    individualFailureCodesByIndividual: Seq[Seq[String]]
  ): Unit = runFlow(
    applicationReference = applicationReference,
    applicantOutcomeSelection = ApplicantFailures(entityFailureCodes),
    individualOutcomeSelections = individualFailureCodesByIndividual.map(Failures.apply)
  )

  /** Sets risking outcomes by running through the full UI flow:
    *   1. Load the Application Details Page
    *   2. Run Risking
    *   3. Select Applicant failures
    *   4. Select either failures or approval for each individual in order
    *   5. Run results file processing
    *
    * This overload allows the applicant to be expressed as failure codes while individuals can be a mix of `Approved` and `Failures(...)`.
    *
    * @param applicationReference
    *   The application reference to set outcomes for
    * @param entityFailureCodes
    *   Applicant/entity failure codes (e.g. "3.1", "4.1")
    * @param individualOutcomeSelections
    *   One outcome selection per individual, in UI order
    */
  @targetName("runFlowByApplicantFailuresAndIndividualOutcomes")
  def runFlow(
    applicationReference: String,
    entityFailureCodes: Seq[String],
    individualOutcomeSelections: IterableOnce[IndividualOutcomeSelection]
  ): Unit = runFlow(
    applicationReference = applicationReference,
    applicantOutcomeSelection = ApplicantFailures(entityFailureCodes),
    individualOutcomeSelections = individualOutcomeSelections
  )

  /** Sets risking outcomes using name-based selection for individuals.
    *
    * This avoids flaky ordering by targeting each individual's support-hub card by name.
    */
  @targetName("runFlowByApplicantFailuresAndIndividualOutcomesByName")
  def runFlow(
    applicationReference: String,
    entityFailureCodes: Seq[String],
    individualOutcomeSelectionsByName: Map[String, IndividualOutcomeSelection]
  ): Unit = runFlow(
    applicationReference = applicationReference,
    applicantOutcomeSelection = ApplicantFailures(entityFailureCodes),
    individualOutcomeSelectionsByName = individualOutcomeSelectionsByName
  )

  /** Sets risking outcomes by running through the full UI flow:
    *   1. Load the Application Details Page
    *   2. Run Risking
    *   3. Select Entity Failures
    *   4. Select either failures or approval for each individual in order
    *   5. Run results file processing
    *
    * Use this overload when the application can contain multiple individuals and each individual may need a different outcome.
    *
    * @param applicationReference
    *   The application reference to set outcomes for
    * @param applicantOutcomeSelection
    *   Applicant outcome selection, usually `ApplicantFailures(...)` for failure codes or `ApplicantApproved`
    * @param individualOutcomeSelections
    *   One outcome selection per individual, in UI order
    */
  @targetName("runFlowByOutcomeSelection")
  def runFlow(
    applicationReference: String,
    applicantOutcomeSelection: ApplicantOutcomeSelection,
    individualOutcomeSelections: IterableOnce[IndividualOutcomeSelection]
  ): Unit =
    val outcomes = individualOutcomeSelections.iterator.toSeq

    ShowAgentApplicationPage.openForApplicationReference(applicationReference)
    ShowAgentApplicationPage.assertPageIsDisplayed()
    ShowAgentApplicationPage.clickRunRiskingLink()

    selectApplicantOutcome(applicantOutcomeSelection)
    selectIndividualOutcomes(outcomes)

    ShowAgentApplicationPage.assertPageIsDisplayed()
    ShowAgentApplicationPage.clickRunResultsFileProcessingLink()
    ShowAgentApplicationPage.assertPageIsDisplayed()

  /** Sets risking outcomes using explicit applicant outcome and name-based individual outcomes. */
  @targetName("runFlowByOutcomeSelectionAndIndividualName")
  def runFlow(
    applicationReference: String,
    applicantOutcomeSelection: ApplicantOutcomeSelection,
    individualOutcomeSelectionsByName: Map[String, IndividualOutcomeSelection]
  ): Unit =
    ShowAgentApplicationPage.openForApplicationReference(applicationReference)
    ShowAgentApplicationPage.assertPageIsDisplayed()
    ShowAgentApplicationPage.clickRunRiskingLink()

    selectApplicantOutcome(applicantOutcomeSelection)
    selectIndividualOutcomesByName(individualOutcomeSelectionsByName)

    ShowAgentApplicationPage.assertPageIsDisplayed()
    ShowAgentApplicationPage.clickRunResultsFileProcessingLink()
    ShowAgentApplicationPage.assertPageIsDisplayed()

  // Private helper methods

  private def selectApplicantOutcome(applicantOutcomeSelection: ApplicantOutcomeSelection): Unit =
    applicantOutcomeSelection match
      case ApplicantApproved =>
        ShowAgentApplicationPage.clickApproveApplicantLink()
        ShowAgentApplicationPage.assertPageIsDisplayed()

      case ApplicantFailures(failureCodes) =>
        ShowAgentApplicationPage.clickChooseEntityFailuresLink()
        SelectEntityFailurePage.assertPageIsDisplayed()

        failureCodes.foreach { code =>
          SelectEntityFailurePage.selectFailureCode(code)
        }

        SelectEntityFailurePage.clickSubmitButton()

  private def selectIndividualOutcomes(outcomesByIndividual: Seq[IndividualOutcomeSelection]): Unit =
    if ShowAgentApplicationPage.hasChooseIndividualFailuresLinksByIndividual || ShowAgentApplicationPage.hasApproveIndividualLinksByIndividual then
      val individualActionControls = math.max(
        ShowAgentApplicationPage.numberOfChooseIndividualFailuresLinksByIndividual,
        ShowAgentApplicationPage.numberOfApproveIndividualLinksByIndividual
      )

      require(
        individualActionControls == outcomesByIndividual.size,
        s"Expected outcomes for $individualActionControls individuals but received ${outcomesByIndividual.size} individual outcome selections"
      )

      outcomesByIndividual.foreach {
        case Approved =>
          ShowAgentApplicationPage.clickFirstRemainingApproveIndividualLink()
          ShowAgentApplicationPage.assertPageIsDisplayed()

        case Failures(failureCodes) =>
          ShowAgentApplicationPage.clickFirstRemainingChooseIndividualFailuresLink()
          SelectIndividualFailurePage.assertPageIsDisplayed()

          failureCodes.foreach { code =>
            SelectIndividualFailurePage.selectFailureCode(code)
          }

          SelectIndividualFailurePage.clickSubmitButton()
          ShowAgentApplicationPage.assertPageIsDisplayed()
      }
    else
      require(
        outcomesByIndividual.size == 1,
        s"This journey exposed a single individual failure selection page, but ${outcomesByIndividual.size} individual outcome selections were provided"
      )

      outcomesByIndividual.head match
        case Approved =>
          ShowAgentApplicationPage.clickFirstRemainingApproveIndividualLink()
          ShowAgentApplicationPage.assertPageIsDisplayed()

        case Failures(failureCodes) =>
          ShowAgentApplicationPage.clickChooseIndividualFailuresLink()
          SelectIndividualFailurePage.assertPageIsDisplayed()

          failureCodes.foreach { code =>
            SelectIndividualFailurePage.selectFailureCode(code)
          }

          SelectIndividualFailurePage.clickSubmitButton()
          ShowAgentApplicationPage.assertPageIsDisplayed()

  private def selectIndividualOutcomesByName(
    outcomesByIndividualName: Map[String, IndividualOutcomeSelection]
  ): Unit =
    if ShowAgentApplicationPage.hasChooseIndividualFailuresLinksByIndividual || ShowAgentApplicationPage.hasApproveIndividualLinksByIndividual then
      val individualActionControls = math.max(
        ShowAgentApplicationPage.numberOfChooseIndividualFailuresLinksByIndividual,
        ShowAgentApplicationPage.numberOfApproveIndividualLinksByIndividual
      )

      require(
        individualActionControls == outcomesByIndividualName.size,
        s"Expected outcomes for $individualActionControls individuals but received ${outcomesByIndividualName.size} individual outcome selections"
      )

      outcomesByIndividualName.foreach { case (individualName, outcomeSelection) =>
        outcomeSelection match
          case Approved =>
            ShowAgentApplicationPage.clickApproveIndividualLinkForIndividualName(individualName)
            ShowAgentApplicationPage.assertPageIsDisplayed()

          case Failures(failureCodes) =>
            ShowAgentApplicationPage.clickChooseIndividualFailuresLinkForIndividualName(individualName)
            SelectIndividualFailurePage.assertPageIsDisplayed()

            failureCodes.foreach { code =>
              SelectIndividualFailurePage.selectFailureCode(code)
            }

            SelectIndividualFailurePage.clickSubmitButton()
            ShowAgentApplicationPage.assertPageIsDisplayed()
      }
    else
      require(
        outcomesByIndividualName.size == 1,
        s"This journey exposed a single individual failure selection page, but ${outcomesByIndividualName.size} named individual outcome selections were provided"
      )

      outcomesByIndividualName.values.head match
        case Approved =>
          ShowAgentApplicationPage.clickFirstRemainingApproveIndividualLink()
          ShowAgentApplicationPage.assertPageIsDisplayed()

        case Failures(failureCodes) =>
          ShowAgentApplicationPage.clickChooseIndividualFailuresLink()
          SelectIndividualFailurePage.assertPageIsDisplayed()

          failureCodes.foreach { code =>
            SelectIndividualFailurePage.selectFailureCode(code)
          }

          SelectIndividualFailurePage.clickSubmitButton()
          ShowAgentApplicationPage.assertPageIsDisplayed()
