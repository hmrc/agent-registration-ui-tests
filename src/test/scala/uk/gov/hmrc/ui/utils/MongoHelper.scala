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
import org.mongodb.scala.model.Updates.*
import scala.concurrent.Await
import scala.concurrent.duration.*
import org.bson.BsonDocument
import org.mongodb.scala.Document

object MongoHelper:

  private val client = MongoClient("mongodb://localhost:27017")
  private val database = client.getDatabase("agent-registration-risking")
  private val collection = database.getCollection("application-for-risking")

  def findByApplicationReference(ref: String): Option[Document] =
    val future = collection
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

  def firstIndividual(doc: Document): BsonDocument = doc.get("individuals")
    .map(_.asArray().getValues.get(0).asDocument())
    .getOrElse(throw new AssertionError("No individuals found"))

  def getFailures(doc: Document): Seq[BsonDocument] = doc.get("failures")
    .map(_.asArray().getValues.toArray.toSeq.map(_.asInstanceOf[org.bson.BsonValue].asDocument()))
    .getOrElse(throw new AssertionError("Field 'failures' not found"))

  /** Simulates the risking service setting status and failure codes on the record. Replaces the entire document to preserve the correct field ordering: ...
    * amlRegNumber, failures, individuals ...
    */
  def updateStatusWithFailures(
    ref: String,
    newStatus: String,
    failures: String
  ): Unit =
    val existing = findByApplicationReference(ref)
      .getOrElse(throw new AssertionError(s"No document found for applicationReference='$ref'"))

    val parsedFailures = org.bson.BsonArray.parse(failures)

    // Rebuild the document with fields in the correct order
    val ordered = Document(
      "_id" -> existing("_id"),
      "applicationReference" -> existing("applicationReference"),
      "status" -> newStatus,
      "createdAt" -> existing("createdAt"),
      "agentDetails" -> existing("agentDetails"),
      "applicantCredentials" -> existing("applicantCredentials"),
      "applicantGroupId" -> existing("applicantGroupId"),
      "applicantName" -> existing("applicantName"),
      "applicantPhone" -> existing("applicantPhone"),
      "applicantEmail" -> existing("applicantEmail"),
      "entitySafeId" -> existing("entitySafeId"),
      "entityType" -> existing("entityType"),
      "entityIdentifier" -> existing("entityIdentifier"),
      "vrns" -> existing("vrns"),
      "payeRefs" -> existing("payeRefs"),
      "amlSupervisoryBody" -> existing("amlSupervisoryBody"),
      "amlRegNumber" -> existing("amlRegNumber"),
      "failures" -> parsedFailures,
      "individuals" -> existing("individuals")
    )

    val future = collection
      .replaceOne(equal("applicationReference", ref), ordered)
      .toFuture()
    val result = Await.result(future, 10.seconds)
    assert(
      result.getMatchedCount == 1,
      s"updateStatusWithFailures: no document matched applicationReference='$ref'"
    )

  val nonFixableFailures: String = """[
      {"type":"_3._1"},
      {"type":"_3._2"},
      {"type":"_4._1"},
      {"type":"_4._2"},
      {"type":"_5._1","value":100.4},
      {"type":"_7"},
      {"type":"_8._1"},
      {"type":"_8._5"},
      {"type":"_8._6"},
      {"type":"_8._7"}
    ]"""

  def deleteByApplicationReference(ref: String): Unit =
    val future = collection
      .deleteOne(equal("applicationReference", ref))
      .toFuture()
    Await.result(future, 10.seconds)
