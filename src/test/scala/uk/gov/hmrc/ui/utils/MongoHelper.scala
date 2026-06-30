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
    .map(_.asDocument().get("failures").asArray().getValues.toArray.toSeq
      .map(_.asInstanceOf[org.bson.BsonValue].asDocument()))
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

    // Use updateOne to set only the fields we need to change to avoid touching optional fields
    val update1 = Updates.combine(
      Updates.set("riskingFileName", "any-old.txt"),
      Updates.set("entityRiskingResult", entityRiskingResult),
      Updates.set("overallStatus", Document("riskingOutcome" -> "FailedNonFixable", "emailsProcessed" -> true))
    )

    val appFuture = collection
      .updateOne(equal("applicationReference", applicationReference), update1)
      .toFuture()
    val appResult = Await.result(appFuture, 10.seconds)
    assert(appResult.getMatchedCount == 1, s"simulateNonFixableRiskingOutcome: no application matched for '$applicationReference'")

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
      .getOrElse(throw new AssertionError(s"No document found for applicationReference='${applicationReference}'"))

    // Parse provided JSON into a BsonDocument and use it directly
    val entityRiskingResult = BsonDocument.parse(entityRiskingResultJson)

    // Update only the necessary fields instead of replacing the whole document
    val update2 = Updates.combine(
      Updates.set("riskingFileName", "any-old.txt"),
      Updates.set("entityRiskingResult", entityRiskingResult),
      Updates.set("overallStatus", Document("riskingOutcome" -> overallOutcome, "emailsProcessed" -> true))
    )

    val appFuture = collection
      .updateOne(equal("applicationReference", applicationReference), update2)
      .toFuture()
    val appResult = Await.result(appFuture, 10.seconds)
    assert(appResult.getMatchedCount == 1, s"insertEntityRiskingResult: no application matched for '${applicationReference}'")

  def getIndividualRiskingFailures(doc: Document): Seq[BsonDocument] = doc.get("individualRiskingResult")
    .map(_.asDocument().get("failures").asArray().getValues.toArray.toSeq
      .map(_.asInstanceOf[org.bson.BsonValue].asDocument()))
    .getOrElse(throw new AssertionError("Field 'individualRiskingResult' not found"))

  def generateRandomObjectId(): String = new org.bson.types.ObjectId().toHexString

  def insertAgentAssuranceRecord(
    oid: String,
    key: String,
    value: String
  ): String =
    val objId = new org.bson.types.ObjectId(oid)
    // remove any existing document with the same _id to make test runs idempotent
    val deleteFuture = agentAssuranceCollection.deleteOne(equal("_id", objId)).toFuture()
    Await.result(deleteFuture, 10.seconds)

    val doc = Document(
      "_id" -> objId,
      "key" -> key,
      "value" -> value
    )

    val insertFuture = agentAssuranceCollection.insertOne(doc).toFuture()
    Await.result(insertFuture, 10.seconds)

    value

  def insertRiskingOutcomeToAgentApplication(
    applicationReference: String,
    riskingCompletedDate: String,
    outcome: String,
    correctiveActionExpiryDate: String,
    fixes: Seq[String] = Seq("EntityFix._4._2")
  ): Unit =
    findBackEndApplicationByApplicationReference(applicationReference)
      .getOrElse(throw new AssertionError(s"No document found for applicationReference='$applicationReference' in agent-application collection"))

    // Build the fixes array from the provided sequence
    val fixesArray = fixes.map(fix => Document("type" -> fix))

    val riskingOutcomeEntity = Document(
      "fixes" -> fixesArray,
      "type" -> outcome
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
    assert(updateResult.getMatchedCount == 1, s"insertRiskingOutcomeToBackEnd: no application matched for '$applicationReference'")

  def insertRiskingOutcomeToAgentApplicationWithAmlsDetails(
    applicationReference: String,
    riskingCompletedDate: String,
    outcome: String,
    correctiveActionExpiryDate: String,
    fixes: Seq[Document]
  ): Unit =
    findBackEndApplicationByApplicationReference(applicationReference)
      .getOrElse(throw new AssertionError(s"No document found for applicationReference='$applicationReference' in agent-application collection"))

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
    individualId: Option[String] = None
  ): Unit =
    val riskingOutcomeIndividual = Document(
      "type" -> riskingOutcomeType
    )

    val filter =
      individualId match
        case Some(id) =>
          and(
            equal("applicationReference", applicationReference),
            equal("id", id)
          )
        case None => equal("applicationReference", applicationReference)

    val updateFuture = backEndIndividualCollection
      .updateMany(
        filter,
        Updates.set("riskingOutcomeIndividual", riskingOutcomeIndividual)
      )
      .toFuture()

    val updateResult = Await.result(updateFuture, 10.seconds)
    assert(
      updateResult.getModifiedCount >= 1,
      s"insertRiskingOutcomeIndividualToBackEnd: no individual(s) matched for applicationReference='$applicationReference'${individualId.map(id => s", individualId='$id'").getOrElse("")}"
    )

  def insertRiskingOutcomeIndividualByAgentApplicationId(
    applicationReference: String,
    riskingOutcomeType: String = "Approved"
  ): Unit =
    val appDoc = findBackEndApplicationByApplicationReference(applicationReference)
      .getOrElse(throw new AssertionError(s"insertRiskingOutcomeIndividualByAgentApplicationId: no agent-application found for '$applicationReference'"))

    val agentApplicationId =
      appDoc.get("_id") match
        case Some(v) if v.isObjectId => v.asObjectId().getValue.toHexString
        case Some(v) if v.isString => v.asString().getValue
        case _ => throw new AssertionError(s"insertRiskingOutcomeIndividualByAgentApplicationId: application '$applicationReference' has no usable _id")

    val update = Updates.set("riskingOutcomeIndividual", Document("type" -> riskingOutcomeType))
    val resultFuture = backEndIndividualCollection
      .updateMany(equal("agentApplicationId", agentApplicationId), update)
      .toFuture()
    val result = Await.result(resultFuture, 10.seconds)

  def findBackEndIndividualsByApplicationReference(ref: String): Seq[Document] =
    val future = backEndIndividualCollection
      .find(equal("applicationReference", ref))
      .toFuture()
    Await.result(future, 10.seconds)

  def insertRiskingOutcomeIndividualByField(
    applicationReference: String,
    individualIdField: String,
    individualIdValue: String,
    riskingOutcomeType: String = "Approved"
  ): Unit =
    val filterCombined = and(equal("applicationReference", applicationReference), equal(individualIdField, individualIdValue))
    val update = Updates.set("riskingOutcomeIndividual", Document("type" -> riskingOutcomeType))

    val resultCombined = Await.result(backEndIndividualCollection.updateOne(filterCombined, update).toFuture(), 10.seconds)
    if (resultCombined.getMatchedCount == 0) then
      // Try updating by identifier only (some backend individual docs may not contain applicationReference)
      val resultIdOnly = Await.result(backEndIndividualCollection.updateOne(equal(individualIdField, individualIdValue), update).toFuture(), 10.seconds)
      if (resultIdOnly.getMatchedCount == 0) then
        // Try updating any existing backend individual(s) for this application instead of
        // inserting a new doc; a new doc without personReference would collide with the existing
        // individual on the unique personReference index (-> E11000 duplicate key null).
        val resultByAppRef = Await.result(
          backEndIndividualCollection.updateMany(equal("applicationReference", applicationReference), update).toFuture(),
          10.seconds
        )
        if (resultByAppRef.getMatchedCount == 0) then
          // Nothing matched; upsert a minimal backend individual document so tests can proceed.
          // Include a unique, non-null personReference to avoid colliding on the unique index.
          val doc = Document(
            individualIdField -> individualIdValue,
            "applicationReference" -> applicationReference,
            "personReference" -> individualIdValue,
            "riskingOutcomeIndividual" -> Document("type" -> riskingOutcomeType)
          )
          import org.mongodb.scala.model.ReplaceOptions
          val replaceFilter = equal(individualIdField, individualIdValue)
          val replaceRes = Await.result(
            backEndIndividualCollection.replaceOne(
              replaceFilter,
              doc,
              ReplaceOptions().upsert(true)
            ).toFuture(),
            10.seconds
          )
          if (replaceRes.getMatchedCount == 0 && replaceRes.getUpsertedId == null) then
            throw new AssertionError(
              s"insertRiskingOutcomeIndividualByField: failed to upsert backend individual for $individualIdField='$individualIdValue' and applicationReference='$applicationReference'"
            )

  def insertRiskingOutcomeIndividualByObjectId(
    applicationReference: String,
    individualObjectIdHex: String,
    riskingOutcomeType: String = "Approved"
  ): Unit =
    val objId = new org.bson.types.ObjectId(individualObjectIdHex)
    val update = Updates.set("riskingOutcomeIndividual", Document("type" -> riskingOutcomeType))

    // 1) Try applicationReference + ObjectId
    val resultCombined = Await.result(
      backEndIndividualCollection.updateOne(and(equal("applicationReference", applicationReference), equal("_id", objId)), update).toFuture(),
      10.seconds
    )
    if (resultCombined.getMatchedCount > 0)
      return

    // 2) Try _id as ObjectId only
    val resultObjIdOnly = Await.result(backEndIndividualCollection.updateOne(equal("_id", objId), update).toFuture(), 10.seconds)
    if (resultObjIdOnly.getMatchedCount > 0)
      return

    // 3) Try _id stored as string (some backends store hex string instead of ObjectId)
    val resultIdString = Await.result(backEndIndividualCollection.updateOne(equal("_id", individualObjectIdHex), update).toFuture(), 10.seconds)
    if (resultIdString.getMatchedCount > 0)
      return

    // 4) Try matching by id field
    val resultIdField = Await.result(backEndIndividualCollection.updateOne(equal("id", individualObjectIdHex), update).toFuture(), 10.seconds)
    if (resultIdField.getMatchedCount > 0)
      return

    // 5) Try matching by individualReference field
    val resultIndRef = Await.result(backEndIndividualCollection.updateOne(equal("individualReference", individualObjectIdHex), update).toFuture(), 10.seconds)
    if (resultIndRef.getMatchedCount > 0)
      return

    // 6) Nothing matched by id; update the existing backend individual(s) for this
    // application instead of inserting a new doc (a new doc without personReference would
    // collide with the existing individual on the unique personReference index -> E11000).
    val resultByAppRef = Await.result(
      backEndIndividualCollection.updateMany(equal("applicationReference", applicationReference), update).toFuture(),
      10.seconds
    )
    if (resultByAppRef.getMatchedCount > 0)
      return

    // 7) Nothing exists; insert a minimal backend individual doc. Include a unique, non-null
    // personReference so we don't collide with other docs on the unique personReference index.
    val upsertDoc = Document(
      "_id" -> objId,
      "applicationReference" -> applicationReference,
      "personReference" -> individualObjectIdHex,
      "riskingOutcomeIndividual" -> Document("type" -> riskingOutcomeType)
    )
    val insertRes = Await.result(backEndIndividualCollection.insertOne(upsertDoc).toFuture(), 10.seconds)
    if (insertRes.getInsertedId == null) then
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

    import org.mongodb.scala.model.ReplaceOptions

    riskingIndividuals.foreach { ind =>
      // Choose a filter to upsert: prefer _id (ObjectId), otherwise id string, otherwise match by applicationReference + name
      val filter =
        ind.get("_id") match
          case Some(oid) if oid.isObjectId => equal("_id", oid.asObjectId().getValue)
          case _ =>
            ind.get("id") match
              case Some(idv) if idv.isString => equal("id", idv.asString().getValue)
              case _ =>
                and(equal("applicationReference", applicationReference), equal("firstName", ind.get("firstName").map(_.asString().getValue).getOrElse("")))

      // Replace (upsert) the backend individual doc with the risking individual document.
      // Convert via JSON to ensure nested Option/Some values from Scala are not preserved in the
      // replacement document (avoid runtime class cast issues).
      val replacement = Document(ind.toJson())
      val replaceFuture = backEndIndividualCollection.replaceOne(
        filter,
        replacement,
        ReplaceOptions().upsert(true)
      ).toFuture()
      val replaceResult = Await.result(replaceFuture, 10.seconds)
      if (replaceResult.getMatchedCount == 0 && replaceResult.getUpsertedId == null) then
        println(s"[DEBUG] syncRiskingIndividualsToBackEnd: replaceOne did not match or upsert for filter=$filter; replacement=${replacement.toJson()}")
    }

  def insertRiskingOutcomeIndividual(
    applicationReference: String,
    riskingIndividual: Document,
    riskingOutcomeType: String = "Approved"
  ): Unit =
    riskingIndividual.get("id") match
      case Some(v) if v.isString =>
        insertRiskingOutcomeIndividualByField(
          applicationReference,
          "id",
          v.asString().getValue,
          riskingOutcomeType
        )
      case _ =>
        riskingIndividual.get("individualReference") match
          case Some(v2) if v2.isString =>
            insertRiskingOutcomeIndividualByField(
              applicationReference,
              "individualReference",
              v2.asString().getValue,
              riskingOutcomeType
            )
          case _ =>
            riskingIndividual.get("_id") match
              case Some(oid) if oid.isObjectId =>
                insertRiskingOutcomeIndividualByObjectId(
                  applicationReference,
                  oid.asObjectId().getValue.toHexString,
                  riskingOutcomeType
                )
              case _ => insertRiskingOutcomeIndividualToBackEnd(applicationReference, riskingOutcomeType = riskingOutcomeType)
