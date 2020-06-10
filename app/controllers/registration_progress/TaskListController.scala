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

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.EstatesStoreConnector
import controllers.actions.Actions
import handlers.ErrorHandler
import models.CompletedTasks
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.TaskListView

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject()(
                                    actions: Actions,
                                    val controllerComponents: MessagesControllerComponents,
                                    val config: FrontendAppConfig,
                                    view: TaskListView,
                                    storeConnector: EstatesStoreConnector,
                                    errorHandler: ErrorHandler
                                  )(implicit ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport with TaskListSections {

  def onPageLoad(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      storeConnector.getStatusOfTasks flatMap {

        case _ @(tasks: CompletedTasks) =>
          val taskList = generateTaskList(tasks)

          // TODO: get estate name from register-estate-details-frontend user answers as Option[String]

          Future.successful(
            Ok(view(
            estateName = None,
            sections = taskList.tasks,
            isTaskListComplete = taskList.isAbleToDeclare
            ))
          )

        case e =>
          Logger.error(s"[TaskListController] unable to get tasks statuses due to error $e")
          errorHandler.onServerError(request, new Exception("Error while retrieving tasks statuses."))
      }
  }

}