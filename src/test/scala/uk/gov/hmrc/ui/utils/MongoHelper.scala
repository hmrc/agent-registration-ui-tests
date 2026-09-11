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

package uk.gov.hmrc.ui.utils

import org.bson.BsonDocument
import org.mongodb.scala.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.model.Updates
import scala.concurrent.Await
import scala.concurrent.duration.*

object MongoHelper:

  private val client = MongoClient("mongodb://localhost:27017")
  private val backEndDatabase = client.getDatabase("agent-registration")
  private val backEndCollection = backEndDatabase.getCollection("agent-application")
  private val backEndIndividualCollection = backEndDatabase.getCollection("individual")

  private val agentAssuranceDatabase = client.getDatabase("agent-assurance")
  private val agentAssuranceCollection = agentAssuranceDatabase.getCollection("agent-assurance")

  final case class IndividualFix(
    fixType: String,
    isConfirmed: Boolean = false,
    // Fields for IndividualFix._10 (IndividualDetailsFix) only.
    // None = NotProvided, Some(value) = Provided
    dateOfBirth: Option[String] = None,
    nino: Option[String] = None,
    saUtr: Option[String] = None
  )

  final case class IndividualRiskingOutcome(
    outcomeType: String = "Approved",
    fixes: Seq[IndividualFix] = Seq.empty,
    providedByApplicant: Boolean = false,
    declarationAgreed: Boolean = false
  )

  final case class EntityFix(
    fixType: String,
    isConfirmed: Boolean = false
  )

  private def documentStringValue(
    document: Document,
    field: String
  ): Option[String] = document.get(field).flatMap { value =>
    if value.isString then Some(value.asString().getValue)
    else if value.isObjectId then Some(value.asObjectId().getValue.toHexString)
    else None
  }

  private def backEndApplicationIdForApplicationReference(applicationReference: String): String =
    val application = findBackEndApplicationByApplicationReference(applicationReference)
      .getOrElse(
        throw new AssertionError(
          s"No document found for applicationReference='$applicationReference' in agent-application collection"
        )
      )

    documentStringValue(application, "_id")
      .getOrElse(
        throw new AssertionError(
          s"No _id found for applicationReference='$applicationReference' in agent-application collection"
        )
      )

  private def backEndIndividualApplicationFilter(applicationReference: String) =
    val applicationId = backEndApplicationIdForApplicationReference(applicationReference)

    or(
      equal("applicationReference", applicationReference),
      equal("agentApplicationId", applicationId)
    )

  private def fixDocument(fix: IndividualFix): Document =
    if fix.fixType.contains("IndividualDetailsFix") || fix.fixType.contains("_10") then
      val dobDoc =
        fix.dateOfBirth match
          case Some(date) => Document("dateOfBirth" -> date, "type" -> "individual.IndividualDateOfBirth.Provided")
          case None => Document("type" -> "individual.IndividualDateOfBirth.NotProvided")

      val ninoDoc =
        fix.nino match
          case Some(n) => Document("nino" -> n, "type" -> "individual.IndividualNino.Provided")
          case None => Document("type" -> "individual.IndividualNino.NotProvided")

      val saUtrDoc =
        fix.saUtr match
          case Some(u) => Document("saUtr" -> u, "type" -> "individual.IndividualSaUtr.Provided")
          case None => Document("type" -> "individual.IndividualSaUtr.NotProvided")

      Document(
        "type" -> fix.fixType,
        "dateOfBirth" -> dobDoc,
        "nino" -> ninoDoc,
        "saUtr" -> saUtrDoc
      )
    else
      Document("type" -> fix.fixType, "isConfirmed" -> fix.isConfirmed)

  private def riskingOutcomeIndividualDocument(outcome: IndividualRiskingOutcome): Document =
    if outcome.fixes.isEmpty then
      Document("type" -> outcome.outcomeType, "declarationAgreed" -> outcome.declarationAgreed)
    else
      Document(
        "type" -> outcome.outcomeType,
        "fixes" -> outcome.fixes.map(fixDocument),
        "declarationAgreed" -> outcome.declarationAgreed
      )

  private def documentBooleanValue(
    document: Document,
    field: String
  ): Option[Boolean] = document.get(field).flatMap { value =>
    if value.isBoolean then Some(value.asBoolean().getValue)
    else None
  }

  private def documentDocumentValue(
    document: Document,
    field: String
  ): Option[Document] = document.get(field).flatMap { value =>
    if value.isDocument then Some(value.asDocument())
    else None
  }

  private def documentDocumentSeqValue(
    document: Document,
    field: String
  ): Seq[Document] = document.get(field)
    .flatMap { value =>
      if value.isArray then
        Some(
          value.asArray().getValues.toArray.toSeq.collect {
            case bsonValue: org.bson.BsonValue if bsonValue.isDocument => Document(bsonValue.asDocument().toJson())
          }
        )
      else None
    }
    .getOrElse(Seq.empty)

  private def individualFixFromDocument(fixDocument: Document): IndividualFix =
    val fixType = documentStringValue(fixDocument, "type")
      .getOrElse(throw new AssertionError(s"No fix type found in individual fix document: ${fixDocument.toJson()}"))

    if fixType.contains("IndividualDetailsFix") || fixType.contains("_10") then
      def nestedValue(
        nestedField: String,
        valueField: String
      ): Option[String] = documentDocumentValue(fixDocument, nestedField)
        .flatMap(documentStringValue(_, valueField))

      IndividualFix(
        fixType = fixType,
        dateOfBirth = nestedValue("dateOfBirth", "dateOfBirth"),
        nino = nestedValue("nino", "nino"),
        saUtr = nestedValue("saUtr", "saUtr")
      )
    else
      IndividualFix(
        fixType = fixType,
        isConfirmed = documentBooleanValue(fixDocument, "isConfirmed").getOrElse(false)
      )

  private def individualRiskingOutcomeFromBackEndIndividual(individual: Document): IndividualRiskingOutcome =
    val riskingOutcomeDocument = documentDocumentValue(individual, "riskingOutcomeIndividual")
      .getOrElse(
        throw new AssertionError(
          s"No riskingOutcomeIndividual found for backend individual: ${individual.toJson()}"
        )
      )

    IndividualRiskingOutcome(
      outcomeType = documentStringValue(riskingOutcomeDocument, "type").getOrElse("Approved"),
      fixes = documentDocumentSeqValue(riskingOutcomeDocument, "fixes").map(individualFixFromDocument),
      providedByApplicant = documentBooleanValue(individual, "providedByApplicant").getOrElse(false),
      declarationAgreed = documentBooleanValue(riskingOutcomeDocument, "declarationAgreed").getOrElse(false)
    )

  def getLinkIdByApplicationReference(applicationReference: String): String =
    val application = findBackEndApplicationByApplicationReference(applicationReference)
      .getOrElse(
        throw new AssertionError(
          s"No Mongo record found for reference: $applicationReference"
        )
      )

    application.get("linkId")
      .map(_.asString().getValue)
      .getOrElse(
        throw new AssertionError(
          s"No linkId found in Mongo record for reference: $applicationReference"
        )
      )

  def findBackEndApplicationByApplicationReference(ref: String): Option[Document] =
    val future = backEndCollection
      .find(equal("applicationReference", ref))
      .first()
      .toFutureOption()

    Await.result(future, 10.seconds)

  def generateRandomObjectId(): String = new org.bson.types.ObjectId().toHexString

  def insertAgentAssuranceRecord(
    oid: String,
    key: String,
    value: String
  ): String =
    val objId = new org.bson.types.ObjectId(oid)

    val deleteFuture = agentAssuranceCollection
      .deleteOne(equal("_id", objId))
      .toFuture()

    Await.result(deleteFuture, 10.seconds)

    val doc = Document(
      "_id" -> objId,
      "key" -> key,
      "value" -> value
    )

    val insertFuture = agentAssuranceCollection
      .insertOne(doc)
      .toFuture()

    Await.result(insertFuture, 10.seconds)

    value

  def findBackEndIndividualsByApplicationReference(ref: String): Seq[Document] =
    val future = backEndIndividualCollection
      .find(backEndIndividualApplicationFilter(ref))
      .toFuture()

    Await.result(future, 10.seconds)

  private def backEndIndividualFilter(
    applicationReference: String,
    individual: Document
  ) =
    documentStringValue(individual, "personReference") match
      case Some(personReference) => and(backEndIndividualApplicationFilter(applicationReference), equal("personReference", personReference))
      case None =>
        individual.get("_id") match
          case Some(oid) if oid.isObjectId => equal("_id", oid.asObjectId().getValue)
          case Some(id) if id.isString => equal("_id", id.asString().getValue)
          case _ =>
            documentStringValue(individual, "individualName") match
              case Some(individualName) => and(backEndIndividualApplicationFilter(applicationReference), equal("individualName", individualName))
              case None => backEndIndividualApplicationFilter(applicationReference)

  private def updateRiskingOutcomeForBackEndIndividual(
    applicationReference: String,
    individual: Document,
    outcome: IndividualRiskingOutcome
  ): Unit =
    val filter = backEndIndividualFilter(applicationReference, individual)

    val updateResult = Await.result(
      backEndIndividualCollection
        .updateOne(
          filter,
          Updates.combine(
            Updates.set("riskingOutcomeIndividual", riskingOutcomeIndividualDocument(outcome)),
            Updates.set("providedByApplicant", outcome.providedByApplicant)
          )
        )
        .toFuture(),
      10.seconds
    )

    assert(
      updateResult.getMatchedCount == 1,
      s"updateRiskingOutcomeForBackEndIndividual: no individual matched filter '$filter' for applicationReference='$applicationReference'"
    )

  def confirmRiskingOutcomeApplicantFixes(
    applicationReference: String,
    fixTypesToConfirm: Seq[String]
  ): Unit =
    require(fixTypesToConfirm.nonEmpty, "confirmRiskingOutcomeApplicantFixes: fixTypesToConfirm must not be empty")

    val application = findBackEndApplicationByApplicationReference(applicationReference)
      .getOrElse(
        throw new AssertionError(
          s"confirmRiskingOutcomeApplicantFixes: no backend application found for applicationReference='$applicationReference'"
        )
      )

    val entityFixes = documentDocumentValue(application, "riskingOutcomeEntity")
      .map(documentDocumentSeqValue(_, "fixes"))
      .getOrElse(
        throw new AssertionError(
          s"confirmRiskingOutcomeApplicantFixes: no riskingOutcomeEntity.fixes found for applicationReference='$applicationReference'"
        )
      )

    val existingFixTypes = entityFixes.flatMap(documentStringValue(_, "type")).toSet
    val missingFixTypes = fixTypesToConfirm.toSet.diff(existingFixTypes)

    assert(
      missingFixTypes.isEmpty,
      s"confirmRiskingOutcomeApplicantFixes: application '$applicationReference' does not contain fix type(s): ${missingFixTypes.mkString(", ")}"
    )

    val quotedFixTypes = fixTypesToConfirm.map(t => s"\"$t\"").mkString(",")
    val arrayFilter = BsonDocument.parse(s"{\"fix.type\": {\"$$in\": [$quotedFixTypes]}}")
    val updateOptions = new UpdateOptions().arrayFilters(java.util.List.of(arrayFilter))

    val updateResult = Await.result(
      backEndCollection
        .updateOne(
          equal("applicationReference", applicationReference),
          Updates.set("riskingOutcomeEntity.fixes.$[fix].isConfirmed", true),
          updateOptions
        )
        .toFuture(),
      10.seconds
    )

    assert(
      updateResult.getMatchedCount == 1,
      s"confirmRiskingOutcomeApplicantFixes: no backend application matched for applicationReference='$applicationReference'"
    )

  def confirmRiskingOutcomeIndividualFixes(
    applicationReference: String,
    fixTypesByIndividualName: Map[String, Seq[String]]
  ): Unit =
    val individuals = findBackEndIndividualsByApplicationReference(applicationReference)

    if individuals.isEmpty then
      throw new AssertionError(
        s"confirmRiskingOutcomeIndividualFixes: no backend individuals found for applicationReference='$applicationReference'"
      )

    val matchedIndividualNames = scala.collection.mutable.Set.empty[String]

    individuals.foreach { individual =>
      documentStringValue(individual, "individualName").foreach { individualName =>
        fixTypesByIndividualName.get(individualName).foreach { fixTypesToConfirm =>
          matchedIndividualNames += individualName

          val existingOutcome = individualRiskingOutcomeFromBackEndIndividual(individual)
          val existingFixTypes = existingOutcome.fixes.map(_.fixType).toSet
          val fixTypesToConfirmSet = fixTypesToConfirm.toSet
          val missingFixTypes = fixTypesToConfirmSet.diff(existingFixTypes)

          assert(
            missingFixTypes.isEmpty,
            s"confirmRiskingOutcomeIndividualFixes: individual '$individualName' does not contain fix type(s): ${missingFixTypes.mkString(", ")}"
          )

          val updatedFixes = existingOutcome.fixes.map { fix =>
            if fixTypesToConfirmSet.contains(fix.fixType) then fix.copy(isConfirmed = true)
            else fix
          }

          val allFixesConfirmed = updatedFixes.nonEmpty && updatedFixes.forall(_.isConfirmed)
          val declarationAgreed =
            if allFixesConfirmed then true
            else existingOutcome.declarationAgreed

          updateRiskingOutcomeForBackEndIndividual(
            applicationReference,
            individual,
            existingOutcome.copy(
              fixes = updatedFixes,
              declarationAgreed = declarationAgreed
            )
          )
        }
      }
    }

    val unmatchedIndividualNames = fixTypesByIndividualName.keySet.diff(matchedIndividualNames.toSet)

    assert(
      unmatchedIndividualNames.isEmpty,
      s"confirmRiskingOutcomeIndividualFixes: no backend individual matched individualName(s): ${unmatchedIndividualNames.mkString(", ")}"
    )

  def setProvidedByApplicantForIndividual(
    applicationReference: String,
    individualName: String,
    providedByApplicant: Boolean = true
  ): Unit =
    val individuals = findBackEndIndividualsByApplicationReference(applicationReference)

    if individuals.isEmpty then
      throw new AssertionError(
        s"setProvidedByApplicantForIndividual: no backend individuals found for applicationReference='$applicationReference'"
      )

    val matchingIndividuals = individuals.filter(ind => documentStringValue(ind, "individualName").contains(individualName))

    assert(
      matchingIndividuals.size == 1,
      s"setProvidedByApplicantForIndividual: expected exactly one backend individual named '$individualName' for applicationReference='$applicationReference', found ${matchingIndividuals.size}"
    )

    val individualToUpdate = matchingIndividuals.head
    val existingOutcome = individualRiskingOutcomeFromBackEndIndividual(individualToUpdate)

    updateRiskingOutcomeForBackEndIndividual(
      applicationReference,
      individualToUpdate,
      existingOutcome.copy(providedByApplicant = providedByApplicant)
    )
