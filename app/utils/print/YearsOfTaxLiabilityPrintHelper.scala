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

package utils.print

import javax.inject.Inject
import models._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.time.TaxYear
import utils.DateFormatter
import utils.print.CheckAnswersFormatters.yesOrNo
import viewmodels.{AnswerRow, AnswerSection}

class YearsOfTaxLiabilityPrintHelper @Inject()(dateFormatter: DateFormatter) {

  def apply(yearReturns: List[YearReturnType])(implicit messages: Messages): Seq[AnswerSection] = {

    def rows(yearReturn: YearReturnType): Seq[AnswerRow] = {
      val taxYear = taxYearStartAndEndDate(yearReturn.taxReturnYear)
      Seq(
        yesNoQuestion(value = true, "taxLiability.neededToPayTax", taxYear._1, taxYear._2),
        yesNoQuestion(value = false, "taxLiability.wasTaxDeclared", taxYear._1, taxYear._2)
      )
    }

    yearReturns.zipWithIndex.foldLeft(Seq[AnswerSection]()) {
      case (acc, (yearReturn, index)) =>
        val taxYear = taxYearStartAndEndDate(yearReturn.taxReturnYear)
        acc :+ AnswerSection(
          headingKey = if (index == 0) Some("taskList.yearsOfTaxLiability.label") else None,
          rows = rows(yearReturn),
          subHeading = Some(messages("taskList.yearOfTaxLiability.label", taxYear._1, taxYear._2))
        )
    }
  }

  private def taxYearStartAndEndDate(taxReturnYear: String): (String, String) = {

    def taxReturnYearToInt(taxReturnYear: String): Int = {
      val taxReturnYearFull = s"20$taxReturnYear" // TODO - not particularly robust. Could we figure out from the current year if this is 20, 21 etc.?
      taxReturnYearFull.toInt
    }
    val endYear: Int = taxReturnYearToInt(taxReturnYear)
    val taxYear = TaxYear(startYear = endYear - 1)
    (dateFormatter.formatDate(taxYear.starts), dateFormatter.formatDate(taxYear.finishes))

  }

  private def yesNoQuestion(value: Boolean,
                            labelKey: String,
                            fromDate: String,
                            toDate: String)(implicit messages: Messages): AnswerRow = {
    AnswerRow(
      HtmlFormat.escape(messages(s"$labelKey.checkYourAnswersLabel", fromDate, toDate)),
      yesOrNo(value)
    )
  }
}
