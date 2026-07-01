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
import org.mongodb.scala.model.ReplaceOptions
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

  private val database = client.getDatabase("agent-registration-risking")
  private val collection = database.getCollection("application-for-risking")
  private val individualsCollection = database.getCollection("individual-for-risking")

  final case class IndividualFix(
    fixType: String,
    isConfirmed: Boolean = false
  )

  final case class IndividualRiskingOutcome(
    outcomeType: String = "Approved",
    fixes: Seq[IndividualFix] = Seq.empty
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

  private def riskingOutcomeIndividualDocument(outcome: IndividualRiskingOutcome): Document =
    if outcome.fixes.isEmpty then Document("type" -> outcome.outcomeType)
    else
      Document(
        "type" -> outcome.outcomeType,
        "fixes" -> outcome.fixes.map(fix => Document("type" -> fix.fixType, "isConfirmed" -> fix.isConfirmed))
      )

  private def riskingOutcomeIndividualDocument(
    riskingOutcomeType: String,
    fixes: Seq[IndividualFix]
  ): Document = riskingOutcomeIndividualDocument(IndividualRiskingOutcome(riskingOutcomeType, fixes))

  def findByApplicationReference(ref: String): Option[Document] =
    val future = collection
      .find(equal("applicationReference", ref))
      .first()
      .toFutureOption()

    Await.result(future, 10.seconds)

  def findBackEndApplicationByApplicationReference(ref: String): Option[Document] =
    val future = backEndCollection
      .find(equal("applicationReference", ref))
      .first()
      .toFutureOption()

    Await.result(future, 10.seconds)

  def getTopLevelString(
    doc: Document,
    field: String
  ): String = doc.get(field)
    .map(_.asString().getValue)
    .getOrElse(throw new AssertionError(s"Field '$field' not found"))

  def getNestedString(
    doc: BsonDocument,
    field: String
  ): String = Option(doc.get(field))
    .map(_.asString().getValue)
    .getOrElse(throw new AssertionError(s"Field '$field' not found"))

  def getNestedInt(
    doc: BsonDocument,
    field: String
  ): Int = Option(doc.get(field))
    .map(_.asInt32().getValue)
    .getOrElse(throw new AssertionError(s"Field '$field' not found"))

  def getEntityRiskingFailures(doc: Document): Seq[BsonDocument] = doc.get("entityRiskingResult")
    .map(
      _.asDocument()
        .get("failures")
        .asArray()
        .getValues
        .toArray
        .toSeq
        .map(_.asInstanceOf[org.bson.BsonValue].asDocument())
    )
    .getOrElse(throw new AssertionError("Field 'entityRiskingResult' not found"))

  def findIndividualsByApplicationReference(ref: String): Seq[Document] =
    val future = individualsCollection
      .find(equal("applicationReference", ref))
      .toFuture()

    Await.result(future, 10.seconds)

  def simulateNonFixableRiskingOutcome(
    applicationReference: String,
    withEntityFailures: Boolean = true,
    withIndividualFailures: Boolean = false
  ): Unit =
    findByApplicationReference(applicationReference)
      .getOrElse(throw new AssertionError(s"No document found for applicationReference='$applicationReference'"))

    val now = java.time.Instant.now().toString

    val entityRiskingResult = BsonDocument.parse(
      s"""{"failures":[
         |  {"type":"_7"},
         |  {"type":"_3._1"},
         |  {"type":"_4._1"},
         |  {"type":"_5._1"},
         |  {"type":"_8._1"},
         |  {"type":"_8._4"},
         |  {"type":"_8._5"},
         |  {"type":"_8._6"},
         |  {"type":"_8._7"}
         |],"receivedAt":"$now"}""".stripMargin
    )

    val individualRiskingResult =
      if withIndividualFailures then
        BsonDocument.parse(
          s"""{"failures":[
             |  {"type":"_4._1"},
             |  {"type":"_5._1"},
             |  {"type":"_6"},
             |  {"type":"_7"},
             |  {"type":"_8._1"},
             |  {"type":"_8._6"},
             |  {"type":"_8._7"},
             |  {"type":"_9"}
             |],"receivedAt":"$now"}""".stripMargin
        )
      else
        BsonDocument.parse(s"""{"failures":[],"receivedAt":"$now"}""")

    val update1 = Updates.combine(
      Updates.set("riskingFileName", "any-old.txt"),
      Updates.set("entityRiskingResult", entityRiskingResult),
      Updates.set("overallStatus", Document("riskingOutcome" -> "FailedNonFixable", "emailsProcessed" -> true))
    )

    val appFuture = collection
      .updateOne(equal("applicationReference", applicationReference), update1)
      .toFuture()

    val appResult = Await.result(appFuture, 10.seconds)

    assert(
      appResult.getMatchedCount == 1,
      s"simulateNonFixableRiskingOutcome: no application matched for '$applicationReference'"
    )

    val indFuture = individualsCollection
      .updateMany(
        equal("applicationReference", applicationReference),
        Updates.set("individualRiskingResult", individualRiskingResult)
      )
      .toFuture()

    Await.result(indFuture, 10.seconds)

  def insertEntityRiskingResult(
    applicationReference: String,
    entityRiskingResultJson: String,
    overallOutcome: String = "FailedFixable"
  ): Unit =
    findByApplicationReference(applicationReference)
      .getOrElse(throw new AssertionError(s"No document found for applicationReference='$applicationReference'"))

    val entityRiskingResult = BsonDocument.parse(entityRiskingResultJson)

    val update2 = Updates.combine(
      Updates.set("riskingFileName", "any-old.txt"),
      Updates.set("entityRiskingResult", entityRiskingResult),
      Updates.set("overallStatus", Document("riskingOutcome" -> overallOutcome, "emailsProcessed" -> true))
    )

    val appFuture = collection
      .updateOne(equal("applicationReference", applicationReference), update2)
      .toFuture()

    val appResult = Await.result(appFuture, 10.seconds)

    assert(
      appResult.getMatchedCount == 1,
      s"insertEntityRiskingResult: no application matched for '$applicationReference'"
    )

  def getIndividualRiskingFailures(doc: Document): Seq[BsonDocument] = doc.get("individualRiskingResult")
    .map(
      _.asDocument()
        .get("failures")
        .asArray()
        .getValues
        .toArray
        .toSeq
        .map(_.asInstanceOf[org.bson.BsonValue].asDocument())
    )
    .getOrElse(throw new AssertionError("Field 'individualRiskingResult' not found"))

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

  def insertRiskingOutcomeToAgentApplication(
    applicationReference: String,
    riskingCompletedDate: String,
    outcome: String,
    correctiveActionExpiryDate: String,
    fixes: Seq[String] = Seq("EntityFix._4._2"),
    riskingOutcomeEntityType: String = "Approved"
  ): Unit =
    findBackEndApplicationByApplicationReference(applicationReference)
      .getOrElse(
        throw new AssertionError(
          s"No document found for applicationReference='$applicationReference' in agent-application collection"
        )
      )

    val fixesArray = fixes.map(fix => Document("type" -> fix))

    val riskingOutcomeEntity =
      if fixesArray.isEmpty then Document("type" -> riskingOutcomeEntityType)
      else
        Document(
          "fixes" -> fixesArray,
          "type" -> riskingOutcomeEntityType
        )

    val riskingOutcomeApplication = Document(
      "riskingCompletedDate" -> riskingCompletedDate,
      "outcome" -> outcome,
      "correctiveActionExpiryDate" -> correctiveActionExpiryDate
    )

    val update = Updates.combine(
      Updates.set("applicationState", "RiskingCompleted"),
      Updates.set("riskingOutcomeApplication", riskingOutcomeApplication),
      Updates.set("riskingOutcomeEntity", riskingOutcomeEntity)
    )

    val updateFuture = backEndCollection
      .updateOne(equal("applicationReference", applicationReference), update)
      .toFuture()

    val updateResult = Await.result(updateFuture, 10.seconds)

    assert(
      updateResult.getMatchedCount == 1,
      s"insertRiskingOutcomeToBackEnd: no application matched for '$applicationReference'"
    )

  def insertRiskingOutcomeToAgentApplicationWithAmlsDetails(
    applicationReference: String,
    riskingCompletedDate: String,
    outcome: String,
    correctiveActionExpiryDate: String,
    fixes: Seq[Document]
  ): Unit =
    findBackEndApplicationByApplicationReference(applicationReference)
      .getOrElse(
        throw new AssertionError(
          s"No document found for applicationReference='$applicationReference' in agent-application collection"
        )
      )

    val riskingOutcomeEntity = Document(
      "fixes" -> fixes,
      "type" -> outcome
    )

    val riskingOutcomeApplication = Document(
      "riskingCompletedDate" -> riskingCompletedDate,
      "outcome" -> outcome,
      "correctiveActionExpiryDate" -> correctiveActionExpiryDate
    )

    val updateBackEnd = Updates.combine(
      Updates.set("applicationState", "RiskingCompleted"),
      Updates.set("riskingOutcomeApplication", riskingOutcomeApplication),
      Updates.set("riskingOutcomeEntity", riskingOutcomeEntity)
    )

    val updateBackEndFuture = backEndCollection
      .updateOne(equal("applicationReference", applicationReference), updateBackEnd)
      .toFuture()

    val backEndResult = Await.result(updateBackEndFuture, 10.seconds)

    assert(
      backEndResult.getMatchedCount == 1,
      s"insertRiskingOutcomeToAgentApplicationWithAmlsDetails: no application matched in agent-application for '$applicationReference'"
    )

  def insertRiskingOutcomeIndividualToBackEnd(
    applicationReference: String,
    riskingOutcomeType: String = "Approved",
    individualId: Option[String] = None,
    fixes: Seq[IndividualFix] = Seq.empty
  ): Unit =
    val riskingOutcomeIndividual = riskingOutcomeIndividualDocument(riskingOutcomeType, fixes)
    val applicationFilter = backEndIndividualApplicationFilter(applicationReference)

    val filter =
      individualId match
        case Some(id) =>
          and(
            applicationFilter,
            or(
              equal("id", id),
              equal("_id", id),
              equal("personReference", id),
              equal("individualReference", id)
            )
          )
        case None => applicationFilter

    val updateFuture = backEndIndividualCollection
      .updateMany(
        filter,
        Updates.set("riskingOutcomeIndividual", riskingOutcomeIndividual)
      )
      .toFuture()

    val updateResult = Await.result(updateFuture, 10.seconds)

    assert(
      updateResult.getMatchedCount >= 1,
      s"insertRiskingOutcomeIndividualToBackEnd: no individual(s) matched for applicationReference='$applicationReference'${individualId.map(id => s", individualId='$id'").getOrElse("")}"
    )

  def insertRiskingOutcomeIndividualByAgentApplicationId(
    applicationReference: String,
    riskingOutcomeType: String = "Approved",
    fixes: Seq[IndividualFix] = Seq.empty
  ): Unit =
    val agentApplicationId = backEndApplicationIdForApplicationReference(applicationReference)

    val update = Updates.set(
      "riskingOutcomeIndividual",
      riskingOutcomeIndividualDocument(riskingOutcomeType, fixes)
    )

    val resultFuture = backEndIndividualCollection
      .updateMany(equal("agentApplicationId", agentApplicationId), update)
      .toFuture()

    val result = Await.result(resultFuture, 10.seconds)

    assert(
      result.getMatchedCount >= 1,
      s"insertRiskingOutcomeIndividualByAgentApplicationId: no individual(s) matched for agentApplicationId='$agentApplicationId' from applicationReference='$applicationReference'"
    )

  def findBackEndIndividualsByApplicationReference(ref: String): Seq[Document] =
    val future = backEndIndividualCollection
      .find(backEndIndividualApplicationFilter(ref))
      .toFuture()

    Await.result(future, 10.seconds)

  def insertRiskingOutcomeIndividualByField(
    applicationReference: String,
    individualIdField: String,
    individualIdValue: String,
    riskingOutcomeType: String = "Approved",
    fixes: Seq[IndividualFix] = Seq.empty
  ): Unit =
    val applicationFilter = backEndIndividualApplicationFilter(applicationReference)
    val filterCombined = and(applicationFilter, equal(individualIdField, individualIdValue))
    val update = Updates.set("riskingOutcomeIndividual", riskingOutcomeIndividualDocument(riskingOutcomeType, fixes))

    val resultCombined = Await.result(
      backEndIndividualCollection
        .updateOne(filterCombined, update)
        .toFuture(),
      10.seconds
    )

    if resultCombined.getMatchedCount == 0 then
      val resultIdOnly = Await.result(
        backEndIndividualCollection
          .updateOne(equal(individualIdField, individualIdValue), update)
          .toFuture(),
        10.seconds
      )

      if resultIdOnly.getMatchedCount == 0 then
        val resultByApplication = Await.result(
          backEndIndividualCollection
            .updateMany(applicationFilter, update)
            .toFuture(),
          10.seconds
        )

        if resultByApplication.getMatchedCount == 0 then
          val doc = Document(
            individualIdField -> individualIdValue,
            "applicationReference" -> applicationReference,
            "agentApplicationId" -> backEndApplicationIdForApplicationReference(applicationReference),
            "personReference" -> individualIdValue,
            "riskingOutcomeIndividual" -> riskingOutcomeIndividualDocument(riskingOutcomeType, fixes)
          )

          val replaceRes = Await.result(
            backEndIndividualCollection
              .replaceOne(
                equal(individualIdField, individualIdValue),
                doc,
                ReplaceOptions().upsert(true)
              )
              .toFuture(),
            10.seconds
          )

          if replaceRes.getMatchedCount == 0 && replaceRes.getUpsertedId == null then
            throw new AssertionError(
              s"insertRiskingOutcomeIndividualByField: failed to upsert backend individual for $individualIdField='$individualIdValue' and applicationReference='$applicationReference'"
            )

  def insertRiskingOutcomeIndividualByObjectId(
    applicationReference: String,
    individualObjectIdHex: String,
    riskingOutcomeType: String = "Approved",
    fixes: Seq[IndividualFix] = Seq.empty
  ): Unit =
    val objId = new org.bson.types.ObjectId(individualObjectIdHex)
    val applicationFilter = backEndIndividualApplicationFilter(applicationReference)
    val update = Updates.set("riskingOutcomeIndividual", riskingOutcomeIndividualDocument(riskingOutcomeType, fixes))

    val resultCombined = Await.result(
      backEndIndividualCollection
        .updateOne(and(applicationFilter, equal("_id", objId)), update)
        .toFuture(),
      10.seconds
    )

    if resultCombined.getMatchedCount > 0 then
      return

    val resultObjIdOnly = Await.result(
      backEndIndividualCollection
        .updateOne(equal("_id", objId), update)
        .toFuture(),
      10.seconds
    )

    if resultObjIdOnly.getMatchedCount > 0 then
      return

    val resultIdString = Await.result(
      backEndIndividualCollection
        .updateOne(equal("_id", individualObjectIdHex), update)
        .toFuture(),
      10.seconds
    )

    if resultIdString.getMatchedCount > 0 then
      return

    val resultIdField = Await.result(
      backEndIndividualCollection
        .updateOne(equal("id", individualObjectIdHex), update)
        .toFuture(),
      10.seconds
    )

    if resultIdField.getMatchedCount > 0 then
      return

    val resultIndRef = Await.result(
      backEndIndividualCollection
        .updateOne(equal("individualReference", individualObjectIdHex), update)
        .toFuture(),
      10.seconds
    )

    if resultIndRef.getMatchedCount > 0 then
      return

    val resultByApplication = Await.result(
      backEndIndividualCollection
        .updateMany(applicationFilter, update)
        .toFuture(),
      10.seconds
    )

    if resultByApplication.getMatchedCount > 0 then
      return

    val upsertDoc = Document(
      "_id" -> objId,
      "applicationReference" -> applicationReference,
      "agentApplicationId" -> backEndApplicationIdForApplicationReference(applicationReference),
      "personReference" -> individualObjectIdHex,
      "riskingOutcomeIndividual" -> riskingOutcomeIndividualDocument(riskingOutcomeType, fixes)
    )

    val insertRes = Await.result(
      backEndIndividualCollection
        .insertOne(upsertDoc)
        .toFuture(),
      10.seconds
    )

    if insertRes.getInsertedId == null then
      val msg =
        s"insertRiskingOutcomeIndividualByObjectId: failed to insert backend individual for _id='$individualObjectIdHex' and applicationReference='$applicationReference'"

      throw new AssertionError(msg)

  def findRiskingIndividualsByApplicationReference(ref: String): Seq[Document] =
    val future = individualsCollection
      .find(equal("applicationReference", ref))
      .toFuture()

    Await.result(future, 10.seconds)

  def syncRiskingIndividualsToBackEnd(applicationReference: String): Unit =
    val riskingIndividuals = findRiskingIndividualsByApplicationReference(applicationReference)

    if riskingIndividuals.isEmpty then
      throw new AssertionError(s"syncRiskingIndividualsToBackEnd: no risking individuals found for $applicationReference")

    riskingIndividuals.foreach { ind =>
      val filter =
        ind.get("_id") match
          case Some(oid) if oid.isObjectId => equal("_id", oid.asObjectId().getValue)
          case Some(id) if id.isString => equal("_id", id.asString().getValue)
          case _ =>
            ind.get("id") match
              case Some(idv) if idv.isString => equal("id", idv.asString().getValue)
              case _ =>
                val name = documentStringValue(ind, "individualName")
                  .orElse(documentStringValue(ind, "firstName"))
                  .getOrElse("")

                and(backEndIndividualApplicationFilter(applicationReference), equal("individualName", name))

      val replacement = Document(ind.toJson())

      val replaceFuture = backEndIndividualCollection
        .replaceOne(
          filter,
          replacement,
          ReplaceOptions().upsert(true)
        )
        .toFuture()

      val replaceResult = Await.result(replaceFuture, 10.seconds)

      if replaceResult.getMatchedCount == 0 && replaceResult.getUpsertedId == null then
        println(
          s"[DEBUG] syncRiskingIndividualsToBackEnd: replaceOne did not match or upsert for filter=$filter; replacement=${replacement.toJson()}"
        )
    }

  private def backEndIndividualFilter(
    applicationReference: String,
    individual: Document
  ) =
    individual.get("_id") match
      case Some(oid) if oid.isObjectId => equal("_id", oid.asObjectId().getValue)
      case Some(id) if id.isString => equal("_id", id.asString().getValue)
      case _ =>
        documentStringValue(individual, "personReference") match
          case Some(personReference) => and(backEndIndividualApplicationFilter(applicationReference), equal("personReference", personReference))
          case None =>
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
        .updateOne(filter, Updates.set("riskingOutcomeIndividual", riskingOutcomeIndividualDocument(outcome)))
        .toFuture(),
      10.seconds
    )

    assert(
      updateResult.getMatchedCount == 1,
      s"updateRiskingOutcomeForBackEndIndividual: no individual matched filter '$filter' for applicationReference='$applicationReference'"
    )

  def insertRiskingOutcomeIndividualsToAgentApplication(
    applicationReference: String,
    outcomesByIndividualName: Map[String, IndividualRiskingOutcome],
    defaultOutcome: IndividualRiskingOutcome = IndividualRiskingOutcome()
  ): Unit =
    val individuals = findBackEndIndividualsByApplicationReference(applicationReference)

    if individuals.isEmpty then
      throw new AssertionError(
        s"insertRiskingOutcomeIndividualsToBackEndByName: no backend individuals found for applicationReference='$applicationReference'"
      )

    val matchedIndividualNames = scala.collection.mutable.Set.empty[String]

    individuals.foreach { individual =>
      val individualName = documentStringValue(individual, "individualName")

      val outcome = individualName
        .flatMap { name =>
          if outcomesByIndividualName.contains(name) then matchedIndividualNames += name
          outcomesByIndividualName.get(name)
        }
        .getOrElse(defaultOutcome)

      updateRiskingOutcomeForBackEndIndividual(
        applicationReference,
        individual,
        outcome
      )
    }

    val unmatchedIndividualNames = outcomesByIndividualName.keySet.diff(matchedIndividualNames.toSet)

    assert(
      unmatchedIndividualNames.isEmpty,
      s"insertRiskingOutcomeIndividualsToBackEndByName: no backend individual matched individualName(s): ${unmatchedIndividualNames.mkString(", ")}"
    )

  def insertRiskingOutcomeIndividual(
    applicationReference: String,
    riskingIndividual: Document,
    riskingOutcomeType: String = "Approved",
    fixes: Seq[IndividualFix] = Seq.empty
  ): Unit =
    riskingIndividual.get("id") match
      case Some(v) if v.isString =>
        insertRiskingOutcomeIndividualByField(
          applicationReference,
          "id",
          v.asString().getValue,
          riskingOutcomeType,
          fixes
        )

      case _ =>
        riskingIndividual.get("individualReference") match
          case Some(v2) if v2.isString =>
            insertRiskingOutcomeIndividualByField(
              applicationReference,
              "individualReference",
              v2.asString().getValue,
              riskingOutcomeType,
              fixes
            )

          case _ =>
            riskingIndividual.get("_id") match
              case Some(oid) if oid.isObjectId =>
                insertRiskingOutcomeIndividualByObjectId(
                  applicationReference,
                  oid.asObjectId().getValue.toHexString,
                  riskingOutcomeType,
                  fixes
                )

              case Some(id) if id.isString =>
                insertRiskingOutcomeIndividualToBackEnd(
                  applicationReference,
                  riskingOutcomeType = riskingOutcomeType,
                  individualId = Some(id.asString().getValue),
                  fixes = fixes
                )

              case _ =>
                insertRiskingOutcomeIndividualToBackEnd(
                  applicationReference,
                  riskingOutcomeType = riskingOutcomeType,
                  fixes = fixes
                )
