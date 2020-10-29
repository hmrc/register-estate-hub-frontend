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
import connectors.{EstatesConnector, EstatesStoreConnector}
import controllers.actions.Actions
import handlers.ErrorHandler
import models.{CompletedTasks, CompletedTasksResponse, UserAnswers}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.TaskListView

import scala.concurrent.ExecutionContext

class TaskListController @Inject()(
                                    actions: Actions,
                                    val controllerComponents: MessagesControllerComponents,
                                    val config: FrontendAppConfig,
                                    view: TaskListView,
                                    connector: EstatesConnector,
                                    storeConnector: EstatesStoreConnector,
                                    errorHandler: ErrorHandler,
                                    repository: SessionRepository
                                  )(implicit ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport with TaskListSections {

  private val logger: Logger = Logger(getClass)
  
  def onPageLoad(): Action[AnyContent] = actions.authWithSession.async {
    implicit request =>

      def continueOrCreateNewSession =
        request.userAnswers.getOrElse(UserAnswers(request.internalId))

      def getEstateName =  connector.getEstateName()
      def getIsLiableForTax =  connector.getIsLiableForTax()

      for {
       _ <- repository.set(continueOrCreateNewSession)
       estateName <- getEstateName
       isLiableForTax <- getIsLiableForTax
        tasks <- storeConnector.getStatusOfTasks
      } yield {
        tasks match {
          case l @ CompletedTasks(_, _, _, _) =>
            val taskList = generateTaskList(l, isLiableForTax)

              Ok(view(
                estateName = estateName,
                sections = taskList.mandatory ++ taskList.other,
                isTaskListComplete = taskList.isAbleToDeclare,
                affinityGroup = request.affinityGroup))

          case CompletedTasksResponse.InternalServerError =>
            logger.error(s"[TaskListController] unable to get tasks statuses")
            InternalServerError(errorHandler.internalServerErrorTemplate)
        }
      }
  }

}
