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

package controllers.registration_progress

import config.FrontendAppConfig
import models.TagStatus.InProgress
import models.{CompletedTasks, TagStatus}
import viewmodels.tasks.{EstateName, PersonWhoDied, PersonalRepresentative, YearsOfTaxLiability}
import viewmodels.{Link, Task}

trait TaskListSections {

  case class TaskList(mandatory: List[Task], other: List[Task]) {
    val isAbleToDeclare : Boolean = !(mandatory ::: other).exists(_.tag.contains(InProgress))
  }

  private lazy val notYetAvailable: String =
    config.featureUnavailableUrl

  val config: FrontendAppConfig

  private val estateDetailsRoute: String = {
    if (config.estateDetailsEnabled) {
      config.estateDetailsFrontendUrl
    } else {
      notYetAvailable
    }
  }

  private val personalRepRoute: String = {
    if (config.personalRepEnabled) {
      config.personalRepFrontendUrl
    } else {
      notYetAvailable
    }
  }

  private val deceasedPersonsRoute: String = {
    if (config.deceasedPersonsEnabled) {
      config.deceasedPersonsFrontendUrl
    } else {
      notYetAvailable
    }
  }

  private val registerTaxRoute: String = {
    if (config.registerTaxEnabled) {
      config.registerTaxFrontendUrl
    } else {
      notYetAvailable
    }
  }

  def generateTaskList(tasks: CompletedTasks, enableTaxLiability: Boolean): TaskList = {
    val mandatorySections = List(
      Task(
        Link(EstateName, estateDetailsRoute),
        Some(TagStatus.tagFor(tasks.details, config.estateDetailsEnabled))
      ),
      Task(
        Link(PersonalRepresentative, personalRepRoute),
        Some(TagStatus.tagFor(tasks.personalRepresentative, config.personalRepEnabled))
      ),
      Task(
        Link(PersonWhoDied, deceasedPersonsRoute),
        Some(TagStatus.tagFor(tasks.deceased, config.deceasedPersonsEnabled))
      )
    )

    val optionalSections = if (enableTaxLiability) {
      List(
        Task(
          Link(YearsOfTaxLiability, registerTaxRoute),
          Some(TagStatus.tagFor(tasks.yearsOfTaxLiability, config.registerTaxEnabled))
        )
      )
    } else {
      Nil
    }

    TaskList(mandatorySections, optionalSections)
  }

}
