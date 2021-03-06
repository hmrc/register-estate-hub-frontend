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

package utils

import com.google.inject.Inject
import org.joda.time.{LocalDate => JodaDate}
import play.api.i18n.Messages
import uk.gov.hmrc.play.language.LanguageUtils

import java.time.{LocalDateTime, LocalDate => JavaDate}

class DateFormatter @Inject()(languageUtils: LanguageUtils) {

  def formatDate(dateTime: LocalDateTime)(implicit messages: Messages): String = {
    formatDate(dateTime.toLocalDate)
  }

  def formatDate(date: JodaDate)(implicit messages: Messages): String = {
    formatDate(JavaDate.of(date.getYear, date.getMonthOfYear, date.getDayOfMonth))
  }

  private def formatDate(date: JavaDate)(implicit messages: Messages): String = {
    languageUtils.Dates.formatDate(date)
  }

}
