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

import org.mongodb.scala.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Updates
import scala.concurrent.Await
import scala.concurrent.duration.*
import org.bson.BsonDocument
import org.mongodb.scala.Document

object MongoHelper:

  private val client = MongoClient("mongodb://localhost:27017")
  private val backEndDatabase = client.getDatabase("agent-registration")
  private val backEndCollection = backEndDatabase.getCollection("agent-application")

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

  /** Simulates the risking service by setting riskingFileName and entityRiskingResult on the application-for-risking record. When withIndividualFailures is
    * true, sets all NonFixableOutcomeListForIndividualFailures codes on each individual-for-risking record; otherwise sets empty failures. Uses replaceOne to
    * preserve correct Mongo field ordering: ..., isEmailSent, riskingFileName, entityRiskingResult.
    */
  def simulateNonFixableRiskingOutcome(
    applicationReference: String,
    withEntityFailures: Boolean = true,
    withIndividualFailures: Boolean = false
  ): Unit =
    val existing = findByApplicationReference(applicationReference)
      .getOrElse(throw new AssertionError(s"No document found for applicationReference='$applicationReference'"))

    val now = java.time.Instant.now().toString
    val entityRiskingResult = BsonDocument.parse(
      s"""{"failures":[
         |  {"type":"_7"},
         |  {"type":"_3._1"},
         |  {"type":"_4._1"},
         |  {"type":"_5._1","value":150},
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
             |  {"type":"_5._1","value":150},
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

    val ordered = Document(
      "_id" -> existing("_id"),
      "applicationReference" -> existing("applicationReference"),
      "applicationData" -> existing("applicationData"),
      "createdAt" -> existing("createdAt"),
      "lastUpdatedAt" -> existing("lastUpdatedAt"),
      "isSubscribed" -> existing("isSubscribed"),
      "isEmailSent" -> existing("isEmailSent"),
      "riskingFileName" -> "any-old.txt",
      "entityRiskingResult" -> entityRiskingResult,
      "overallStatus" -> Document("riskingOutcome" -> "FailedNonFixable", "emailsProcessed" -> true)
    )

    val appFuture = collection
      .replaceOne(equal("applicationReference", applicationReference), ordered)
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

  def getIndividualRiskingFailures(doc: Document): Seq[BsonDocument] = doc.get("individualRiskingResult")
    .map(_.asDocument().get("failures").asArray().getValues.toArray.toSeq
      .map(_.asInstanceOf[org.bson.BsonValue].asDocument()))
    .getOrElse(throw new AssertionError("Field 'individualRiskingResult' not found"))

  def deleteByApplicationReference(ref: String): Unit =
    val future = collection
      .deleteOne(equal("applicationReference", ref))
      .toFuture()
    Await.result(future, 10.seconds)
