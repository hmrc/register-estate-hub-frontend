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

package views

import forms.DeclarationFormProvider
import models.Declaration
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import views.behaviours.QuestionViewBehaviours
import views.html.DeclarationView

class DeclarationViewSpec extends QuestionViewBehaviours[Declaration] {

  val messageKeyPrefix = "declaration"

  val form = new DeclarationFormProvider()()

  "Declaration view for Org or Agent" must {

    val view = viewFor[DeclarationView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, AffinityGroup.Organisation)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      None,
      controllers.routes.ConfirmationController.onPageLoad().url,
      "firstName", "middleName", "lastName"
    )
  }

  "render declaration warning for an Org" in {
    val view = viewFor[DeclarationView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, AffinityGroup.Organisation)(fakeRequest, messages)

    val doc = asDocument(applyView(form))
    assertContainsText(doc, "I confirm that I have taken all reasonable steps to obtain up to " +
      "date and accurate information for this registration. I understand " +
      "that if I knowingly provide false information and cannot demonstrate that I have taken all " +
      "reasonable steps, I could be subject to penalties.")
  }

  "render declaration warning for an Agent" in {
    val view = viewFor[DeclarationView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, AffinityGroup.Agent)(fakeRequest, messages)

    val doc = asDocument(applyView(form))
    assertContainsText(doc, "I confirm that my client has taken all reasonable steps to obtain up " +
      "to date and accurate information for this registration. I understand that if my client knowingly " +"" +
      "provides false information and cannot demonstrate that they have taken all reasonable steps, " +
      "they could be subject to penalties.")
  }

}
