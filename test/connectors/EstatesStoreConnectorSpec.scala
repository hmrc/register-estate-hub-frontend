/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{get, okJson, urlEqualTo, _}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import models.CompletedTasks
import models.CompletedTasksResponse.InternalServerError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

class EstatesStoreConnectorSpec extends SpecBase with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  "estates store connector" must {

    "return OK with the current task status" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates-store.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesStoreConnector]

      val json = Json.parse(
        """
          |{
          |  "details": true,
          |  "personalRepresentative": true,
          |  "deceased": true,
          |  "yearsOfTaxLiability": true
          |}
          |""".stripMargin)

      server.stubFor(
        get(urlEqualTo("/estates-store/register/tasks"))
          .willReturn(okJson(json.toString))
      )

      val result = connector.getStatusOfTasks

      result.futureValue mustBe
        CompletedTasks(details = true, personalRepresentative = true, deceased = true, yearsOfTaxLiability = true)

      application.stop()
    }

    "return internal server error when a failure occurs" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates-store.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[EstatesStoreConnector]

      val json = Json.parse(
        """
          |{
          | "code": "INTERNAL_SERVER_ERROR",
          | "message": "Internal server error."
          |}
          |""".stripMargin)

      server.stubFor(
        get(urlEqualTo("/estates-store/register/tasks"))
          .willReturn(aResponse()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .withBody(json.toString())
          )
      )

      val result = connector.getStatusOfTasks

      result.futureValue mustBe
        InternalServerError

      application.stop()
    }

  }

}
