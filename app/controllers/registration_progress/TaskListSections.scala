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

package controllers.registration_progress

import config.FrontendAppConfig
import models.Tag.InProgress
import models.{CompletedTasks, Tag}
import viewmodels.tasks.{DeceasedPersons, EstateDetails, PersonalRep}
import viewmodels.{Link, Task}

trait TaskListSections {

  case class TaskList(tasks: List[Task]) {
    val isAbleToDeclare: Boolean = !tasks.exists(_.tag.contains(InProgress))
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

  def generateTaskList(tasks: CompletedTasks): TaskList = {
    val sections = List(
      Task(
        Link(EstateDetails, estateDetailsRoute),
        Some(Tag.tagFor(tasks.details, config.estateDetailsEnabled))
      ),
      Task(
        Link(PersonalRep, personalRepRoute),
        Some(Tag.tagFor(tasks.personalRepresentative, config.personalRepEnabled))
      ),
      Task(
        Link(DeceasedPersons, deceasedPersonsRoute),
        Some(Tag.tagFor(tasks.deceased, config.deceasedPersonsEnabled))
      )
    )

    TaskList(sections)
  }

}