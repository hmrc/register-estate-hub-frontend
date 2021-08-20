/*
 * Copyright 2021 HM Revenue & Customs
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

package views

import controllers.routes
import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.HaveUTRYesNoView

class HaveUTRYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "haveUtrYesNo"

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix("haveUtrYesNo")

  val view: HaveUTRYesNoView = viewFor[HaveUTRYesNoView](Some(emptyUserAnswers))

  "HaveUTRYesNo view" when {

    "org cred user" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, isOrgCredUser = true)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, None, routes.HaveUTRYesNoController.onSubmit().url)

      behave like pageWithHint(form, applyView, s"$messageKeyPrefix.hint")

      behave like pageWithASubmitButton(applyView(form))
    }

    "non-org cred user" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, isOrgCredUser = false)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, None, routes.HaveUTRYesNoController.onSubmit().url)

      behave like pageWithASubmitButton(applyView(form))
    }

  }
}
