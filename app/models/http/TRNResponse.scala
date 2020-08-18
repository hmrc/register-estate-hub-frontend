/*
 * Copyright 2020 HM Revenue & Customs
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

package models.http

import play.api.Logger
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

sealed trait DeclarationResponse

final case class TRNResponse(trn : String) extends DeclarationResponse

object TRNResponse {

  implicit val formats : Format[TRNResponse] = Json.format[TRNResponse]

}

object DeclarationResponse {

  implicit object RegistrationResponseFormats extends Reads[DeclarationResponse] {

    override def reads(json: JsValue): JsResult[DeclarationResponse] = json.validate[TRNResponse]

  }

  case object AlreadyRegistered extends DeclarationResponse
  case object InternalServerError extends DeclarationResponse

  implicit lazy val httpReads: HttpReads[DeclarationResponse] =
    new HttpReads[DeclarationResponse] {
      override def read(method: String, url: String, response: HttpResponse): DeclarationResponse = {
        Logger.info(s"[DeclarationResponse] response status received from estates api: ${response.status}")

        response.status match {
          case OK =>
            response.json.as[TRNResponse]
          case CONFLICT =>
            AlreadyRegistered
          case _ =>
            InternalServerError
        }
      }
    }

}
