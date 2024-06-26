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

package controllers

import com.google.inject.Inject
import connectors.EstatesConnector
import controllers.actions.Actions
import handlers.ErrorHandler
import pages._
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session
import views.html.ConfirmationView

import scala.concurrent.ExecutionContext

class ConfirmationController @Inject()(
                                        val controllerComponents: MessagesControllerComponents,
                                        view: ConfirmationView,
                                        connector: EstatesConnector,
                                        actions: Actions,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext
) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad: Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      request.userAnswers.get(TRNPage).fold {
        logger.error(s"[Session ID: ${Session.id(hc)}] no TRN in user answers, cannot render confirmation")
        errorHandler.onServerError(request, new Exception("TRN is not available for completed estate."))
      }{
        trn =>
          connector.getPersonalRepName().map {
            name =>
              Ok(view(trn, name.name))
          }
      }
  }
}
