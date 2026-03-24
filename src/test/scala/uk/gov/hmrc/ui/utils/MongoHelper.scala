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
