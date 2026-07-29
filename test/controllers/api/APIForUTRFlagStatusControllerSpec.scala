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

package controllers.api

import base.SpecBase
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class APIForUTRFlagStatusControllerSpec extends SpecBase {

  "APIForUTRFlagStatusControllerSpec controller" must {

    "return true when haveUtrYesNo is true" in {

      val userAnswers = UserAnswers(
        "some-id",
        Json.obj(
          "haveUtrYesNo" -> true
        )
      )

      val application: Application = getApplication(userAnswers)

      when(sessionRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))

      val request = FakeRequest(GET, routes.APIForUTRFlagStatusController.getUTRFlag().url)

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsJson(result) mustBe Json.obj(
        "utrFlag" -> true
      )
      application.stop()
    }

    "return false when haveUtrYesNo is false" in {
      val userAnswers = UserAnswers(
        "some-id",
        Json.obj(
          "haveUtrYesNo" -> false
        )
      )

      val application: Application = getApplication(userAnswers)

      when(sessionRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))

      val request = FakeRequest(GET, routes.APIForUTRFlagStatusController.getUTRFlag().url)

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsJson(result) mustBe Json.obj(
        "utrFlag" -> false
      )
      application.stop()
    }

    "return false when haveUtrYesNo is missing" in {
      val userAnswers = UserAnswers("some-id", Json.obj())

      val application: Application = getApplication(userAnswers)

      when(sessionRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))

      val request = FakeRequest(GET, routes.APIForUTRFlagStatusController.getUTRFlag().url)

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsJson(result) mustBe Json.obj(
        "utrFlag" -> false
      )
      application.stop()
    }

    def getApplication(userAnswers: UserAnswers) =
      applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          Seq(
            bind[SessionRepository].toInstance(sessionRepository)
          )
        )
        .build()

  }

}
