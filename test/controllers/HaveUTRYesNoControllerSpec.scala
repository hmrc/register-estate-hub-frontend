/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import base.SpecBase
import config.annotations.EstateRegistration
import forms.YesNoFormProvider
import navigation.Navigator
import pages.HaveUTRYesNoPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.HaveUTRYesNoView

class HaveUTRYesNoControllerSpec extends SpecBase {

  val formProvider = new YesNoFormProvider()
  val form: Form[Boolean] = formProvider.withPrefix("haveUtrYesNo")

  lazy val haveUTRRoute: String = routes.HaveUTRYesNoController.onPageLoad().url

  "HaveUTR Controller" when {

    "org cred user" must {
      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Organisation)
          .overrides(bind[Navigator].qualifiedWith(classOf[EstateRegistration]).toInstance(fakeNavigator))
          .build()

        val request = FakeRequest(GET, haveUTRRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HaveUTRYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, isOrgCredUser = true)(request, messages).toString

        application.stop()
      }
    }

    "non-org cred user" must {
      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent)
          .overrides(bind[Navigator].qualifiedWith(classOf[EstateRegistration]).toInstance(fakeNavigator))
          .build()

        val request = FakeRequest(GET, haveUTRRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HaveUTRYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, isOrgCredUser = false)(request, messages).toString

        application.stop()
      }
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(HaveUTRYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, haveUTRRoute)

      val view = application.injector.instanceOf[HaveUTRYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), isOrgCredUser = true)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(Seq(
            bind[Navigator].qualifiedWith(classOf[EstateRegistration]).toInstance(fakeNavigator),
            bind[SessionRepository].toInstance(sessionRepository)
          ))
          .build()

      val request =
        FakeRequest(POST, haveUTRRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, haveUTRRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[HaveUTRYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, isOrgCredUser = true)(request, messages).toString

      application.stop()
    }

    "redirect SessionExpired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, haveUTRRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to the SessionExpired when valid data is submitted if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, haveUTRRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }


  }
}
