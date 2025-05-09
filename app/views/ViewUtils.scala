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

package views

import play.api.data.{Form, FormError}
import play.api.i18n.Messages
import viewmodels.RadioOption

object ViewUtils {

  def errorPrefix(form: Form[_])(implicit messages: Messages): String = {
    if (form.hasErrors || form.hasGlobalErrors) s"${messages("error.browser.title.prefix")} " else ""
  }

  def breadcrumbTitle(title: String)(implicit messages: Messages): String = {
    s"$title - ${messages("service.name")} - GOV.UK"
  }

  def errorHref(error: FormError, radioOptions: Seq[RadioOption] = Nil): String = {
    error.args match {
      case x if x.contains("day") || x.contains("month") || x.contains("year") =>
        s"${error.key}.${error.args.head}"
      case _ if error.message.toLowerCase.contains("yesno") =>
        s"${error.key}-yes"
      case _ if radioOptions.nonEmpty =>
        radioOptions.head.id
      case _ =>
        val isSingleDateField = isDateError(error.message) && !error.message.toLowerCase.contains("yesno")
        if (isDateError(error.key) || isSingleDateField) {
          s"${error.key}.day"
        } else {
          s"${error.key}"
        }
    }
  }

  private def isDateError(error: String): Boolean = {
    error.toLowerCase.contains("date") || error.toLowerCase.contains("when")
  }
}
