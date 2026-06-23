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

import uk.gov.hmrc.ui.domain.BusinessType.GeneralPartnership
import uk.gov.hmrc.ui.domain.BusinessType.SoleTrader
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.partnerInformation.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow.listProgress
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper

class FailedFixableOutcomeForApplicantSpec
extends BaseSpec:

  Feature("Applicant FailedFixable List Page"):
    Scenario(
      "Sole Trader Owner sees FailedFixable Outcome Page after sign in",
      TagFixableFailures
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, SoleTrader)

      ProvideIndividualDetailsFlow
        .ProvideIndividualDetailsSoleTrader
        .runFlow(
          stubbedSignInData,
          listProgress.complete,
          fastForwardUsed = true
        )

      DeclarationFlow.AcceptDeclaration.runFlow(SoleTrader, fastForwardUsed = true)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      // Insert risking outcome data into the backend agent-application collection
      MongoHelper.insertRiskingOutcomeToAgentApplication(
        applicationReference = applicationReference,
        riskingCompletedDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = Seq("EntityFix._4._2")
      )
      
      MongoHelper.syncRiskingIndividualsToBackEnd(applicationReference)

      val riskingIndividuals = MongoHelper.findRiskingIndividualsByApplicationReference(applicationReference)
      riskingIndividuals should not be empty

      // For each individual in the risking database, update the backend individual collection
      // by trying to match on various identifier fields
      riskingIndividuals.foreach { riskingIndividual =>
        riskingIndividual.get("id") match
          case Some(v) if v.isString =>
            val id = v.asString().getValue
            MongoHelper.insertRiskingOutcomeIndividualByField(
              applicationReference,
              "id",
              id
            )
          case _ =>
            riskingIndividual.get("individualReference") match
              case Some(v2) if v2.isString =>
                val ref = v2.asString().getValue
                MongoHelper.insertRiskingOutcomeIndividualByField(
                  applicationReference,
                  "individualReference",
                  ref
                )
              case _ =>
                riskingIndividual.get("_id") match
                  case Some(oid) if oid.isObjectId =>
                    val hex = oid.asObjectId().getValue.toHexString
                    MongoHelper.insertRiskingOutcomeIndividualByObjectId(applicationReference, hex)
                  case _ =>
                    // Fallback: update all individuals for the application
                    MongoHelper.insertRiskingOutcomeIndividualToBackEnd(applicationReference)
      }

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("ST Name ST Lastname")

      val outcomeDoc = MongoHelper
        .findBackEndApplicationByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      // Verify riskingOutcomeApplication data
      MongoHelper.getTopLevelString(outcomeDoc, "applicationState") shouldBe "RiskingCompleted"
      val riskingOutcomeApp = outcomeDoc.get("riskingOutcomeApplication").get.asDocument()
      riskingOutcomeApp.getString("outcome").getValue shouldBe "FailedFixable"
      riskingOutcomeApp.getString("riskingCompletedDate").getValue shouldBe "2026-06-18"
      riskingOutcomeApp.getString("correctiveActionExpiryDate").getValue shouldBe "2026-08-17"

      // Verify riskingOutcomeEntity data
      val riskingOutcomeEntity = outcomeDoc.get("riskingOutcomeEntity").get.asDocument()
      riskingOutcomeEntity.getString("type").getValue shouldBe "FailedFixable"
      val fixesArray = riskingOutcomeEntity.getArray("fixes")
      val fixesValues = fixesArray.getValues.toArray
      fixesValues.length shouldBe 1
      fixesValues(0).asInstanceOf[org.bson.BsonDocument].getString("type").getValue shouldBe "EntityFix._4._2"

      ApplicationSubmittedPage.assertOutcomeDescriptionContainsAll(
        "ST Name ST Lastname will not be given an agent services account on this occasion",
        "the application will be deleted on 17 August 2026 to comply with our data retention policy"
      )

    Scenario(
      "General Partnership sees FailedFixable Outcome Page when partner have individual failures",
      TagSmokeTests
    ):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, GeneralPartnership)

      PartnerTaxAdvisorInformationFlow
        .singlePartner
        .runFlow()

      ProvideIndividualDetailsFlow
        .ProvideIndividualDetails
        .runFlow(
          stubbedSignInData,
          complete,
          GeneralPartnership
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(GeneralPartnership, fastForwardUsed = true)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      // Insert risking outcome data into the backend agent-application collection
      MongoHelper.insertRiskingOutcomeToAgentApplication(
        applicationReference = applicationReference,
        riskingCompletedDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = Seq("EntityFix._4._1", "EntityFix._4._3")
      )
      
      MongoHelper.syncRiskingIndividualsToBackEnd(applicationReference)

      val riskingIndividuals = MongoHelper.findRiskingIndividualsByApplicationReference(applicationReference)
      riskingIndividuals should not be empty

      // For each individual in the risking database, update the backend individual collection
      // by trying to match on various identifier fields
      riskingIndividuals.foreach { riskingIndividual =>
        riskingIndividual.get("id") match
          case Some(v) if v.isString =>
            val id = v.asString().getValue
            MongoHelper.insertRiskingOutcomeIndividualByField(
              applicationReference,
              "id",
              id
            )
          case _ =>
            riskingIndividual.get("individualReference") match
              case Some(v2) if v2.isString =>
                val ref = v2.asString().getValue
                MongoHelper.insertRiskingOutcomeIndividualByField(
                  applicationReference,
                  "individualReference",
                  ref
                )
              case _ =>
                riskingIndividual.get("_id") match
                  case Some(oid) if oid.isObjectId =>
                    val hex = oid.asObjectId().getValue.toHexString
                    MongoHelper.insertRiskingOutcomeIndividualByObjectId(applicationReference, hex)
                  case _ =>
                    // Fallback: update all individuals for the application
                    MongoHelper.insertRiskingOutcomeIndividualToBackEnd(applicationReference)
      }

      RiskingOutcomeFlow
        .SignInAsApplicantAfterRiskingOutcome
        .runFlow(stubbedSignInData)

      ApplicationSubmittedPage.assertPageIsDisplayed()
      ApplicationSubmittedPage.assertPageHeadingContains("Electronicsson Group")

      val outcomeDoc = MongoHelper
        .findBackEndApplicationByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      // Verify riskingOutcomeApplication data
      MongoHelper.getTopLevelString(outcomeDoc, "applicationState") shouldBe "RiskingCompleted"
      val riskingOutcomeApp = outcomeDoc.get("riskingOutcomeApplication").get.asDocument()
      riskingOutcomeApp.getString("outcome").getValue shouldBe "FailedFixable"

      // Verify riskingOutcomeEntity data
      val riskingOutcomeEntity = outcomeDoc.get("riskingOutcomeEntity").get.asDocument()
      riskingOutcomeEntity.getString("type").getValue shouldBe "FailedFixable"
      val fixesArr = riskingOutcomeEntity.getArray("fixes").getValues.toArray
      fixesArr.length shouldBe 2
      val fixTypes = fixesArr.map(_.asInstanceOf[org.bson.BsonDocument].getString("type").getValue).toSeq
      fixTypes should contain allOf ("EntityFix._4._1", "EntityFix._4._3")

      ApplicationSubmittedPage.assertOutcomeDescriptionContainsAll(
        "Electronicsson Group will not be given an agent services account on this occasion",
        "the application will be deleted on 17 August 2026 to comply with our data retention policy"
      )
